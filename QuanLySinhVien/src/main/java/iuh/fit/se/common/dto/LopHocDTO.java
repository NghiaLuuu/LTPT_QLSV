package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Lớp hành chính (Administrative Class)
 */
public class LopHocDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLop;
    private String tenLop;
    private String nienKhoa;
    private String maKhoa;  // Foreign key
    private String tenKhoa;  // Tên khoa (để hiển thị)

    public LopHocDTO() {}

    public LopHocDTO(String maLop, String tenLop, String nienKhoa, String maKhoa, String tenKhoa) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.nienKhoa = nienKhoa;
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getNienKhoa() { return nienKhoa; }
    public void setNienKhoa(String nienKhoa) { this.nienKhoa = nienKhoa; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "LopHocDTO{maLop='" + maLop + "', tenLop='" + tenLop + "', nienKhoa='" + nienKhoa + "', maKhoa='" + maKhoa + "'}";
    }
}
