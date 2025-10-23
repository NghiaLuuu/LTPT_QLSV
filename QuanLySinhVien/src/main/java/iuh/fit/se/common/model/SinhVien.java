package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sinh_vien")
public class SinhVien implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_sv", length = 20)
    @NotBlank(message = "Mã SV không được để trống")
    @Size(min = 5, max = 20, message = "Mã SV phải từ 5 đến 20 ký tự")
    private String maSV;

    @Column(name = "ho_ten", nullable = false, length = 100)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String hoTen;

    @Column(name = "ngay_sinh")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Giới tính không được để trống")
    private Gender gioiTinh;

    @Column(name = "chuyen_nganh", length = 50)
    private String chuyenNganh;

    @ManyToOne
    @JoinColumn(name = "ma_lop")
    private LopHoc lopHoc;

    @Column(name = "diem_tb")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private double diemTB;

    @OneToOne(mappedBy = "sinhVien")
    private TaiKhoan taiKhoan;

    @OneToMany(mappedBy = "sinhVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Diem> dsDiem = new ArrayList<>();

    public SinhVien() { }

    public SinhVien(String maSV, String hoTen, LocalDate ngaySinh, Gender gioiTinh, String chuyenNganh, double diemTB) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.chuyenNganh = chuyenNganh;
        this.diemTB = diemTB;
    }

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

    public LopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }

    public double getDiemTB() { return diemTB; }
    public void setDiemTB(double diemTB) { this.diemTB = diemTB; }

    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    public List<Diem> getDsDiem() { return dsDiem; }
    public void setDsDiem(List<Diem> dsDiem) { this.dsDiem = dsDiem; }

    @Override
    public String toString() {
        return "SinhVien{" +
                "maSV='" + maSV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh=" + gioiTinh +
                ", chuyenNganh='" + chuyenNganh + '\'' +
                ", diemTB=" + diemTB +
                '}';
    }
}
