# Hướng dẫn Chạy Hệ thống Quản lý Sinh viên - Phiên bản Nâng cao

## Tổng quan
Hệ thống Quản lý Đào tạo theo mô hình Client-Server với:
- **Backend**: Java Socket Server + JPA/Hibernate + SQL Server
- **Authentication**: BCrypt password hashing
- **Authorization**: Role-based access control (ADMIN, GIANG_VIEN, SINH_VIEN)

## Yêu cầu hệ thống

### 1. Phần mềm cần cài đặt
- **Java JDK 8** trở lên
- **SQL Server 2012** trở lên (hoặc SQL Server Express)
- **Maven** (hoặc sử dụng IDE tích hợp Maven như IntelliJ IDEA, Eclipse)

### 2. Cấu hình SQL Server

#### Bước 1: Tạo Database
```sql
CREATE DATABASE db_quanlysinhvien;
GO

USE db_quanlysinhvien;
GO
```

#### Bước 2: Tạo User (nếu cần)
```sql
-- Tạo login
CREATE LOGIN sa_user WITH PASSWORD = 'sapassword';
GO

-- Gán quyền
USE db_quanlysinhvien;
ALTER USER sa_user WITH DEFAULT_SCHEMA = dbo;
EXEC sp_addrolemember 'db_owner', 'sa_user';
GO
```

#### Bước 3: Cấu hình kết nối
Chỉnh sửa file `src/main/resources/META-INF/persistence.xml`:
```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:sqlserver://localhost:1433;databaseName=db_quanlysinhvien;encrypt=true;trustServerCertificate=true;" />
<property name="javax.persistence.jdbc.user" value="sa" />
<property name="javax.persistence.jdbc.password" value="sapassword" />
```

## Cài đặt và Chạy

### 1. Build Project
```bash
mvn clean install
```

### 2. Khởi động Server

#### Cách 1: Chạy từ IDE
- Mở class `iuh.fit.se.server.Server`
- Click Run hoặc Debug

#### Cách 2: Chạy từ command line
```bash
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"
```

**Kết quả mong đợi:**
```
[main] INFO iuh.fit.se.server.Server - Server started on port 8888
[main] INFO iuh.fit.se.server.Server - Waiting for clients...
```

### 3. Tạo dữ liệu mẫu (Seed Data)

Sau khi server chạy lần đầu (JPA đã tạo các bảng), chạy script SQL:
```bash
# Mở SQL Server Management Studio hoặc Azure Data Studio
# Mở file: seed_data.sql
# Chạy script
```

**Dữ liệu mẫu bao gồm:**
- 4 Khoa (CNTT, KT, DTVT, SPKT)
- 4 Lớp học
- 4 Giảng viên
- 5 Môn học
- 3 Học kỳ
- 5 Sinh viên
- 6 Tài khoản (1 admin, 2 GV, 2 SV)

### 4. Khởi động Client

#### Cách 1: Chạy từ IDE
- Mở class `iuh.fit.se.client.ClientMain`
- Click Run

#### Cách 2: Chạy từ command line
```bash
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.ClientMain"
```

## Tài khoản đăng nhập mặc định

### Admin (Quản trị viên)
```
Username: admin
Password: 123456
Quyền: Toàn quyền quản lý hệ thống
```

### Giảng viên
```
Username: gv001
Password: 123456
Quyền: Xem lớp giảng dạy, nhập điểm

Username: gv002
Password: 123456
```

### Sinh viên
```
Username: 20001001
Password: 123456
Quyền: Xem điểm, đăng ký học phần

Username: 20001002
Password: 123456
```

## Kiến trúc hệ thống

### Server Side (Port 8888)
```
├── AuthService          - Xác thực & Phân quyền
├── KhoaService          - CRUD Khoa
├── LopHocService        - CRUD Lớp học hành chính
├── GiangVienService     - CRUD Giảng viên
├── MonHocService        - CRUD Môn học
├── HocKyService         - CRUD Học kỳ
├── HocVuService         - Nghiệp vụ phức tạp
│   ├── Mở lớp học phần
│   ├── Đăng ký học phần (kiểm tra sĩ số, trùng lặp)
│   ├── Nhập điểm (kiểm tra quyền GV)
│   └── Xem bảng điểm
└── ClientHandler        - Xử lý request/response
```

### Database Schema
```
tai_khoan (Account)
    ├── 1:1 → sinh_vien
    └── 1:1 → giang_vien

khoa (Faculty)
    ├── 1:N → lop_hoc
    ├── 1:N → giang_vien
    └── 1:N → mon_hoc

lop_hoc (Class)
    ├── N:1 → khoa
    └── 1:N → sinh_vien

sinh_vien (Student)
    ├── N:1 → lop_hoc
    ├── 1:1 → tai_khoan
    └── 1:N → diem

giang_vien (Lecturer)
    ├── N:1 → khoa
    ├── 1:1 → tai_khoan
    └── 1:N → lop_hoc_phan

mon_hoc (Course)
    ├── N:1 → khoa
    └── 1:N → lop_hoc_phan

hoc_ky (Semester)
    └── 1:N → lop_hoc_phan

lop_hoc_phan (Course Section)
    ├── N:1 → mon_hoc
    ├── N:1 → giang_vien
    ├── N:1 → hoc_ky
    └── 1:N → diem

diem (Grade) - Composite Key
    ├── N:1 → sinh_vien
    └── N:1 → lop_hoc_phan
```

## Phân quyền (Authorization)

### ADMIN
- ✅ Tất cả các thao tác CRUD
- ✅ Quản lý Khoa, Lớp, Môn học, Giảng viên, Sinh viên
- ✅ Mở/Đóng lớp học phần
- ✅ Quản lý tài khoản

### GIANG_VIEN
- ✅ Xem danh sách lớp mình dạy
- ✅ Xem danh sách sinh viên trong lớp
- ✅ Nhập điểm cho sinh viên
- ❌ Không thể sửa thông tin khác

### SINH_VIEN
- ✅ Xem danh sách lớp học phần để đăng ký
- ✅ Đăng ký học phần (có kiểm tra sĩ số)
- ✅ Xem bảng điểm của mình
- ❌ Không thể sửa bất kỳ thông tin nào

## Troubleshooting

### Lỗi kết nối SQL Server
```
Error: The TCP/IP connection to the host localhost, port 1433 has failed
```
**Giải pháp:**
1. Kiểm tra SQL Server đã chạy: `SQL Server Configuration Manager`
2. Enable TCP/IP protocol
3. Kiểm tra port 1433 đang mở

### Lỗi Authentication
```
Status: UNAUTHORIZED - Chưa đăng nhập hoặc phiên đã hết hạn
```
**Giải pháp:**
- Đăng nhập lại với username/password đúng
- Token chỉ tồn tại trong runtime của server

### Lỗi Permission Denied
```
Status: FORBIDDEN - Không có quyền thực hiện thao tác này
```
**Giải pháp:**
- Kiểm tra role của tài khoản
- Đăng nhập với tài khoản có quyền phù hợp

## Mở rộng

### Thêm tính năng mới
1. Tạo Service mới trong `server.service`
2. Thêm Command vào `common.protocol.Command`
3. Xử lý trong `ClientHandler.handleAuthenticatedRequest()`
4. Kiểm tra phân quyền với `if (role != UserRole.XXX) return forbidden();`

### Thêm Entity mới
1. Tạo Entity class với JPA annotations
2. Tạo DTO tương ứng
3. Khai báo trong `persistence.xml`
4. Tạo Service và Mapper

## Liên hệ & Hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra log trong console
2. Kiểm tra file `logback.xml` để điều chỉnh log level
3. Xem database schema đã được tạo đúng chưa

---
**Phiên bản:** 2.0 (Nâng cao)  
**Ngày cập nhật:** 2024

