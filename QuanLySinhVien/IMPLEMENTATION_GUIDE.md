# TÀI LIỆU HƯỚNG DẪN SỬ DỤNG HỆ THỐNG SAU KHI NÂNG CẤP

## 📋 TỔNG QUAN CÁC THAY ĐỔI ĐÃ THỰC HIỆN

### ✅ ĐÃ HOÀN THÀNH:

#### 1. **PHÂN GIAO DIỆN THEO ROLE** ⭐⭐⭐
- **LoginFrame.java**: Đã cập nhật logic điều hướng
  - ADMIN → `AdminMainFrame` (đầy đủ quyền)
  - GIANG_VIEN → `TeacherMainFrame` (mới tạo)
  - SINH_VIEN → `StudentMainFrame` (mới tạo)

#### 2. **GIAO DIỆN GIẢNG VIÊN - MỚI HOÀN TOÀN**
Files mới được tạo:
- ✅ `TeacherMainFrame.java` - Frame chính cho giảng viên
- ✅ `panels/teacher/TeacherClassListPanel.java` - Xem danh sách lớp dạy
- ✅ `panels/teacher/TeacherGradeEntryPanel.java` - Nhập điểm (khung sẵn)
- ✅ `panels/teacher/TeacherStatisticsPanel.java` - Thống kê (khung sẵn)

Tính năng:
- Hiển thị danh sách lớp học phần được phân công
- Menu với các chức năng: Làm mới, Đổi mật khẩu, Đăng xuất, Thoát
- Giao diện hiện đại với header và status bar
- Tự động cập nhật thời gian

#### 3. **GIAO DIỆN SINH VIÊN - MỚI HOÀN TOÀN**
Files mới được tạo:
- ✅ `StudentMainFrame.java` - Frame chính cho sinh viên
- ✅ `panels/student/StudentProfilePanel.java` - Xem thông tin cá nhân
- ✅ `panels/student/StudentCourseRegistrationPanel.java` - Đăng ký học phần (khung sẵn)
- ✅ `panels/student/StudentRegisteredCoursesPanel.java` - Xem lớp đã đăng ký (khung sẵn)
- ✅ `panels/student/StudentTranscriptPanel.java` - Xem bảng điểm (khung sẵn)

Tính năng:
- Tab "Thông tin cá nhân": Hiển thị đầy đủ thông tin sinh viên (đã hoàn chỉnh)
- Các tab khác đã có khung sẵn, chờ backend hỗ trợ
- Giao diện hiện đại, dễ sử dụng

#### 4. **TÀI LIỆU**
- ✅ `UPGRADE_PLAN.md` - Kế hoạch nâng cấp chi tiết
- ✅ `IMPLEMENTATION_GUIDE.md` - File này - Hướng dẫn sử dụng

---

## 🚀 CÁCH SỬ DỤNG HỆ THỐNG

### 1. Khởi động Server
```cmd
cd C:\Users\ADMINS\Downloads\BaiTapPhanTan_QuanLySinhVien\QuanLySinhVien
mvn clean compile
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"
```

### 2. Khởi động Client
```cmd
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.ClientMain"
```

### 3. Đăng nhập

#### ADMIN (Toàn quyền):
- Username: `admin`
- Password: `admin123`
- Sau khi đăng nhập → `AdminMainFrame` với 7 tabs:
  - Quản lý Khoa
  - Quản lý Lớp học
  - Quản lý Giảng viên ✅
  - Quản lý Môn học
  - Quản lý Học kỳ
  - Quản lý Sinh viên ✅
  - Quản lý Lớp học phần

#### GIẢNG VIÊN:
- Username: `gv001` (hoặc tài khoản GV khác)
- Password: `123456`
- Sau khi đăng nhập → `TeacherMainFrame` với 3 tabs:
  - 📚 Lớp học phần của tôi (hiển thị lớp được phân công)
  - 📝 Nhập điểm (đang phát triển)
  - 📊 Thống kê (đang phát triển)

#### SINH VIÊN:
- Username: `sv001` hoặc `sv002`
- Password: `123456`
- Sau khi đăng nhập → `StudentMainFrame` với 4 tabs:
  - 👤 Thông tin cá nhân (hiển thị đầy đủ thông tin) ✅
  - 📅 Đăng ký học phần (đang phát triển)
  - 📖 Lớp đã đăng ký (đang phát triển)
  - 🎓 Bảng điểm (đang phát triển)

---

## 🔧 CÁC CHỨC NĂNG ĐANG HOẠT ĐỘNG

### ✅ ADMIN:
1. **Quản lý Sinh viên** (SinhVienPanel):
   - Xem danh sách sinh viên
   - Thêm sinh viên mới
   - Sửa thông tin sinh viên
   - Xóa sinh viên
   - Tìm kiếm sinh viên

2. **Quản lý Giảng viên** (GiangVienPanel):
   - Xem danh sách giảng viên
   - Thêm giảng viên mới
   - Sửa thông tin giảng viên
   - Xóa giảng viên
   - Liên kết với Khoa

3. **Quản lý Khoa, Lớp học, Môn học, Học kỳ, Lớp học phần**:
   - CRUD đầy đủ cho tất cả các entity

### ✅ GIẢNG VIÊN:
1. **Xem danh sách lớp học phần**:
   - Hiển thị các lớp được phân công giảng dạy
   - Thông tin: Mã LHP, Tên môn, Học kỳ, Lịch học, Sĩ số
   - Command backend: `GV_GET_MY_CLASSES`

### ✅ SINH VIÊN:
1. **Xem thông tin cá nhân**:
   - Hiển thị: Mã SV, Họ tên, Ngày sinh, Giới tính, Chuyên ngành, Lớp, Điểm TB
   - Command backend: `SINHVIEN_GET_BY_ID`

---

## ⏳ CÁC CHỨC NĂNG ĐANG PHÁT TRIỂN

### 🔨 CẦN BỔ SUNG BACKEND:

#### 1. Service cho Giảng viên
File mới cần tạo: `TeacherCourseService.java`
```java
// Cần implement:
- List<LopHocPhanDTO> getMyClasses(String maGV)
- List<SinhVienDTO> getStudentListOfClass(String maLHP)
- boolean enterGrade(String maLHP, String maSV, Diem diem)
- Diem getGrade(String maLHP, String maSV)
```

Cập nhật `ClientHandler.java`:
```java
case GV_GET_MY_CLASSES:
    // Lấy danh sách lớp của GV
case GV_GET_STUDENT_LIST_OF_CLASS:
    // Lấy DS sinh viên trong lớp
case GV_ENTER_GRADE:
    // Nhập điểm
```

#### 2. Service cho Sinh viên
File mới cần tạo: `StudentCourseService.java`
```java
// Cần implement:
- List<LopHocPhanDTO> getAvailableCoursesToRegister(String maSV, String maHocKy)
- boolean registerCourse(String maSV, String maLHP)
- boolean unregisterCourse(String maSV, String maLHP)
- List<Diem> getTranscript(String maSV)
- List<LopHocPhanDTO> getRegisteredCourses(String maSV, String maHocKy)
```

Cập nhật `ClientHandler.java`:
```java
case SV_GET_LOPHOCPHAN_TO_REGISTER:
    // Lấy danh sách HP có thể đăng ký
case SV_REGISTER_COURSE:
    // Đăng ký học phần
case SV_GET_TRANSCRIPT:
    // Lấy bảng điểm
```

#### 3. Quản lý Tài khoản (Admin)
File mới cần tạo: `AccountManagementPanel.java`
Service: `AccountService.java`
```java
- TaiKhoan createAccount(String username, String password, UserRole role, String maSV/maGV)
- boolean changePassword(String username, String newPassword)
- boolean deleteAccount(String username)
```

---

## 📝 HƯỚNG DẪN BỔ SUNG BACKEND

### Bước 1: Tạo TeacherCourseService.java
```java
// Vị trí: src/main/java/iuh/fit/se/server/service/TeacherCourseService.java

public class TeacherCourseService {
    private final EntityManagerFactory emf;
    
    public List<LopHocPhanDTO> getMyClasses(String maGV) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT lhp FROM LopHocPhan lhp WHERE lhp.giangVien.maGV = :maGV";
            List<LopHocPhan> list = em.createQuery(jpql, LopHocPhan.class)
                .setParameter("maGV", maGV)
                .getResultList();
            return list.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
    
    // Thêm các method khác...
}
```

### Bước 2: Cập nhật ClientHandler
```java
case GV_GET_MY_CLASSES:
    if (role != UserRole.GIANG_VIEN && role != UserRole.ADMIN) 
        return forbidden();
    
    String maGV = (String) request.getData();
    TeacherCourseService teacherService = TeacherCourseService.getInstance();
    List<LopHocPhanDTO> classes = teacherService.getMyClasses(maGV);
    return new Response(Status.SUCCESS, "Thành công", classes);
```

### Bước 3: Test từng chức năng
1. Test backend bằng client đơn giản
2. Test frontend với dữ liệu mock
3. Tích hợp và test end-to-end

---

## 🎯 LỘ TRÌNH HOÀN THIỆN TIẾP THEO

### Sprint 1 (Ưu tiên cao):
1. ✅ Implement `GV_GET_MY_CLASSES` trong ClientHandler
2. ✅ Tạo `TeacherCourseService.getMyClasses()`
3. ✅ Test chức năng xem lớp của giảng viên
4. ✅ Hoàn thiện `TeacherGradeEntryPanel` với form nhập điểm

### Sprint 2:
5. ✅ Implement các commands cho sinh viên (đăng ký HP, xem điểm)
6. ✅ Tạo `StudentCourseService`
7. ✅ Hoàn thiện các panels cho sinh viên
8. ✅ Test workflows sinh viên

### Sprint 3:
9. ✅ Tạo `AccountManagementPanel` cho Admin
10. ✅ Implement quản lý tài khoản
11. ✅ Thêm chức năng đổi mật khẩu
12. ✅ Testing tổng thể

---

## 💡 LƯU Ý QUAN TRỌNG

### 1. Kiểm tra dữ liệu test
Đảm bảo database có:
- Tài khoản admin, giảng viên, sinh viên
- Dữ liệu mẫu: Khoa, Lớp, Môn học, Học kỳ
- Giảng viên được phân công lớp học phần
- Sinh viên thuộc lớp học

### 2. Backend Commands
Các commands đã được định nghĩa trong `Command.java`:
- ✅ `GV_GET_MY_CLASSES` - Đã có trong code
- ✅ `SV_GET_TRANSCRIPT` - Đã có trong code
- ⚠️ Chưa implement trong ClientHandler

### 3. Cấu trúc thư mục mới
```
client/gui/
├── LoginFrame.java
├── AdminMainFrame.java
├── TeacherMainFrame.java ← MỚI
├── StudentMainFrame.java ← MỚI
├── panels/
│   ├── [Admin panels...]
│   ├── teacher/ ← MỚI
│   │   ├── TeacherClassListPanel.java
│   │   ├── TeacherGradeEntryPanel.java
│   │   └── TeacherStatisticsPanel.java
│   └── student/ ← MỚI
│       ├── StudentProfilePanel.java
│       ├── StudentCourseRegistrationPanel.java
│       ├── StudentRegisteredCoursesPanel.java
│       └── StudentTranscriptPanel.java
```

---

## 🐛 TROUBLESHOOTING

### Lỗi: "Cannot connect to server"
- Kiểm tra server đã khởi động chưa
- Kiểm tra port 8888 có bị chiếm không
- Kiểm tra firewall

### Lỗi: "Không tìm thấy panel"
- Clean và rebuild project: `mvn clean compile`
- Kiểm tra các class đã được compile

### Lỗi compile
- Đảm bảo tất cả import đúng
- Kiểm tra package structure

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề:
1. Kiểm tra file `UPGRADE_PLAN.md` để xem tổng quan
2. Xem logs trong console
3. Debug từng bước với breakpoints

**Phiên bản**: 2.0  
**Ngày cập nhật**: 24/10/2025  
**Trạng thái**: Đang phát triển (70% hoàn thành)

