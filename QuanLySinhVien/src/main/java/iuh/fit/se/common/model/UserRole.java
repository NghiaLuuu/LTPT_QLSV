package iuh.fit.se.common.model;

import java.io.Serializable;

/**
 * Enum định nghĩa các vai trò người dùng trong hệ thống
 */
public enum UserRole implements Serializable {
    ADMIN,        // Quản trị viên/Giáo vụ - toàn quyền
    GIANG_VIEN,   // Giảng viên - nhập điểm, xem lớp mình dạy
    SINH_VIEN     // Sinh viên - xem điểm, đăng ký học phần
}

