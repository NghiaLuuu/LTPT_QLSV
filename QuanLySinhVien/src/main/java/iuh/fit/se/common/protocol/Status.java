package iuh.fit.se.common.protocol;

public enum Status {
    SUCCESS,
    ERROR,
    NOT_FOUND,
    VALIDATION_ERROR,

    // === NÂNG CAO - Phân quyền ===
    UNAUTHORIZED,  // Chưa đăng nhập hoặc token không hợp lệ
    FORBIDDEN      // Đã đăng nhập nhưng không có quyền thực hiện thao tác này
}
