package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Lớp học phần (Course Section)
 */
public class LopHocPhanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long maLHP;
    private String maMH;
    private String tenMH;
    private String maGV;
    private String tenGV;
    private String maHocKy;
    private String tenHocKy;
    private int soLuongToiDa;
    private String thu;           // Thứ trong tuần (2-7, CN)
    private String tiet;          // Tiết học (1-10)
    private String phongHoc;      // Phòng học
    private String namHoc;        // Năm học
    private int siSo;             // Số sinh viên đã đăng ký

    public LopHocPhanDTO() {
    }

    public LopHocPhanDTO(Long maLHP, String maMH, String tenMH, String maGV, String tenGV,
                         String maHocKy, String tenHocKy, int soLuongToiDa) {
        this.maLHP = maLHP;
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.maGV = maGV;
        this.tenGV = tenGV;
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.soLuongToiDa = soLuongToiDa;
    }

    public Long getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(Long maLHP) {
        this.maLHP = maLHP;
    }

    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    public String getTenGV() {
        return tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public String getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(String maHocKy) {
        this.maHocKy = maHocKy;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getTiet() {
        return tiet;
    }

    public void setTiet(String tiet) {
        this.tiet = tiet;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public int getSiSo() {
        return siSo;
    }

    public void setSiSo(int siSo) {
        this.siSo = siSo;
    }

    @Override
    public String toString() {
        return "LopHocPhanDTO{" +
                "maLHP=" + maLHP +
                ", tenMH='" + tenMH + '\'' +
                ", tenGV='" + tenGV + '\'' +
                ", tenHocKy='" + tenHocKy + '\'' +
                ", soLuongToiDa=" + soLuongToiDa +
                '}';
    }
}
