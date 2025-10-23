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

