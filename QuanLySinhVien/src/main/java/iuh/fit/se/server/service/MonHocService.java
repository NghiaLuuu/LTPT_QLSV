package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.MonHocDTO;
import iuh.fit.se.common.model.Khoa;
import iuh.fit.se.common.model.MonHoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý Môn học
 */
public class MonHocService {
    private static final Logger logger = LoggerFactory.getLogger(MonHocService.class);
    private static MonHocService instance;
    private final EntityManagerFactory emf;

    private MonHocService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized MonHocService getInstance() {
        if (instance == null) {
            instance = new MonHocService();
        }
        return instance;
    }

    public MonHocDTO addMonHoc(MonHocDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
            if (khoa == null) {
                throw new RuntimeException("Không tìm thấy khoa: " + dto.getMaKhoa());
            }

            MonHoc monHoc = new MonHoc(dto.getMaMH(), dto.getTenMH(), dto.getSoTinChi());
            monHoc.setKhoa(khoa);
            em.persist(monHoc);

            em.getTransaction().commit();
            logger.info("Added MonHoc: {}", dto.getMaMH());

            return toDTO(monHoc);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error adding MonHoc", e);
            throw new RuntimeException("Lỗi thêm môn học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public MonHocDTO updateMonHoc(MonHocDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            MonHoc monHoc = em.find(MonHoc.class, dto.getMaMH());
            if (monHoc == null) {
                throw new RuntimeException("Không tìm thấy môn học: " + dto.getMaMH());
            }

            monHoc.setTenMH(dto.getTenMH());
            monHoc.setSoTinChi(dto.getSoTinChi());

            if (dto.getMaKhoa() != null) {
                Khoa khoa = em.find(Khoa.class, dto.getMaKhoa());
                if (khoa != null) {
                    monHoc.setKhoa(khoa);
                }
            }

            em.merge(monHoc);
            em.getTransaction().commit();

            logger.info("Updated MonHoc: {}", dto.getMaMH());
            return toDTO(monHoc);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error updating MonHoc", e);
            throw new RuntimeException("Lỗi cập nhật môn học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public boolean deleteMonHoc(String maMH) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            MonHoc monHoc = em.find(MonHoc.class, maMH);
            if (monHoc == null) {
                return false;
            }

            if (!monHoc.getDsLopHocPhan().isEmpty()) {
                throw new RuntimeException("Không thể xóa môn học đã có lớp học phần");
            }

            em.remove(monHoc);
            em.getTransaction().commit();

            logger.info("Deleted MonHoc: {}", maMH);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting MonHoc", e);
            throw new RuntimeException("Lỗi xóa môn học: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public MonHocDTO getMonHocById(String maMH) {
        EntityManager em = emf.createEntityManager();
        try {
            MonHoc monHoc = em.find(MonHoc.class, maMH);
            return monHoc != null ? toDTO(monHoc) : null;
        } finally {
            em.close();
        }
    }

    public List<MonHocDTO> getAllMonHocByKhoa(String maKhoa) {
        EntityManager em = emf.createEntityManager();
        try {
            List<MonHoc> monHocs = em.createQuery(
                "SELECT m FROM MonHoc m WHERE m.khoa.maKhoa = :maKhoa ORDER BY m.maMH", MonHoc.class)
                .setParameter("maKhoa", maKhoa)
                .getResultList();
            return monHocs.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<MonHocDTO> getAllMonHoc() {
        EntityManager em = emf.createEntityManager();
        try {
            List<MonHoc> monHocs = em.createQuery("SELECT m FROM MonHoc m ORDER BY m.maMH", MonHoc.class)
                    .getResultList();
            return monHocs.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private MonHocDTO toDTO(MonHoc monHoc) {
        return new MonHocDTO(
            monHoc.getMaMH(),
            monHoc.getTenMH(),
            monHoc.getSoTinChi(),
            monHoc.getKhoa() != null ? monHoc.getKhoa().getMaKhoa() : null,
            monHoc.getKhoa() != null ? monHoc.getKhoa().getTenKhoa() : null
        );
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

