package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.HocKyDTO;
import iuh.fit.se.common.model.HocKy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Học kỳ
 */
public class HocKyService {
    private static final Logger logger = LoggerFactory.getLogger(HocKyService.class);
    private static HocKyService instance;
    private final EntityManagerFactory emf;

    private HocKyService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized HocKyService getInstance() {
        if (instance == null) {
            instance = new HocKyService();
        }
        return instance;
    }

    public HocKyDTO addHocKy(HocKyDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            HocKy hocKy = new HocKy(dto.getMaHocKy(), dto.getTenHocKy(),
                                    dto.getNgayBatDau(), dto.getNgayKetThuc());
            em.persist(hocKy);

            em.getTransaction().commit();
            logger.info("Added HocKy: {}", dto.getMaHocKy());

            return toDTO(hocKy);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error adding HocKy", e);
            throw new RuntimeException("Lỗi thêm học kỳ: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public HocKyDTO updateHocKy(HocKyDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            HocKy hocKy = em.find(HocKy.class, dto.getMaHocKy());
            if (hocKy == null) {
                throw new RuntimeException("Không tìm thấy học kỳ: " + dto.getMaHocKy());
            }

            hocKy.setTenHocKy(dto.getTenHocKy());
            hocKy.setNgayBatDau(dto.getNgayBatDau());
            hocKy.setNgayKetThuc(dto.getNgayKetThuc());

            em.merge(hocKy);
            em.getTransaction().commit();

            logger.info("Updated HocKy: {}", dto.getMaHocKy());
            return toDTO(hocKy);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error updating HocKy", e);
            throw new RuntimeException("Lỗi cập nhật học kỳ: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public boolean deleteHocKy(String maHocKy) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            HocKy hocKy = em.find(HocKy.class, maHocKy);
            if (hocKy == null) {
                return false;
            }

            if (!hocKy.getDsLopHocPhan().isEmpty()) {
                throw new RuntimeException("Không thể xóa học kỳ đã có lớp học phần");
            }

            em.remove(hocKy);
            em.getTransaction().commit();

            logger.info("Deleted HocKy: {}", maHocKy);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting HocKy", e);
            throw new RuntimeException("Lỗi xóa học kỳ: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public HocKyDTO getHocKyById(String maHocKy) {
        EntityManager em = emf.createEntityManager();
        try {
            HocKy hocKy = em.find(HocKy.class, maHocKy);
            return hocKy != null ? toDTO(hocKy) : null;
        } finally {
            em.close();
        }
    }

    public List<HocKyDTO> getAllHocKy() {
        EntityManager em = emf.createEntityManager();
        try {
            List<HocKy> hocKys = em.createQuery(
                "SELECT h FROM HocKy h ORDER BY h.ngayBatDau DESC", HocKy.class)
                .getResultList();
            return hocKys.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private HocKyDTO toDTO(HocKy hocKy) {
        return new HocKyDTO(
            hocKy.getMaHocKy(),
            hocKy.getTenHocKy(),
            hocKy.getNgayBatDau(),
            hocKy.getNgayKetThuc()
        );
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

