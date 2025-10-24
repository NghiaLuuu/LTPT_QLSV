# 🌱 HỆ THỐNG TỰ ĐỘNG TẠO DỮ LIỆU MẪU

## ✅ ĐÃ HOÀN THÀNH

Hệ thống đã được cấu hình để **tự động tạo dữ liệu mẫu** khi khởi động Server (nếu chưa có):

### 📊 Dữ liệu được tạo tự động:

#### 10 Sinh viên:
| Mã SV | Họ tên | Ngày sinh | Giới tính | Chuyên ngành | Lớp | Username | Password |
|-------|--------|-----------|-----------|--------------|-----|----------|----------|
| SV001 | Nguyễn Văn An | 15/01/2003 | NAM | CNTT | DHKTPM16A | sv001 | 123456 |
| SV002 | Trần Thị Bích | 20/02/2003 | NỮ | CNTT | DHKTPM16A | sv002 | 123456 |
| SV003 | Lê Văn Cường | 10/03/2003 | NAM | CNTT | DHKTPM16B | sv003 | 123456 |
| SV004 | Phạm Thị Dung | 25/04/2003 | NỮ | HTTT | DHHTTT16A | sv004 | 123456 |
| SV005 | Hoàng Văn Em | 30/05/2003 | NAM | KHMT | DHKHMT16A | sv005 | 123456 |
| SV006 | Võ Thị Phượng | 15/06/2003 | NỮ | CNTT | DHKTPM16B | sv006 | 123456 |
| SV007 | Đặng Văn Giang | 20/07/2003 | NAM | HTTT | DHHTTT16A | sv007 | 123456 |
| SV008 | Bùi Thị Hoa | 05/08/2003 | NỮ | KHMT | DHKHMT16A | sv008 | 123456 |
| SV009 | Phan Văn Inh | 12/09/2003 | NAM | CNTT | DHKTPM16A | sv009 | 123456 |
| SV010 | Ngô Thị Kim | 18/10/2003 | NỮ | CNTT | DHKTPM16B | sv010 | 123456 |

#### 10 Giảng viên:
| Mã GV | Họ tên | Học vị | Khoa | Username | Password |
|-------|--------|--------|------|----------|----------|
| GV001 | TS. Nguyễn Văn Giáo | Tiến sĩ | CNTT | gv001 | 123456 |
| GV002 | TS. Trần Thị Hương | Tiến sĩ | CNTT | gv002 | 123456 |
| GV003 | ThS. Lê Văn Kiên | Thạc sĩ | CNTT | gv003 | 123456 |
| GV004 | TS. Phạm Thị Lan | Tiến sĩ | KHMT | gv004 | 123456 |
| GV005 | ThS. Hoàng Văn Minh | Thạc sĩ | CNTT | gv005 | 123456 |
| GV006 | TS. Võ Thị Nga | Tiến sĩ | HTTT | gv006 | 123456 |
| GV007 | ThS. Đặng Văn Oanh | Thạc sĩ | CNTT | gv007 | 123456 |
| GV008 | TS. Bùi Thị Phượng | Tiến sĩ | KHMT | gv008 | 123456 |
| GV009 | ThS. Phan Văn Quang | Thạc sĩ | HTTT | gv009 | 123456 |
| GV010 | TS. Ngô Thị Rượu | Tiến sĩ | CNTT | gv010 | 123456 |

#### Tài khoản Admin:
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |

---

## 🚀 CÁCH SỬ DỤNG

### Bước 1: Khởi động Server
```cmd
mvn clean compile
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"
```

### Bước 2: Xem Log
Khi Server khởi động, bạn sẽ thấy log như sau:

```
========================================
🌱 STARTING DATA SEEDING
========================================
🌱 Starting to seed student data...
  ✓ Created student: SV001 - Nguyễn Văn An
  ✓ Created student: SV002 - Trần Thị Bích
  ... (tổng 10 sinh viên)
🌱 Student seeding completed: 10 created, 0 skipped

🌱 Starting to seed teacher data...
  ✓ Created teacher: GV001 - TS. Nguyễn Văn Giáo
    → Created account: gv001 / 123456
  ... (tổng 10 giảng viên)
🌱 Teacher seeding completed: 10 created, 0 skipped

🌱 Starting to seed student accounts...
  ✓ Created account for SV001: sv001 / 123456
  ... (tổng 10 tài khoản)
🌱 Student accounts seeding completed: 10 created, 0 skipped

========================================
🌱 DATA SEEDING COMPLETED SUCCESSFULLY
========================================
```

### Bước 3: Khởi động Client và Test
```cmd
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.ClientMain"
```

Đăng nhập với:
- **Admin**: admin / admin123
- **Sinh viên**: sv001 / 123456 (hoặc sv002, sv003, ... sv010)
- **Giảng viên**: gv001 / 123456 (hoặc gv002, gv003, ... gv010)

---

## 🔄 CHẠY LẠI LẦN 2

Nếu bạn khởi động Server lần 2, hệ thống sẽ:
- ✅ **Kiểm tra** xem dữ liệu đã tồn tại chưa
- ⊘ **Bỏ qua** nếu đã có (không tạo trùng)
- ✓ **Chỉ tạo** những dữ liệu chưa có

Log sẽ hiển thị:
```
🌱 Student seeding completed: 0 created, 10 skipped
🌱 Teacher seeding completed: 0 created, 10 skipped
🌱 Student accounts seeding completed: 0 created, 10 skipped
```

---

## 📁 CÁC FILE LIÊN QUAN

1. **DataSeeder.java** (MỚI)
   - Path: `src/main/java/iuh/fit/se/server/util/DataSeeder.java`
   - Chức năng: Tạo 10 SV, 10 GV, và tài khoản tương ứng

2. **Server.java** (ĐÃ CẬP NHẬT)
   - Thêm: `DataSeeder.seedAll(studentService);`
   - Gọi tự động sau khi khởi tạo JPA

3. **AuthService.java** (ĐÃ CẬP NHẬT)
   - Thêm: `createStudentAccount()`
   - Thêm: `createTeacherAccount()`

---

## 🎯 TÍNH NĂNG NỔI BẬT

✅ **Tự động hoàn toàn**: Không cần chạy script SQL riêng
✅ **An toàn**: Kiểm tra trùng lặp trước khi tạo
✅ **Idempotent**: Chạy bao nhiêu lần cũng được, không bị duplicate
✅ **Logging rõ ràng**: Biết được bao nhiêu dữ liệu được tạo/bỏ qua
✅ **Password mã hóa**: Sử dụng BCrypt hash

---

## 🧪 TEST NGAY

### Test Case 1: Đăng nhập sinh viên
1. Khởi động Server
2. Khởi động Client
3. Login: `sv001` / `123456`
4. ✓ Thấy StudentMainFrame với 4 tabs
5. ✓ Tab "Thông tin cá nhân" hiển thị: Nguyễn Văn An

### Test Case 2: Đăng nhập giảng viên
1. Login: `gv001` / `123456`
2. ✓ Thấy TeacherMainFrame với 3 tabs
3. ✓ Hiển thị: TS. Nguyễn Văn Giáo

### Test Case 3: Đăng nhập admin
1. Login: `admin` / `admin123`
2. ✓ Thấy AdminMainFrame với 7 tabs
3. ✓ Tab "Quản lý Sinh viên" có 10 sinh viên
4. ✓ Tab "Quản lý Giảng viên" có 10 giảng viên

---

## 💡 LƯU Ý

- Dữ liệu sẽ được tạo **TỰ ĐỘNG** khi Server khởi động
- Nếu muốn xóa và tạo lại, hãy xóa dữ liệu trong database trước
- Mật khẩu mặc định cho tất cả tài khoản test: `123456`
- Điểm trung bình của sinh viên: random từ 7.0 đến 9.5

---

**Tác giả**: AI Assistant  
**Ngày tạo**: 24/10/2025  
**Phiên bản**: 1.0

