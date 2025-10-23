package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.GiangVienDTO;
import iuh.fit.se.common.model.GiangVien;
import iuh.fit.se.common.model.Khoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Giảng viên
 */
public class GiangVienService {
    private static final Logger logger = LoggerFactory.getLogger(GiangVienService.class);
    private static GiangVienService instance;
    private final EntityManagerFactory emf;

    private GiangVienService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized GiangVienService getInstance() {
        if (instance == null) {
            instance = new GiangVienService();
        }
        return instance;
    }

    public GiangVienDTO addGiangVien(GiangVienDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
            if (khoa == null) {
                throw new RuntimeException("Không tìm thấy khoa: " + dto.getMaKhoa());
            }

            GiangVien gv = new GiangVien(dto.getMaGV(), dto.getHoTen(), dto.getHocVi());
            gv.setKhoa(khoa);
            em.persist(gv);

            em.getTransaction().commit();
            logger.info("Added GiangVien: {}", dto.getMaGV());

            return toDTO(gv);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error adding GiangVien", e);
            throw new RuntimeException("Lỗi thêm giảng viên: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public GiangVienDTO updateGiangVien(GiangVienDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            GiangVien gv = em.find(GiangVien.class, dto.getMaGV());
            if (gv == null) {
                throw new RuntimeException("Không tìm thấy giảng viên: " + dto.getMaGV());
            }

            gv.setHoTen(dto.getHoTen());
            gv.setHocVi(dto.getHocVi());

            if (dto.getMaKhoa() != null) {
                Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
                if (khoa != null) {
                    gv.setKhoa(khoa);
                }
            }

            em.merge(gv);
            em.getTransaction().commit();

            logger.info("Updated GiangVien: {}", dto.getMaGV());
            return toDTO(gv);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error updating GiangVien", e);
            throw new RuntimeException("Lỗi cập nhật giảng viên: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public boolean deleteGiangVien(String maGV) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            GiangVien gv = em.find(GiangVien.class, maGV);
            if (gv == null) {
                return false;
            }

            if (!gv.getDsLopHocPhan().isEmpty()) {
                throw new RuntimeException("Không thể xóa giảng viên đang có lớp học phần");
            }

            em.remove(gv);
            em.getTransaction().commit();

            logger.info("Deleted GiangVien: {}", maGV);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting GiangVien", e);
            throw new RuntimeException("Lỗi xóa giảng viên: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public GiangVienDTO getGiangVienById(String maGV) {
        EntityManager em = emf.createEntityManager();
        try {
            GiangVien gv = em.find(GiangVien.class, maGV);
            return gv != null ? toDTO(gv) : null;
        } finally {
            em.close();
        }
    }

    public List<GiangVienDTO> getAllGiangVienByKhoa(String maKhoa) {
        EntityManager em = emf.createEntityManager();
        try {
            List<GiangVien> gvList = em.createQuery(
                "SELECT g FROM GiangVien g WHERE g.khoa.maKhoa = :maKhoa ORDER BY g.maGV", GiangVien.class)
                .setParameter("maKhoa", maKhoa)
                .getResultList();
            return gvList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<GiangVienDTO> getAllGiangVien() {
        EntityManager em = emf.createEntityManager();
        try {
            List<GiangVien> gvList = em.createQuery("SELECT g FROM GiangVien g ORDER BY g.maGV", GiangVien.class)
                    .getResultList();
            return gvList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private GiangVienDTO toDTO(GiangVien gv) {
        return new GiangVienDTO(
            gv.getMaGV(),
            gv.getHoTen(),
            gv.getHocVi(),
            gv.getKhoa() != null ? gv.getKhoa().getMaKhoa() : null,
            gv.getKhoa() != null ? gv.getKhoa().getTenKhoa() : null
        );
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

