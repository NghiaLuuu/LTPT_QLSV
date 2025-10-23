package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lop_hoc")
public class LopHoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_lop", length = 20)
    @NotBlank(message = "Mã lớp không được để trống")
    private String maLop;

    @Column(name = "ten_lop", nullable = false, length = 100)
    @NotBlank(message = "Tên lớp không được để trống")
    private String tenLop;

    @Column(name = "nien_khoa", length = 20)
    private String nienKhoa;

    @ManyToOne
    @JoinColumn(name = "ma_khoa")
    private Khoa khoa;

    @OneToMany(mappedBy = "lopHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SinhVien> dsSinhVien = new ArrayList<>();

    public LopHoc() {
    }

    public LopHoc(String maLop, String tenLop, String nienKhoa) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.nienKhoa = nienKhoa;
    }

    // Getters and Setters
    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getNienKhoa() {
        return nienKhoa;
    }

    public void setNienKhoa(String nienKhoa) {
        this.nienKhoa = nienKhoa;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    public List<SinhVien> getDsSinhVien() {
        return dsSinhVien;
    }

    public void setDsSinhVien(List<SinhVien> dsSinhVien) {
        this.dsSinhVien = dsSinhVien;
    }

    @Override
    public String toString() {
        return "LopHoc{" +
                "maLop='" + maLop + '\'' +
                ", tenLop='" + tenLop + '\'' +
                ", nienKhoa='" + nienKhoa + '\'' +
                '}';
    }
}

