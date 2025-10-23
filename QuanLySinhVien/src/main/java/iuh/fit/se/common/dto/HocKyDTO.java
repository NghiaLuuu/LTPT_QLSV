package iuh.fit.se.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO cho Học kỳ
 */
public class HocKyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHocKy;
    private String tenHocKy;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    public HocKyDTO() {
    }

    public HocKyDTO(String maHocKy, String tenHocKy, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
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

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    @Override
    public String toString() {
        return "HocKyDTO{" +
                "maHocKy='" + maHocKy + '\'' +
                ", tenHocKy='" + tenHocKy + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}

