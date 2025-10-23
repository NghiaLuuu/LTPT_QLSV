package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.LopHocDTO;
import iuh.fit.se.common.model.Khoa;
import iuh.fit.se.common.model.LopHoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Lớp học hành chính
 */
public class LopHocService {
    private static final Logger logger = LoggerFactory.getLogger(LopHocService.class);
    private static LopHocService instance;
    private final EntityManagerFactory emf;

    private LopHocService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized LopHocService getInstance() {
        if (instance == null) {
            instance = new LopHocService();
        }
        return instance;
    }

    public LopHocDTO addLopHoc(LopHocDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
            if (khoa == null) {
                throw new RuntimeException("Không tìm thấy khoa: " + dto.getMaKhoa());
            }

            LopHoc lopHoc = new LopHoc(dto.getMaLop(), dto.getTenLop(), dto.getNienKhoa());
            lopHoc.setKhoa(khoa);
            em.persist(lopHoc);

            em.getTransaction().commit();
            logger.info("Added LopHoc: {}", dto.getMaLop());

            return toDTO(lopHoc);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error adding LopHoc", e);
            throw new RuntimeException("Lỗi thêm lớp học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public LopHocDTO updateLopHoc(LopHocDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LopHoc lopHoc = em.find(LopHoc.class, dto.getMaLop());
            if (lopHoc == null) {
                throw new RuntimeException("Không tìm thấy lớp: " + dto.getMaLop());
            }

            lopHoc.setTenLop(dto.getTenLop());
            lopHoc.setNienKhoa(dto.getNienKhoa());

            if (dto.getMaKhoa() != null) {
                Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
                if (khoa != null) {
                    lopHoc.setKhoa(khoa);
                }
            }

            em.merge(lopHoc);
            em.getTransaction().commit();

            logger.info("Updated LopHoc: {}", dto.getMaLop());
            return toDTO(lopHoc);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error updating LopHoc", e);
            throw new RuntimeException("Lỗi cập nhật lớp học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public boolean deleteLopHoc(String maLop) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LopHoc lopHoc = em.find(LopHoc.class, maLop);
            if (lopHoc == null) {
                return false;
            }

            if (!lopHoc.getDsSinhVien().isEmpty()) {
                throw new RuntimeException("Không thể xóa lớp đã có sinh viên");
            }

            em.remove(lopHoc);
            em.getTransaction().commit();

            logger.info("Deleted LopHoc: {}", maLop);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting LopHoc", e);
            throw new RuntimeException("Lỗi xóa lớp học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<LopHocDTO> getAllLopHocByKhoa(String maKhoa) {
        EntityManager em = emf.createEntityManager();
        try {
            List<LopHoc> lopHocs = em.createQuery(
                "SELECT l FROM LopHoc l WHERE l.khoa.maKhoa = :maKhoa ORDER BY l.maLop", LopHoc.class)
                .setParameter("maKhoa", maKhoa)
                .getResultList();
            return lopHocs.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<LopHocDTO> getAllLopHoc() {
        EntityManager em = emf.createEntityManager();
        try {
            List<LopHoc> lopHocs = em.createQuery("SELECT l FROM LopHoc l ORDER BY l.maLop", LopHoc.class)
                    .getResultList();
            return lopHocs.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private LopHocDTO toDTO(LopHoc lopHoc) {
        return new LopHocDTO(
            lopHoc.getMaLop(),
            lopHoc.getTenLop(),
            lopHoc.getNienKhoa(),
            lopHoc.getKhoa() != null ? lopHoc.getKhoa().getMaKhoa() : null,
            lopHoc.getKhoa() != null ? lopHoc.getKhoa().getTenKhoa() : null
        );
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

