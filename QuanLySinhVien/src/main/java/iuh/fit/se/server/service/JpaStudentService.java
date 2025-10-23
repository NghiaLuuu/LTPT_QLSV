    package iuh.fit.se.server.service;

import iuh.fit.se.common.model.SinhVien;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

/**
 * JPA-backed StudentService implementation. Each method creates its own EntityManager
 * so it's safe to use from multiple threads.
 */
public class JpaStudentService {
    private static final Logger logger = LoggerFactory.getLogger(JpaStudentService.class);

    private final EntityManagerFactory emf;
    private final Validator validator;

    public JpaStudentService(EntityManagerFactory emf) {
        this.emf = emf;
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    public boolean addStudent(SinhVien sv) {
        if (sv == null) return false;

        // Bean validation
        Set<ConstraintViolation<SinhVien>> violations = validator.validate(sv);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<SinhVien> v : violations) {
                logger.warn("Validation error: {} -> {}", v.getPropertyPath(), v.getMessage());
            }
            return false;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            if (em.find(SinhVien.class, sv.getMaSV()) != null) {
                logger.warn("Thêm thất bại: Trùng mã SV {}", sv.getMaSV());
                return false;
            }
            tx.begin();
            em.persist(sv);
            tx.commit();
            logger.info("Đã thêm SV: {}", sv.getMaSV());
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Lỗi khi thêm SV: {}", e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }

    public SinhVien findStudentById(String maSV) {
        if (maSV == null) return null;
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(SinhVien.class, maSV);
        } finally {
            em.close();
        }
    }

    public List<SinhVien> getAllStudents() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<SinhVien> q = em.createQuery("SELECT s FROM SinhVien s", SinhVien.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean updateStudent(SinhVien sv) {
        if (sv == null || sv.getMaSV() == null) return false;

        Set<ConstraintViolation<SinhVien>> violations = validator.validate(sv);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<SinhVien> v : violations) {
                logger.warn("Validation error: {} -> {}", v.getPropertyPath(), v.getMessage());
            }
            return false;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            SinhVien existing = em.find(SinhVien.class, sv.getMaSV());
            if (existing == null) return false;
            tx.begin();
            em.merge(sv);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Lỗi khi cập nhật SV: {}", e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteStudent(String maSV) {
        if (maSV == null) return false;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            SinhVien existing = em.find(SinhVien.class, maSV);
            if (existing == null) return false;
            tx.begin();
            em.remove(existing);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Lỗi khi xóa SV: {}", e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }
}

