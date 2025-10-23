package iuh.fit.se.common.dto;

import iuh.fit.se.common.model.UserRole;
import java.io.Serializable;

/**
 * DTO cho phản hồi sau khi đăng nhập
 */
public class AuthResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String authToken;
    private String username;
    private UserRole role;
    private String maSV;  // Nếu là sinh viên
    private String maGV;  // Nếu là giảng viên

    public AuthResponseDTO() {}

    public AuthResponseDTO(boolean success, String message, String authToken, String username,
                          UserRole role, String maSV, String maGV) {
        this.success = success;
        this.message = message;
        this.authToken = authToken;
        this.username = username;
        this.role = role;
        this.maSV = maSV;
        this.maGV = maGV;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    @Override
    public String toString() {
        return "AuthResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
