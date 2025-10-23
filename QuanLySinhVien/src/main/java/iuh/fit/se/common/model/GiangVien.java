package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "giang_vien")
public class GiangVien implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_gv", length = 20)
    @NotBlank(message = "Mã giảng viên không được để trống")
    private String maGV;

    @Column(name = "ho_ten", nullable = false, length = 100)
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @Column(name = "hoc_vi", length = 50)
    private String hocVi; // Thạc sĩ, Tiến sĩ

    @ManyToOne
    @JoinColumn(name = "ma_khoa")
    private Khoa khoa;

    @OneToOne(mappedBy = "giangVien")
    private TaiKhoan taiKhoan;

    @OneToMany(mappedBy = "giangVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LopHocPhan> dsLopHocPhan = new ArrayList<>();

    public GiangVien() {
    }

    public GiangVien(String maGV, String hoTen, String hocVi) {
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.hocVi = hocVi;
    }

    // Getters and Setters
    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public List<LopHocPhan> getDsLopHocPhan() {
        return dsLopHocPhan;
    }

    public void setDsLopHocPhan(List<LopHocPhan> dsLopHocPhan) {
        this.dsLopHocPhan = dsLopHocPhan;
    }

    @Override
    public String toString() {
        return "GiangVien{" +
                "maGV='" + maGV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", hocVi='" + hocVi + '\'' +
                '}';
    }
}

