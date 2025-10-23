package iuh.fit.se.common.protocol;

public enum Command {
    // === GỐC - Quản lý Sinh viên đơn giản ===
    ADD_STUDENT,
    UPDATE_STUDENT,
    DELETE_STUDENT,
    FIND_STUDENT_BY_ID,
    GET_ALL_STUDENTS,

    // === NÂNG CAO - Xác thực ===
    LOGIN,
    LOGOUT,

    // === Quản lý Khoa ===
    KHOA_ADD,
    KHOA_UPDATE,
    KHOA_DELETE,
    KHOA_GET_BY_ID,
    KHOA_GET_ALL,

    // === Quản lý Lớp hành chính ===
    LOPHOC_ADD,
    LOPHOC_UPDATE,
    LOPHOC_DELETE,
    LOPHOC_GET_ALL_BY_KHOA,
    LOPHOC_GET_ALL,

    // === Quản lý Sinh viên (mở rộng) ===
    SINHVIEN_ADD,
    SINHVIEN_UPDATE,
    SINHVIEN_DELETE,
    SINHVIEN_GET_BY_ID,
    SINHVIEN_GET_ALL_BY_LOPHOC,
    SINHVIEN_GET_ALL,

    // === Quản lý Giảng viên ===
    GIANGVIEN_ADD,
    GIANGVIEN_UPDATE,
    GIANGVIEN_DELETE,
    GIANGVIEN_GET_ALL_BY_KHOA,
    GIANGVIEN_GET_ALL,

    // === Quản lý Môn học ===
    MONHOC_ADD,
    MONHOC_UPDATE,
    MONHOC_DELETE,
    MONHOC_GET_ALL_BY_KHOA,
    MONHOC_GET_ALL,

    // === Quản lý Học kỳ ===
    HOCKY_ADD,
    HOCKY_UPDATE,
    HOCKY_DELETE,
    HOCKY_GET_ALL,

    // === Quản lý Đào tạo (Lớp học phần) ===
    LOPHOCPHAN_ADD,
    LOPHOCPHAN_UPDATE,
    LOPHOCPHAN_DELETE,
    LOPHOCPHAN_GET_ALL_BY_HOCKY,
    LOPHOCPHAN_GET_ALL,

    // === Nghiệp vụ Sinh viên ===
    SV_GET_LOPHOCPHAN_TO_REGISTER,  // Lấy danh sách lớp học phần để đăng ký
    SV_REGISTER_COURSE,             // Đăng ký học phần
    SV_GET_TRANSCRIPT,              // Lấy bảng điểm

    // === Nghiệp vụ Giảng viên ===
    GV_GET_MY_CLASSES,              // Lấy các lớp giảng viên dạy
    GV_GET_STUDENT_LIST_OF_CLASS,   // Lấy danh sách sinh viên của 1 lớp học phần
    GV_ENTER_GRADE,                 // Nhập điểm

    // === Quản lý Tài khoản ===
    TAIKHOAN_ADD,
    TAIKHOAN_UPDATE,
    TAIKHOAN_DELETE,
    TAIKHOAN_GET_ALL,
    TAIKHOAN_CHANGE_PASSWORD
}
