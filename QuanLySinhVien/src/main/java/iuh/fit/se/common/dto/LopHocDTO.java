package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho Lớp hành chính (Administrative Class)
 */
public class LopHocDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLop;
    private String tenLop;
    private String nienKhoa;
    private String maKhoa;  // Foreign key

    public LopHocDTO() {}

    public LopHocDTO(String maLop, String tenLop, String nienKhoa, String maKhoa) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.nienKhoa = nienKhoa;
        this.maKhoa = maKhoa;
    }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getNienKhoa() { return nienKhoa; }
    public void setNienKhoa(String nienKhoa) { this.nienKhoa = nienKhoa; }

    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    @Override
    public String toString() {
        return "LopHocDTO{maLop='" + maLop + "', tenLop='" + tenLop + "', nienKhoa='" + nienKhoa + "'}";
    }
}
package iuh.fit.se.common.dto;

import java.io.Serializable;

/**
 * DTO cho thông tin đăng nhập
 */
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public LoginDTO() {}

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

