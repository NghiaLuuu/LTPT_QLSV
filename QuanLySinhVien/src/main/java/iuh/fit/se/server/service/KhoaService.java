package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.model.Khoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Khoa (Faculty/Department)
 */
public class KhoaService {
    private static final Logger logger = LoggerFactory.getLogger(KhoaService.class);
    private static KhoaService instance;
    private final EntityManagerFactory emf;

    private KhoaService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized KhoaService getInstance() {
        if (instance == null) {
            instance = new KhoaService();
        }
        return instance;
    }

    /**
     * Thêm khoa mới
     */
    public KhoaDTO addKhoa(KhoaDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = new Khoa(dto.getMaKhoa(), dto.getTenKhoa());
            em.persist(khoa);

            em.getTransaction().commit();
            logger.info("Added new Khoa: {}", dto.getMaKhoa());

            return toDTO(khoa);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error adding Khoa", e);
            throw new RuntimeException("Lỗi thêm khoa: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin khoa
     */
    public KhoaDTO updateKhoa(KhoaDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
            if (khoa == null) {
                throw new RuntimeException("Không tìm thấy khoa với mã: " + dto.getMaKhoa());
            }

            khoa.setTenKhoa(dto.getTenKhoa());
            em.merge(khoa);

            em.getTransaction().commit();
            logger.info("Updated Khoa: {}", dto.getMaKhoa());

            return toDTO(khoa);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error updating Khoa", e);
            throw new RuntimeException("Lỗi cập nhật khoa: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Xóa khoa
     */
    public boolean deleteKhoa(String maKhoa) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = em.find(Khoa.class, maKhoa);
            if (khoa == null) {
                return false;
            }

            // Kiểm tra khoa có dữ liệu liên quan không
            if (!khoa.getDsLopHoc().isEmpty() || !khoa.getDsGiangVien().isEmpty() || !khoa.getDsMonHoc().isEmpty()) {
                throw new RuntimeException("Không thể xóa khoa đã có dữ liệu liên quan");
            }

            em.remove(khoa);
            em.getTransaction().commit();

            logger.info("Deleted Khoa: {}", maKhoa);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting Khoa", e);
            throw new RuntimeException("Lỗi xóa khoa: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Lấy thông tin khoa theo mã
     */
    public KhoaDTO getKhoaById(String maKhoa) {
        EntityManager em = emf.createEntityManager();
        try {
            Khoa khoa = em.find(Khoa.class, maKhoa);
            return khoa != null ? toDTO(khoa) : null;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách tất cả các khoa
     */
    public List<KhoaDTO> getAllKhoa() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Khoa> khoas = em.createQuery("SELECT k FROM Khoa k ORDER BY k.maKhoa", Khoa.class)
                    .getResultList();
            return khoas.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    /**
     * Chuyển đổi Entity sang DTO
     */
    private KhoaDTO toDTO(Khoa khoa) {
        return new KhoaDTO(khoa.getMaKhoa(), khoa.getTenKhoa());
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

