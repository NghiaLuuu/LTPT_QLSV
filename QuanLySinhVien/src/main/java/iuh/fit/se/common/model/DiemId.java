package iuh.fit.se.common.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DiemId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ma_sv")
    private String maSV;

    @Column(name = "ma_lhp")
    private Long maLHP;

    public DiemId() {
    }

    public DiemId(String maSV, Long maLHP) {
        this.maSV = maSV;
        this.maLHP = maLHP;
    }

    // Getters and Setters
    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public Long getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(Long maLHP) {
        this.maLHP = maLHP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiemId diemId = (DiemId) o;
        return Objects.equals(maSV, diemId.maSV) && Objects.equals(maLHP, diemId.maLHP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSV, maLHP);
    }

    @Override
    public String toString() {
        return "DiemId{" +
                "maSV='" + maSV + '\'' +
                ", maLHP=" + maLHP +
                '}';
    }
}

