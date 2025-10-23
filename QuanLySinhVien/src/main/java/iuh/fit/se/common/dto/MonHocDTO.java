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

    public MonHocDTO() {}

    public MonHocDTO(String maMH, String tenMH, int soTinChi, String maKhoa) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
        this.maKhoa = maKhoa;
    }

    public String getMaMH() { return maMH; }
    public void setMaMH(String maMH) { this.maMH = maMH; }

    public String getTenMH() { return tenMH; }
    public void setTenMH(String tenMH) { this.tenMH = tenMH; }

    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    @Override
    public String toString() {
        return "MonHocDTO{maMH='" + maMH + "', tenMH='" + tenMH + "', soTinChi=" + soTinChi + "}";
    }
}
package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Giảng viên (Lecturer)
 */
public class GiangVienDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maGV;
    private String hoTen;
    private String hocVi;  // Thạc sĩ, Tiến sĩ
    private String maKhoa;

    public GiangVienDTO() {}

    public GiangVienDTO(String maGV, String hoTen, String hocVi, String maKhoa) {
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.hocVi = hocVi;
        this.maKhoa = maKhoa;
    }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getHocVi() { return hocVi; }
    public void setHocVi(String hocVi) { this.hocVi = hocVi; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    @Override
    public String toString() {
        return "GiangVienDTO{maGV='" + maGV + "', hoTen='" + hoTen + "', hocVi='" + hocVi + "'}";
    }
}

