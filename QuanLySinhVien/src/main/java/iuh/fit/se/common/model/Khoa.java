package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khoa")
public class Khoa implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_khoa", length = 10)
    @NotBlank(message = "Mã khoa không được để trống")
    private String maKhoa;

    @Column(name = "ten_khoa", nullable = false, length = 100)
    @NotBlank(message = "Tên khoa không được để trống")
    private String tenKhoa;

    @OneToMany(mappedBy = "khoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LopHoc> dsLopHoc = new ArrayList<>();

    @OneToMany(mappedBy = "khoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiangVien> dsGiangVien = new ArrayList<>();

    @OneToMany(mappedBy = "khoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonHoc> dsMonHoc = new ArrayList<>();

    public Khoa() {
    }

    public Khoa(String maKhoa, String tenKhoa) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    // Getters and Setters
    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public List<LopHoc> getDsLopHoc() {
        return dsLopHoc;
    }

    public void setDsLopHoc(List<LopHoc> dsLopHoc) {
        this.dsLopHoc = dsLopHoc;
    }

    public List<GiangVien> getDsGiangVien() {
        return dsGiangVien;
    }

    public void setDsGiangVien(List<GiangVien> dsGiangVien) {
        this.dsGiangVien = dsGiangVien;
    }

    public List<MonHoc> getDsMonHoc() {
        return dsMonHoc;
    }

    public void setDsMonHoc(List<MonHoc> dsMonHoc) {
        this.dsMonHoc = dsMonHoc;
    }

    @Override
    public String toString() {
        return "Khoa{" +
                "maKhoa='" + maKhoa + '\'' +
                ", tenKhoa='" + tenKhoa + '\'' +
                '}';
    }
}

