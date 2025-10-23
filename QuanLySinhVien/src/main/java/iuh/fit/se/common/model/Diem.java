package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Entity
@Table(name = "diem")
public class Diem implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private DiemId id;

    @ManyToOne
    @MapsId("maSV")
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    @ManyToOne
    @MapsId("maLHP")
    @JoinColumn(name = "ma_lhp")
    private LopHocPhan lopHocPhan;

    @Column(name = "diem_chuyen_can")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private Double diemChuyenCan;

    @Column(name = "diem_giua_ky")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private Double diemGiuaKy;

    @Column(name = "diem_cuoi_ky")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private Double diemCuoiKy;

    @Column(name = "diem_tong_ket")
    @Min(value = 0, message = "Điểm phải >= 0")
    @Max(value = 10, message = "Điểm phải <= 10")
    private Double diemTongKet;

    @Column(name = "trang_thai", length = 20)
    @Enumerated(EnumType.STRING)
    private TrangThaiDiem trangThai;

    public Diem() {
    }

    public Diem(DiemId id, SinhVien sinhVien, LopHocPhan lopHocPhan) {
        this.id = id;
        this.sinhVien = sinhVien;
        this.lopHocPhan = lopHocPhan;
    }

    // Tính điểm tổng kết tự động (ví dụ: 10% chuyên cần + 30% giữa kỳ + 60% cuối kỳ)
    public void tinhDiemTongKet() {
        if (diemChuyenCan != null && diemGiuaKy != null && diemCuoiKy != null) {
            this.diemTongKet = diemChuyenCan * 0.1 + diemGiuaKy * 0.3 + diemCuoiKy * 0.6;
            this.trangThai = (diemTongKet >= 5.0) ? TrangThaiDiem.DAT : TrangThaiDiem.KHONG_DAT;
        }
    }

    // Getters and Setters
    public DiemId getId() {
        return id;
    }

    public void setId(DiemId id) {
        this.id = id;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public LopHocPhan getLopHocPhan() {
        return lopHocPhan;
    }

    public void setLopHocPhan(LopHocPhan lopHocPhan) {
        this.lopHocPhan = lopHocPhan;
    }

    public Double getDiemChuyenCan() {
        return diemChuyenCan;
    }

    public void setDiemChuyenCan(Double diemChuyenCan) {
        this.diemChuyenCan = diemChuyenCan;
    }

    public Double getDiemGiuaKy() {
        return diemGiuaKy;
    }

    public void setDiemGiuaKy(Double diemGiuaKy) {
        this.diemGiuaKy = diemGiuaKy;
    }

    public Double getDiemCuoiKy() {
        return diemCuoiKy;
    }

    public void setDiemCuoiKy(Double diemCuoiKy) {
        this.diemCuoiKy = diemCuoiKy;
    }

    public Double getDiemTongKet() {
        return diemTongKet;
    }

    public void setDiemTongKet(Double diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    public TrangThaiDiem getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDiem trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Diem{" +
                "id=" + id +
                ", diemChuyenCan=" + diemChuyenCan +
                ", diemGiuaKy=" + diemGiuaKy +
                ", diemCuoiKy=" + diemCuoiKy +
                ", diemTongKet=" + diemTongKet +
                ", trangThai=" + trangThai +
                '}';
    }
}

