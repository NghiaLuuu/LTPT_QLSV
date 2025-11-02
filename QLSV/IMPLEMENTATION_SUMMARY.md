# Tóm tắt các thay đổi - Thêm sinh viên vào môn học

## Ngày: 3 tháng 11, 2025

### Yêu cầu
Thêm tính năng cho phép Admin chọn sinh viên và thêm vào môn học khi **Sửa môn học**, sau đó gửi sự kiện WebSocket sang giao diện Sinh viên để cập nhật danh sách **Môn học đã đăng ký** ngay lập tức.

---

## Các file đã được chỉnh sửa

### 1. **SubjectRequest.java** ✅
**Đường dẫn:** `src/main/java/iuh/fit/se/dto/request/SubjectRequest.java`

**Thay đổi:**
- Thêm field `List<Long> studentIds` để nhận danh sách ID sinh viên cần thêm vào môn học

```java
// Danh sách ID sinh viên để thêm vào môn học
private List<Long> studentIds;
```

---

### 2. **EnrollmentRepository.java** ✅
**Đường dẫn:** `src/main/java/iuh/fit/se/repository/EnrollmentRepository.java`

**Thay đổi:**
- Thêm method `existsByStudentIdAndSubjectId` để kiểm tra sinh viên đã đăng ký môn học chưa

```java
boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
```

---

### 3. **SubjectServiceImpl.java** ✅
**Đường dẫn:** `src/main/java/iuh/fit/se/service/impl/SubjectServiceImpl.java`

**Thay đổi:**
- Inject thêm các dependencies: `StudentRepository`, `EnrollmentRepository`, `SimpMessagingTemplate`, `StudentService`
- Cập nhật method `updateSubject()` để:
  - Xử lý danh sách `studentIds` từ request
  - Tạo `Enrollment` mới cho mỗi sinh viên được chọn
  - Gửi WebSocket notification đến từng sinh viên với topic: `/topic/student/{username}/enrollments`
  - Gửi dữ liệu `StudentDashboardResponse` để cập nhật toàn bộ dashboard

```java
// Xử lý thêm sinh viên vào môn học
if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
    String currentSemester = getCurrentSemester();
    
    for (Long studentId : request.getStudentIds()) {
        // Kiểm tra sinh viên đã đăng ký chưa
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndSubjectId(studentId, id);
        
        if (!alreadyEnrolled) {
            // Tạo enrollment mới
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setSubject(updatedSubject);
            enrollment.setSemester(currentSemester);
            enrollment.setGrade(null);
            enrollmentRepository.save(enrollment);
            
            // Gửi WebSocket notification
            StudentDashboardResponse dashboardData = studentService.getStudentDashboard(student.getStudentCode());
            messagingTemplate.convertAndSend("/topic/student/" + studentUsername + "/enrollments", dashboardData);
        }
    }
}
```

- Thêm method `getCurrentSemester()` để tự động xác định học kỳ hiện tại

---

### 4. **SubjectDialog.java** ✅
**Đường dẫn:** `src/main/java/iuh/fit/se/gui/view/SubjectDialog.java`

**Thay đổi:**
- Thêm components: `studentTable`, `studentTableModel` để hiển thị danh sách sinh viên
- Thêm inner class `StudentItem` để lưu thông tin sinh viên
- Thêm phần giao diện chọn sinh viên **chỉ khi ở chế độ EDIT** (subject != null)
- Table có checkbox để chọn nhiều sinh viên cùng lúc
- Method `loadAvailableStudents()` load tất cả sinh viên từ API
- Method `saveSubject()` được cập nhật để:
  - Thu thập các sinh viên được chọn từ table
  - Thêm `studentIds` vào request data
  - Gọi API để cập nhật môn học

**Giao diện mới:**
```
┌────────────────────────────────────────┐
│ Sửa thông tin Môn học                  │
├────────────────────────────────────────┤
│ Mã môn học: MH00000001                 │
│ Tên môn học: [____________]            │
│ Số tín chỉ: [3]                        │
│ Số lượng SV tối đa: [50]               │
│ Giảng viên: [Chọn giảng viên]          │
│                                        │
│ Thêm sinh viên vào môn học             │
│ ┌──────────────────────────────────┐  │
│ │ [✓] | SV00000001 | Nguyễn Văn A  │  │
│ │ [ ] | SV00000002 | Trần Thị B    │  │
│ │ [✓] | SV00000003 | Lê Văn C      │  │
│ └──────────────────────────────────┘  │
│                                        │
│                     [Hủy]    [Lưu]    │
└────────────────────────────────────────┘
```

---

### 5. **StudentDashboardFrame.java** ✅
**Đường dẫn:** `src/main/java/iuh/fit/se/gui/view/StudentDashboardFrame.java`

**Thay đổi:**
- Cập nhật method `setupWebSocket()` để subscribe thêm topic mới:
  - `/topic/student/{username}/enrollments` - nhận cập nhật môn học đã đăng ký

- Thêm method `handleEnrollmentUpdate()` để xử lý WebSocket message:
  - Cập nhật `studentData` với dữ liệu mới
  - Hiển thị notification cho sinh viên
  - Tự động refresh màn hình "Môn học đã đăng ký"

```java
private void handleEnrollmentUpdate(StudentDashboardResponse dashboardData) {
    SwingUtilities.invokeLater(() -> {
        try {
            // Cập nhật studentData
            String json = ApiClient.getObjectMapper().writeValueAsString(dashboardData);
            studentData = ApiClient.getObjectMapper().readTree(json);

            // Hiển thị notification
            JOptionPane.showMessageDialog(this,
                    "Bạn đã được thêm vào môn học mới bởi quản trị viên!",
                    "Cập nhật môn học",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh lại màn hình
            showEnrollmentsPanel();
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi xử lý cập nhật môn học");
        }
    });
}
```

---

## Luồng hoạt động

### 1. Admin thêm sinh viên vào môn học:
```
Admin App (SubjectDialog)
    │
    ├─> Chọn môn học để sửa
    │
    ├─> Chọn sinh viên từ danh sách (checkbox)
    │
    ├─> Nhấn [Lưu]
    │
    └─> API Call: PUT /api/subjects/{id}
         └─> Body: {
               "code": "MH00000001",
               "name": "Lập trình Java",
               "credit": 3,
               "studentIds": [1, 2, 3]  // ← Danh sách ID sinh viên
             }
```

### 2. Backend xử lý:
```
SubjectServiceImpl.updateSubject()
    │
    ├─> Cập nhật thông tin môn học
    │
    ├─> Lặp qua từng studentId:
    │   ├─> Kiểm tra đã đăng ký chưa?
    │   ├─> Nếu chưa → Tạo Enrollment mới
    │   └─> Gửi WebSocket đến sinh viên:
    │       Topic: /topic/student/{username}/enrollments
    │       Data: StudentDashboardResponse (toàn bộ dashboard)
    │
    └─> Log: "📢 [WEBSOCKET] Đã gửi thông báo cập nhật môn học"
```

### 3. Student App nhận cập nhật:
```
StudentDashboardFrame
    │
    ├─> WebSocket nhận message từ topic:
    │   /topic/student/{username}/enrollments
    │
    ├─> handleEnrollmentUpdate() được gọi
    │
    ├─> Cập nhật studentData
    │
    ├─> Hiển thị JOptionPane:
    │   "Bạn đã được thêm vào môn học mới!"
    │
    └─> Tự động refresh màn hình "Môn học đã đăng ký"
```

---

## WebSocket Topics

### Topic được sử dụng:
1. **`/topic/student/{username}`** - Cập nhật thông tin cá nhân sinh viên
2. **`/topic/student/{username}/enrollments`** - Cập nhật môn học đã đăng ký ⭐ **MỚI**
3. **`/topic/students`** - Broadcast cho tất cả admin

---

## Cách test

### Test Case 1: Thêm sinh viên vào môn học
1. **Login với role ADMIN**
2. Vào **Quản lý môn học**
3. Chọn một môn học và nhấn **Sửa**
4. Trong dialog, kéo xuống phần "Thêm sinh viên vào môn học"
5. Chọn (tick) một hoặc nhiều sinh viên
6. Nhấn **Lưu**
7. Kiểm tra console: Phải thấy log "📢 [WEBSOCKET] Đã gửi thông báo..."

### Test Case 2: Sinh viên nhận cập nhật
1. **Login với role STUDENT** (tài khoản sinh viên vừa được thêm)
2. Chờ một chút, popup sẽ hiện: "Bạn đã được thêm vào môn học mới!"
3. Nhấn **OK**
4. Vào menu **Môn học đã đăng ký**
5. Kiểm tra: Môn học mới phải xuất hiện trong danh sách

### Test Case 3: Không thêm sinh viên đã đăng ký
1. Thử thêm cùng một sinh viên vào cùng môn học lần 2
2. Hệ thống phải bỏ qua (không tạo enrollment trùng)
3. Không gửi WebSocket notification

---

## Ghi chú quan trọng

### ⚠️ Lưu ý:
1. **Chỉ hiển thị phần chọn sinh viên khi EDIT môn học** (không hiện khi thêm mới)
2. **Tự động detect học kỳ hiện tại** dựa vào tháng:
   - Tháng 9-1: HK1-{năm}
   - Tháng 2-6: HK2-{năm}
   - Tháng 7-8: HKH-{năm} (Học kỳ hè)
3. **WebSocket phải được kết nối** trên cả Admin App và Student App
4. **Enrollment được tạo với grade = null** (chưa có điểm)

### 🔍 Debug:
- Kiểm tra console log để xem WebSocket có gửi không
- Kiểm tra database table `enrollments` sau khi thêm
- Kiểm tra Student App có subscribe đúng topic không

---

## Tương tự với tính năng hiện có

Tính năng này được implement **TƯƠNG TỰ** với:
- **Quản lý sinh viên** → Khi Admin sửa thông tin sinh viên
- **WebSocket** → Gửi notification đến topic `/topic/student/{username}`
- **Student App** → Tự động refresh màn hình "Thông tin cá nhân"

---

## Status: ✅ HOÀN THÀNH

Tất cả các file đã được chỉnh sửa và sẵn sàng để test!

