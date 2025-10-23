package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoc_ky")
public class HocKy implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_hoc_ky", length = 20)
    @NotBlank(message = "Mã học kỳ không được để trống")
    private String maHocKy;

    @Column(name = "ten_hoc_ky", nullable = false, length = 100)
    @NotBlank(message = "Tên học kỳ không được để trống")
    private String tenHocKy;

    @Column(name = "ngay_bat_dau")
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;

    @OneToMany(mappedBy = "hocKy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LopHocPhan> dsLopHocPhan = new ArrayList<>();

    public HocKy() {
    }

    public HocKy(String maHocKy, String tenHocKy, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    // Getters and Setters
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

    public List<LopHocPhan> getDsLopHocPhan() {
        return dsLopHocPhan;
    }

    public void setDsLopHocPhan(List<LopHocPhan> dsLopHocPhan) {
        this.dsLopHocPhan = dsLopHocPhan;
    }

    @Override
    public String toString() {
        return "HocKy{" +
                "maHocKy='" + maHocKy + '\'' +
                ", tenHocKy='" + tenHocKy + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}

