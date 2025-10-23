package iuh.fit.se.common.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "tai_khoan")
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", length = 50)
    @NotBlank(message = "Username không được để trống")
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "Password không được để trống")
    private String password; // Mật khẩu đã được hash (BCrypt)

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne
    @JoinColumn(name = "ma_sv")
    private SinhVien sinhVien;

    @OneToOne
    @JoinColumn(name = "ma_gv")
    private GiangVien giangVien;

    public TaiKhoan() {
    }

    public TaiKhoan(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}

