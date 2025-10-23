package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Môn học (Course/Subject)
 */
public class MonHocDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maMH;
    private String tenMH;
    private int soTinChi;
    private String maKhoa;
    private String tenKhoa;  // Tên khoa (để hiển thị)

    public MonHocDTO() {}

    public MonHocDTO(String maMH, String tenMH, int soTinChi, String maKhoa, String tenKhoa) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaMH() { return maMH; }
    public void setMaMH(String maMH) { this.maMH = maMH; }

    public String getTenMH() { return tenMH; }
    public void setTenMH(String tenMH) { this.tenMH = tenMH; }

    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "MonHocDTO{maMH='" + maMH + "', tenMH='" + tenMH + "', soTinChi=" + soTinChi + ", maKhoa='" + maKhoa + "'}";
    }
}
