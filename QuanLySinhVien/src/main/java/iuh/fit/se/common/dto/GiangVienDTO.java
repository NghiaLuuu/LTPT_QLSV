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
    private String tenKhoa;  // Tên khoa (để hiển thị)

    public GiangVienDTO() {}

    public GiangVienDTO(String maGV, String hoTen, String hocVi, String maKhoa, String tenKhoa) {
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.hocVi = hocVi;
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getHocVi() { return hocVi; }
    public void setHocVi(String hocVi) { this.hocVi = hocVi; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "GiangVienDTO{maGV='" + maGV + "', hoTen='" + hoTen + "', hocVi='" + hocVi + "', maKhoa='" + maKhoa + "'}";
    }
}

