package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.LopHocPhanDTO;
import iuh.fit.se.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ phức tạp: đăng ký học phần, nhập điểm, xem bảng điểm
 */
public class HocVuService {
    private static final Logger logger = LoggerFactory.getLogger(HocVuService.class);
    private static HocVuService instance;
    private final EntityManagerFactory emf;

    private HocVuService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized HocVuService getInstance() {
        if (instance == null) {
            instance = new HocVuService();
        }
        return instance;
    }

    /**
     * Mở lớp học phần mới
     */
    public LopHocPhanDTO moLopHocPhan(String maMH, String maGV, String maHocKy, int soLuongToiDa) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            MonHoc monHoc = em.find(MonHoc.class, maMH);
            if (monHoc == null) {
                throw new RuntimeException("Không tìm thấy môn học: " + maMH);
            }

            GiangVien giangVien = em.find(GiangVien.class, maGV);
            if (giangVien == null) {
                throw new RuntimeException("Không tìm thấy giảng viên: " + maGV);
            }

            HocKy hocKy = em.find(HocKy.class, maHocKy);
            if (hocKy == null) {
                throw new RuntimeException("Không tìm thấy học kỳ: " + maHocKy);
            }

            LopHocPhan lhp = new LopHocPhan(monHoc, giangVien, hocKy, soLuongToiDa);
            em.persist(lhp);

            em.getTransaction().commit();
            logger.info("Created LopHocPhan: {} - {} - {}", monHoc.getTenMH(), giangVien.getHoTen(), hocKy.getTenHocKy());

            return toDTO(lhp);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error creating LopHocPhan", e);
            throw new RuntimeException("Lỗi mở lớp học phần: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Sinh viên đăng ký học phần
     */
    public boolean dangKyHocPhan(String maSV, Long maLHP) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            SinhVien sinhVien = em.find(SinhVien.class, maSV);
            if (sinhVien == null) {
                throw new RuntimeException("Không tìm thấy sinh viên: " + maSV);
            }

            LopHocPhan lopHocPhan = em.find(LopHocPhan.class, maLHP);
            if (lopHocPhan == null) {
                throw new RuntimeException("Không tìm thấy lớp học phần: " + maLHP);
            }

            // Kiểm tra sĩ số
            long soSinhVienDaDangKy = em.createQuery(
                "SELECT COUNT(d) FROM Diem d WHERE d.lopHocPhan.maLHP = :maLHP", Long.class)
                .setParameter("maLHP", maLHP)
                .getSingleResult();

            if (soSinhVienDaDangKy >= lopHocPhan.getSoLuongToiDa()) {
                throw new RuntimeException("Lớp học phần đã đầy");
            }

            // Kiểm tra đã đăng ký chưa
            DiemId diemId = new DiemId(maSV, maLHP);
            Diem existingDiem = em.find(Diem.class, diemId);
            if (existingDiem != null) {
                throw new RuntimeException("Sinh viên đã đăng ký lớp học phần này");
            }

            // Tạo bản ghi điểm mới (chưa có điểm)
            Diem diem = new Diem(diemId, sinhVien, lopHocPhan);
            em.persist(diem);

            em.getTransaction().commit();
            logger.info("Student {} registered for course {}", maSV, maLHP);

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error registering course", e);
            throw new RuntimeException("Lỗi đăng ký học phần: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Giảng viên nhập điểm cho sinh viên
     */
    public boolean nhapDiem(String maGV, String maSV, Long maLHP,
                           Double diemCC, Double diemGK, Double diemCK) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Kiểm tra giảng viên có dạy lớp này không
            LopHocPhan lopHocPhan = em.find(LopHocPhan.class, maLHP);
            if (lopHocPhan == null) {
                throw new RuntimeException("Không tìm thấy lớp học phần");
            }

            if (!lopHocPhan.getGiangVien().getMaGV().equals(maGV)) {
                throw new RuntimeException("Giảng viên không có quyền nhập điểm cho lớp này");
            }

            // Tìm bản ghi điểm
            DiemId diemId = new DiemId(maSV, maLHP);
            Diem diem = em.find(Diem.class, diemId);
            if (diem == null) {
                throw new RuntimeException("Sinh viên chưa đăng ký lớp học phần này");
            }

            // Cập nhật điểm
            diem.setDiemChuyenCan(diemCC);
            diem.setDiemGiuaKy(diemGK);
            diem.setDiemCuoiKy(diemCK);

            // Tính điểm tổng kết tự động
            diem.tinhDiemTongKet();

            em.merge(diem);
            em.getTransaction().commit();

            logger.info("Lecturer {} entered grades for student {} in course {}", maGV, maSV, maLHP);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error entering grades", e);
            throw new RuntimeException("Lỗi nhập điểm: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách lớp học phần để đăng ký (theo học kỳ)
     */
    public List<LopHocPhanDTO> getLopHocPhanToRegister(String maHocKy) {
        EntityManager em = emf.createEntityManager();
        try {
            List<LopHocPhan> lhpList = em.createQuery(
                "SELECT l FROM LopHocPhan l WHERE l.hocKy.maHocKy = :maHocKy", LopHocPhan.class)
                .setParameter("maHocKy", maHocKy)
                .getResultList();

            return lhpList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách lớp học phần của giảng viên
     */
    public List<LopHocPhanDTO> getLopHocPhanByGiangVien(String maGV) {
        EntityManager em = emf.createEntityManager();
        try {
            List<LopHocPhan> lhpList = em.createQuery(
                "SELECT l FROM LopHocPhan l WHERE l.giangVien.maGV = :maGV", LopHocPhan.class)
                .setParameter("maGV", maGV)
                .getResultList();

            return lhpList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách sinh viên trong lớp học phần
     */
    public List<String> getDanhSachSinhVienByLopHocPhan(Long maLHP) {
        EntityManager em = emf.createEntityManager();
        try {
            List<String> dsSinhVien = em.createQuery(
                "SELECT d.sinhVien.maSV FROM Diem d WHERE d.lopHocPhan.maLHP = :maLHP", String.class)
                .setParameter("maLHP", maLHP)
                .getResultList();

            return dsSinhVien;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa lớp học phần
     */
    public boolean deleteLopHocPhan(Long maLHP) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp == null) {
                return false;
            }

            if (!lhp.getDsDiem().isEmpty()) {
                throw new RuntimeException("Không thể xóa lớp học phần đã có sinh viên đăng ký");
            }

            em.remove(lhp);
            em.getTransaction().commit();

            logger.info("Deleted LopHocPhan: {}", maLHP);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting LopHocPhan", e);
            throw new RuntimeException("Lỗi xóa lớp học phần: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<LopHocPhanDTO> getAllLopHocPhanByHocKy(String maHocKy) {
        EntityManager em = emf.createEntityManager();
        try {
            List<LopHocPhan> lhpList = em.createQuery(
                "SELECT l FROM LopHocPhan l WHERE l.hocKy.maHocKy = :maHocKy", LopHocPhan.class)
                .setParameter("maHocKy", maHocKy)
                .getResultList();

            return lhpList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private LopHocPhanDTO toDTO(LopHocPhan lhp) {
        return new LopHocPhanDTO(
            lhp.getMaLHP(),
            lhp.getMonHoc() != null ? lhp.getMonHoc().getMaMH() : null,
            lhp.getMonHoc() != null ? lhp.getMonHoc().getTenMH() : null,
            lhp.getGiangVien() != null ? lhp.getGiangVien().getMaGV() : null,
            lhp.getGiangVien() != null ? lhp.getGiangVien().getHoTen() : null,
            lhp.getHocKy() != null ? lhp.getHocKy().getMaHocKy() : null,
            lhp.getHocKy() != null ? lhp.getHocKy().getTenHocKy() : null,
            lhp.getSoLuongToiDa()
        );
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

