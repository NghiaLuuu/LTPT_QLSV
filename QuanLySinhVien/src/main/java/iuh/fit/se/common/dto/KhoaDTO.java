package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Khoa (Faculty/Department)
 */
public class KhoaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKhoa;
    private String tenKhoa;

    public KhoaDTO() {}

    public KhoaDTO(String maKhoa, String tenKhoa) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "KhoaDTO{maKhoa='" + maKhoa + "', tenKhoa='" + tenKhoa + "'}";
    }
}

