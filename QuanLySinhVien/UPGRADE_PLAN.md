# KẾ HOẠCH NÂNG CẤP HỆ THỐNG QUẢN LÝ ĐÀO TẠO

## Ngày tạo: 24/10/2025

---

## 📋 TÌNH TRẠNG HIỆN TẠI

### ✅ Đã có (Hoạt động tốt):
1. **Kiến trúc Client-Server**: Socket-based communication
2. **Xác thực & Phân quyền**: Login với 3 roles (ADMIN, GIANG_VIEN, SINH_VIEN)
3. **Backend Services**: 
   - AuthService (xác thực)
   - KhoaService, LopHocService, MonHocService, HocKyService
   - GiangVienService, StudentService
   - LopHocPhanService
4. **Admin Panel**: AdminMainFrame có các tabs:
   - ✅ Quản lý Khoa (KhoaPanel)
   - ✅ Quản lý Lớp học (LopHocPanel)
   - ✅ Quản lý Giảng viên (GiangVienPanel)
   - ✅ Quản lý Môn học (MonHocPanel)
   - ✅ Quản lý Học kỳ (HocKyPanel)
   - ✅ Quản lý Sinh viên (SinhVienPanel)
   - ✅ Quản lý Lớp học phần (LopHocPhanPanel)

### ❌ VẤN ĐỀ CẦN KHẮC PHỤC:

#### 1. **PHÂN GIAO DIỆN THEO ROLE - QUAN TRỌNG NHẤT**
   - **Hiện tại**: 
     - Admin và Giảng viên đều mở `AdminMainFrame` (không phân biệt)
     - Sinh viên mở `MainFrame` cũ (chỉ có chức năng xem danh sách sinh viên đơn giản)
   - **Vấn đề**: 
     - GIANG_VIEN không nên thấy tất cả tabs như ADMIN
     - SINH_VIEN cần giao diện riêng để xem điểm, đăng ký học phần
     - MainFrame cũ không sử dụng AuthResponse, không có chức năng phù hợp với sinh viên

#### 2. **GIAO DIỆN ADMIN - CHƯA ĐẦY ĐỦ**
   - ❌ **Thiếu Panel CRUD Giảng viên**: GiangVienPanel đã có nhưng cần kiểm tra đầy đủ
   - ❌ **Thiếu Panel CRUD Sinh viên nâng cao**: SinhVienPanel cần liên kết với Lớp học
   - ❌ **Thiếu quản lý Tài khoản**: Tạo tài khoản cho SV, GV

#### 3. **GIAO DIỆN GIẢNG VIÊN - CHƯA CÓ**
   - ❌ Xem danh sách lớp học phần mình dạy
   - ❌ Xem danh sách sinh viên trong lớp
   - ❌ Nhập điểm cho sinh viên
   - ❌ Thống kê điểm

#### 4. **GIAO DIỆN SINH VIÊN - CHƯA HOÀN THIỆN**
   - ❌ Xem thông tin cá nhân
   - ❌ Xem lịch học/thời khóa biểu
   - ❌ Đăng ký học phần
   - ❌ Xem bảng điểm (transcript)
   - ❌ Xem lớp học phần đã đăng ký

#### 5. **BACKEND - THIẾU MỘT SỐ CHỨC NĂNG**
   - ⚠️ Commands đã định nghĩa nhưng chưa implement:
     - `SV_GET_LOPHOCPHAN_TO_REGISTER`
     - `SV_REGISTER_COURSE`
     - `SV_GET_TRANSCRIPT`
     - `GV_GET_MY_CLASSES`
     - `GV_GET_STUDENT_LIST_OF_CLASS`
     - `GV_ENTER_GRADE`
     - `TAIKHOAN_*` (quản lý tài khoản)

---

## 🎯 KẾ HOẠCH NÂNG CẤP CHI TIẾT

### PHASE 1: PHÂN CHIA GIAO DIỆN THEO ROLE ⭐⭐⭐ (Ưu tiên cao nhất)

#### 1.1. Tạo giao diện riêng cho Giảng viên
**File mới**: `TeacherMainFrame.java`
- Header hiển thị thông tin giảng viên
- Tabs:
  - 📚 Lớp học phần của tôi
  - 👨‍🎓 Danh sách sinh viên
  - 📝 Nhập điểm
  - 📊 Thống kê

#### 1.2. Nâng cấp giao diện Sinh viên
**File cần sửa**: `MainFrame.java` → đổi tên thành `StudentMainFrame.java`
- Redesign hoàn toàn
- Tabs:
  - 👤 Thông tin cá nhân
  - 📅 Đăng ký học phần
  - 📖 Lớp học phần đã đăng ký
  - 🎓 Bảng điểm
  - 📊 Thống kê học tập

#### 1.3. Cập nhật LoginFrame
**File cần sửa**: `LoginFrame.java`
- Thay đổi logic điều hướng sau login:
  - ADMIN → AdminMainFrame (full access)
  - GIANG_VIEN → TeacherMainFrame
  - SINH_VIEN → StudentMainFrame

#### 1.4. Điều chỉnh AdminMainFrame
**File cần sửa**: `AdminMainFrame.java`
- Đảm bảo chỉ ADMIN mới thấy tất cả tabs
- Thêm tab quản lý Tài khoản

---

### PHASE 2: HOÀN THIỆN BACKEND SERVICES

#### 2.1. Implement Services cho Sinh viên
**File mới**: `StudentCourseService.java`
- `getLopHocPhanAvailableForRegistration(String maSV, String maHocKy)`
- `registerCourse(String maSV, String maLHP)`
- `getTranscript(String maSV)`
- `getRegisteredCourses(String maSV, String maHocKy)`

#### 2.2. Implement Services cho Giảng viên
**File mới**: `TeacherCourseService.java`
- `getMyClasses(String maGV)`
- `getStudentListOfClass(String maLHP)`
- `enterGrade(String maLHP, String maSV, double diemGK, double diemCK, double diemKhac)`
- `updateGrade(String maLHP, String maSV, double diemGK, double diemCK, double diemKhac)`

#### 2.3. Quản lý Tài khoản
**File mới**: `AccountService.java`
- `createAccountForStudent(String maSV, String username, String password)`
- `createAccountForTeacher(String maGV, String username, String password)`
- `changePassword(String username, String oldPassword, String newPassword)`
- `resetPassword(String username, String newPassword)` // Chỉ admin
- `deleteAccount(String username)`

#### 2.4. Cập nhật ClientHandler
**File cần sửa**: `ClientHandler.java`
- Thêm handlers cho các commands mới
- Đảm bảo kiểm tra quyền đúng cho từng command

---

### PHASE 3: TẠO CÁC PANEL MỚI

#### 3.1. Panels cho Giảng viên
**Files mới**:
- `TeacherClassListPanel.java` - Danh sách lớp dạy
- `TeacherStudentListPanel.java` - Danh sách sinh viên trong lớp
- `TeacherGradeEntryPanel.java` - Nhập điểm
- `TeacherStatisticsPanel.java` - Thống kê điểm

#### 3.2. Panels cho Sinh viên
**Files mới**:
- `StudentProfilePanel.java` - Thông tin cá nhân
- `StudentCourseRegistrationPanel.java` - Đăng ký học phần
- `StudentRegisteredCoursesPanel.java` - Các môn đã đăng ký
- `StudentTranscriptPanel.java` - Bảng điểm
- `StudentStatisticsPanel.java` - Thống kê (GPA, tín chỉ tích lũy)

#### 3.3. Panel quản lý Tài khoản cho Admin
**File mới**:
- `AccountManagementPanel.java` - CRUD tài khoản (Admin only)

---

### PHASE 4: CẢI THIỆN UX/UI

#### 4.1. Modern Components
- Sử dụng FlatLaf hoặc Material UI cho Java Swing
- Icon set thống nhất (Font Awesome for Swing)
- Color scheme nhất quán

#### 4.2. Validation & Error Handling
- Validate input trước khi gửi request
- Hiển thị lỗi rõ ràng, dễ hiểu
- Loading indicators cho các thao tác dài

#### 4.3. Responsive Design
- Điều chỉnh layout theo kích thước màn hình
- Scroll panels cho nội dung dài

---

### PHASE 5: BẢO MẬT & PERFORMANCE

#### 5.1. Bảo mật
- Mã hóa mật khẩu (BCrypt)
- Session timeout
- Log các thao tác quan trọng
- Validate dữ liệu ở cả client và server

#### 5.2. Performance
- Connection pooling cho database
- Cache dữ liệu thường xuyên truy vấn
- Lazy loading cho bảng lớn
- Pagination cho danh sách dài

#### 5.3. Error Recovery
- Reconnection mechanism khi mất kết nối
- Transaction rollback khi lỗi
- Backup & restore

---

## 📊 DANH SÁCH FILES CẦN TẠO MỚI

### Backend (Server):
1. ✅ `StudentCourseService.java`
2. ✅ `TeacherCourseService.java`
3. ✅ `AccountService.java`
4. ✅ `DiemService.java` (nếu chưa có)

### Frontend - Teacher:
5. ✅ `TeacherMainFrame.java`
6. ✅ `TeacherClassListPanel.java`
7. ✅ `TeacherStudentListPanel.java`
8. ✅ `TeacherGradeEntryPanel.java`
9. ✅ `TeacherStatisticsPanel.java`

### Frontend - Student:
10. ✅ `StudentMainFrame.java` (rename MainFrame.java)
11. ✅ `StudentProfilePanel.java`
12. ✅ `StudentCourseRegistrationPanel.java`
13. ✅ `StudentRegisteredCoursesPanel.java`
14. ✅ `StudentTranscriptPanel.java`
15. ✅ `StudentStatisticsPanel.java`

### Frontend - Admin:
16. ✅ `AccountManagementPanel.java`

### Dialogs:
17. ✅ `GradeEntryDialog.java`
18. ✅ `CourseRegistrationDialog.java`
19. ✅ `AccountDialog.java`
20. ✅ `ChangePasswordDialog.java`

---

## 📝 DANH SÁCH FILES CẦN CẬP NHẬT

1. ✅ `LoginFrame.java` - Điều hướng theo role
2. ✅ `AdminMainFrame.java` - Thêm tab quản lý tài khoản
3. ✅ `ClientHandler.java` - Thêm handlers cho commands mới
4. ✅ `Command.java` - Đã có đủ commands
5. ✅ `SinhVienPanel.java` - Cải thiện, liên kết với lớp học
6. ✅ `GiangVienPanel.java` - Kiểm tra và hoàn thiện

---

## 🚀 THỨ TỰ THỰC HIỆN (Đề xuất)

### Sprint 1 (2-3 ngày):
1. ✅ Tạo TeacherMainFrame + các panels cơ bản
2. ✅ Tạo StudentMainFrame mới + các panels cơ bản
3. ✅ Cập nhật LoginFrame để điều hướng đúng
4. ✅ Test phân quyền giao diện

### Sprint 2 (2-3 ngày):
5. ✅ Implement StudentCourseService
6. ✅ Implement TeacherCourseService
7. ✅ Cập nhật ClientHandler
8. ✅ Test các chức năng backend

### Sprint 3 (2-3 ngày):
9. ✅ Hoàn thiện Student panels (đăng ký HP, xem điểm)
10. ✅ Hoàn thiện Teacher panels (nhập điểm)
11. ✅ Test end-to-end workflows

### Sprint 4 (1-2 ngày):
12. ✅ Implement AccountService
13. ✅ Tạo AccountManagementPanel cho Admin
14. ✅ Test quản lý tài khoản

### Sprint 5 (1-2 ngày):
15. ✅ Cải thiện UI/UX
16. ✅ Validation & error handling
17. ✅ Testing tổng thể
18. ✅ Documentation

---

## 🎓 TÍNH NĂNG CHI TIẾT THEO ROLE

### ADMIN (Quản trị viên):
- ✅ Quản lý Khoa, Lớp học, Môn học, Học kỳ
- ✅ Quản lý Giảng viên (CRUD)
- ✅ Quản lý Sinh viên (CRUD)
- ✅ Quản lý Lớp học phần
- ✅ Quản lý Tài khoản (tạo, xóa, reset password)
- ✅ Xem tất cả danh sách
- ✅ Báo cáo thống kê tổng quan

### GIẢNG VIÊN:
- ✅ Xem thông tin cá nhân
- ✅ Xem danh sách lớp học phần được phân công
- ✅ Xem danh sách sinh viên trong từng lớp
- ✅ Nhập/Sửa điểm cho sinh viên
- ✅ Xem thống kê điểm của lớp
- ✅ Đổi mật khẩu

### SINH VIÊN:
- ✅ Xem thông tin cá nhân
- ✅ Đăng ký học phần (theo học kỳ)
- ✅ Hủy đăng ký học phần (trong thời gian cho phép)
- ✅ Xem lịch học/thời khóa biểu
- ✅ Xem bảng điểm (từng môn, từng học kỳ)
- ✅ Xem điểm trung bình (GPA)
- ✅ Xem tín chỉ tích lũy
- ✅ Đổi mật khẩu

---

## 🔍 CHECKLIST KIỂM TRA

### Phân quyền:
- [ ] Admin thấy tất cả tabs
- [ ] Giảng viên chỉ thấy chức năng của mình
- [ ] Sinh viên chỉ thấy chức năng của mình
- [ ] Backend kiểm tra quyền cho mọi request

### Chức năng CRUD:
- [ ] Admin CRUD Sinh viên (đầy đủ)
- [ ] Admin CRUD Giảng viên (đầy đủ)
- [ ] Admin CRUD Tài khoản

### Chức năng nghiệp vụ:
- [ ] Sinh viên đăng ký học phần
- [ ] Sinh viên xem bảng điểm
- [ ] Giảng viên nhập điểm
- [ ] Giảng viên xem danh sách lớp

### UI/UX:
- [ ] Giao diện đẹp, hiện đại
- [ ] Icons và màu sắc nhất quán
- [ ] Loading indicators
- [ ] Error messages rõ ràng
- [ ] Responsive design

### Bảo mật:
- [ ] Mật khẩu được mã hóa
- [ ] Session management
- [ ] Authorization checks
- [ ] Input validation

---

## 💡 GỢI Ý CẢI TIẾN THÊM (Tương lai)

1. **Export/Import Excel**: Xuất danh sách, điểm ra Excel
2. **Email notification**: Thông báo điểm, lịch học
3. **Mobile app**: Phát triển app di động
4. **Real-time updates**: WebSocket cho cập nhật realtime
5. **File upload**: Upload ảnh đại diện, tài liệu
6. **Chat/Forum**: Giao tiếp giữa GV-SV
7. **Calendar integration**: Tích hợp lịch Google
8. **Dashboard**: Biểu đồ thống kê đẹp hơn
9. **Multi-language**: Hỗ trợ nhiều ngôn ngữ
10. **Dark mode**: Chế độ tối

---

## 📞 HỖ TRỢ

Tài liệu này được tạo tự động bởi AI Assistant.
Nếu cần hỗ trợ, vui lòng liên hệ team phát triển.

**Phiên bản**: 1.0  
**Ngày cập nhật**: 24/10/2025

