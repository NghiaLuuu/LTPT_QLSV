package iuh.fit.se.common.dto;

import iuh.fit.se.common.model.Gender;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO cho Sinh viên
 */
public class SinhVienDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maSV;
    private String hoTen;
    private LocalDate ngaySinh;
    private Gender gioiTinh;
    private String chuyenNganh;
    private String lop;
    private double diemTB;

    public SinhVienDTO() { }

    public SinhVienDTO(String maSV, String hoTen, LocalDate ngaySinh, Gender gioiTinh, String chuyenNganh, String lop, double diemTB) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.chuyenNganh = chuyenNganh;
        this.lop = lop;
        this.diemTB = diemTB;
    }

    // Getters and setters
    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public Gender getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(Gender gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getChuyenNganh() { return chuyenNganh; }
    public void setChuyenNganh(String chuyenNganh) { this.chuyenNganh = chuyenNganh; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    public double getDiemTB() { return diemTB; }
    public void setDiemTB(double diemTB) { this.diemTB = diemTB; }

    @Override
    public String toString() {
        return "SinhVienDTO{" +
                "maSV='" + maSV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh=" + gioiTinh +
                ", chuyenNganh='" + chuyenNganh + '\'' +
                ", lop='" + lop + '\'' +
                ", diemTB=" + diemTB +
                '}';
    }
}

