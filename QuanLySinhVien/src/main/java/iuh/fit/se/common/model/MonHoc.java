package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mon_hoc")
public class MonHoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_mh", length = 20)
    @NotBlank(message = "Mã môn học không được để trống")
    private String maMH;

    @Column(name = "ten_mh", nullable = false, length = 100)
    @NotBlank(message = "Tên môn học không được để trống")
    private String tenMH;

    @Column(name = "so_tin_chi", nullable = false)
    @Positive(message = "Số tín chỉ phải lớn hơn 0")
    private int soTinChi;

    @ManyToOne
    @JoinColumn(name = "ma_khoa")
    private Khoa khoa;

    @OneToMany(mappedBy = "monHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LopHocPhan> dsLopHocPhan = new ArrayList<>();

    public MonHoc() {
    }

    public MonHoc(String maMH, String tenMH, int soTinChi) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
    }

    // Getters and Setters
    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    public List<LopHocPhan> getDsLopHocPhan() {
        return dsLopHocPhan;
    }

    public void setDsLopHocPhan(List<LopHocPhan> dsLopHocPhan) {
        this.dsLopHocPhan = dsLopHocPhan;
    }

    @Override
    public String toString() {
        return "MonHoc{" +
                "maMH='" + maMH + '\'' +
                ", tenMH='" + tenMH + '\'' +
                ", soTinChi=" + soTinChi +
                '}';
    }
}

