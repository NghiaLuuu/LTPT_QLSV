package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lop_hoc_phan")
public class LopHocPhan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lhp")
    private Long maLHP;

    @ManyToOne
    @JoinColumn(name = "ma_mh", nullable = false)
    private MonHoc monHoc;

    @ManyToOne
    @JoinColumn(name = "ma_gv")
    private GiangVien giangVien;

    @ManyToOne
    @JoinColumn(name = "ma_hoc_ky", nullable = false)
    private HocKy hocKy;

    @Column(name = "so_luong_toi_da")
    @Positive(message = "Số lượng tối đa phải lớn hơn 0")
    private int soLuongToiDa;

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Diem> dsDiem = new ArrayList<>();

    public LopHocPhan() {
    }

    public LopHocPhan(MonHoc monHoc, GiangVien giangVien, HocKy hocKy, int soLuongToiDa) {
        this.monHoc = monHoc;
        this.giangVien = giangVien;
        this.hocKy = hocKy;
        this.soLuongToiDa = soLuongToiDa;
    }

    // Getters and Setters
    public Long getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(Long maLHP) {
        this.maLHP = maLHP;
    }

    public MonHoc getMonHoc() {
        return monHoc;
    }

    public void setMonHoc(MonHoc monHoc) {
        this.monHoc = monHoc;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public HocKy getHocKy() {
        return hocKy;
    }

    public void setHocKy(HocKy hocKy) {
        this.hocKy = hocKy;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }

    public List<Diem> getDsDiem() {
        return dsDiem;
    }

    public void setDsDiem(List<Diem> dsDiem) {
        this.dsDiem = dsDiem;
    }

    @Override
    public String toString() {
        return "LopHocPhan{" +
                "maLHP=" + maLHP +
                ", soLuongToiDa=" + soLuongToiDa +
                '}';
    }
}

