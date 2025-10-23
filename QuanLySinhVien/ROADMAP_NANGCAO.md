# HỆ THỐNG QUẢN LÝ ĐÀO TẠO - PHIÊN BẢN NÂNG CAO

## 🎓 TỔNG QUAN

Hệ thống đã được nâng cấp từ **quản lý sinh viên đơn giản** lên **hệ thống quản lý đào tạo đầy đủ** của một trường đại học.

## ✅ ĐÃ HOÀN THÀNH

### 1. Protocol Layer (Giao thức mở rộng)
- ✅ **UserRole** enum: ADMIN, GIANG_VIEN, SINH_VIEN
- ✅ **Command** mở rộng: 60+ commands cho tất cả chức năng
  - Xác thực: LOGIN, LOGOUT
  - Quản lý: KHOA_*, LOPHOC_*, GIANGVIEN_*, MONHOC_*, HOCKY_*, LOPHOCPHAN_*
  - Nghiệp vụ SV: SV_REGISTER_COURSE, SV_GET_TRANSCRIPT
  - Nghiệp vụ GV: GV_ENTER_GRADE, GV_GET_MY_CLASSES
- ✅ **Status** mở rộng: UNAUTHORIZED, FORBIDDEN
- ✅ **Request** với authToken field

### 2. DTO Layer (Data Transfer Objects)
- ✅ LoginDTO - Thông tin đăng nhập
- ✅ AuthResponseDTO - Phản hồi sau login (chứa token)
- ✅ KhoaDTO - Khoa/Department
- ✅ LopHocDTO - Lớp hành chính
- ✅ GiangVienDTO - Giảng viên
- ✅ MonHocDTO - Môn học

### 3. Hệ thống hiện tại (đang hoạt động)
- ✅ Server với in-memory mode
- ✅ GUI Client (MainFrame + StudentDialog)
- ✅ SinhVienDTO + basic CRUD

## 🚧 CẦN TRIỂN KHAI TIẾP

### GIAI ĐOẠN 3: Entity Layer (JPA Entities)
**10 Entity classes cần tạo:**

1. **TaiKhoan** (Account) - BẮT BUỘC
   - username (PK)
   - password (hashed)
   - role (UserRole enum)
   - @OneToOne sinhVien / giangVien

2. **Khoa** (Faculty)
   - maKhoa (PK)
   - tenKhoa
   - @OneToMany dsLopHoc, dsGiangVien, dsMonHoc

3. **LopHoc** (Administrative Class)
   - maLop (PK)
   - tenLop, nienKhoa
   - @ManyToOne khoa
   - @OneToMany dsSinhVien

4. **SinhVien** (Student) - Cập nhật
   - maSV (PK)
   - @ManyToOne lopHoc
   - @OneToOne taiKhoan
   - @OneToMany dsDiem

5. **GiangVien** (Lecturer)
   - maGV (PK)
   - hoTen, hocVi
   - @ManyToOne khoa
   - @OneToOne taiKhoan
   - @OneToMany dsLopHocPhan

6. **MonHoc** (Course)
   - maMH (PK)
   - tenMH, soTinChi
   - @ManyToOne khoa
   - @OneToMany dsLopHocPhan

7. **HocKy** (Semester)
   - maHocKy (PK)
   - tenHocKy, ngayBatDau, ngayKetThuc
   - @OneToMany dsLopHocPhan

8. **LopHocPhan** (Course Section)
   - maLHP (PK, auto-increment)
   - @ManyToOne monHoc, giangVien, hocKy
   - soLuongToiDa
   - @OneToMany dsDiem

9. **Diem** (Grade) - Composite Key
   - @EmbeddedId DiemId
   - @ManyToOne sinhVien, lopHocPhan
   - diemChuyenCan, diemGiuaKy, diemCuoiKy, diemTongKet

10. **DiemId** (Composite Key)
    - @Embeddable
    - maSV + maLHP

### GIAI ĐOẠN 4: Service Layer
**Services cần tạo:**

1. **AuthService**
   - login(username, password) → AuthResponseDTO
   - logout(token)
   - validateToken(token) → UserInfo
   - Sử dụng BCrypt để hash/verify password

2. **KhoaService**
   - addKhoa(KhoaDTO)
   - updateKhoa(KhoaDTO)
   - deleteKhoa(maKhoa)
   - getAllKhoa() → List<KhoaDTO>

3. **LopHocService**
   - CRUD cho LopHoc
   - getAllByKhoa(maKhoa)

4. **GiangVienService**
   - CRUD cho GiangVien
   - getAllByKhoa(maKhoa)

5. **MonHocService**
   - CRUD cho MonHoc
   - getAllByKhoa(maKhoa)

6. **HocVuService** (Nghiệp vụ phức tạp)
   - moLopHocPhan(...)
   - dangKyHocPhan(maSV, maLHP)
   - nhapDiem(maGV, DiemDTO)
   - xemBangDiem(maSV) → BangDiemDTO

### GIAI ĐOẠN 5: Server Update
**Cập nhật ClientHandler:**
- Thêm xác thực token cho mọi request
- Kiểm tra phân quyền (ADMIN, GIANG_VIEN, SINH_VIEN)
- Route commands đến các service tương ứng

**Cập nhật persistence.xml:**
- Khai báo tất cả 10 Entity classes

### GIAI ĐOẠN 6: GUI Client Nâng cao
**MainFrame sử dụng JTabbedPane:**

Tab 1: **Quản lý Khoa**
- JTable hiển thị danh sách Khoa
- Nút: Thêm, Sửa, Xóa

Tab 2: **Quản lý Lớp**
- ComboBox chọn Khoa
- JTable hiển thị Lớp thuộc Khoa
- Nút: Thêm, Sửa, Xóa

Tab 3: **Quản lý Sinh viên**
- ComboBox chọn Lớp
- JTable hiển thị SV thuộc Lớp
- Nút: Thêm, Sửa, Xóa

Tab 4: **Quản lý Giảng viên**
- ComboBox chọn Khoa
- JTable hiển thị GV thuộc Khoa

Tab 5: **Quản lý Môn học**
- ComboBox chọn Khoa
- JTable hiển thị Môn học

Tab 6: **Quản lý Đào tạo**
- ComboBox chọn Học kỳ
- JTable hiển thị Lớp học phần
- Nút: Mở lớp mới

Tab 7: **Tài khoản**
- JTable hiển thị Tài khoản
- Nút: Thêm, Đổi mật khẩu, Xóa

**Login Screen:**
- JDialog login trước khi vào MainFrame
- Nhập username/password
- Lưu authToken sau khi login thành công
- Mọi request đều gửi kèm token

## 📋 LỘ TRÌNH TRIỂN KHAI ĐỀ XUẤT

### Tuần 1: Entity + JPA
1. Tạo 10 Entity classes
2. Cập nhật persistence.xml
3. Test JPA với database

### Tuần 2: Service Layer
1. Implement AuthService (quan trọng nhất)
2. Implement các CRUD Services (Khoa, LopHoc, GiangVien, MonHoc)
3. Test từng service riêng lẻ

### Tuần 3: Server Integration
1. Cập nhật ClientHandler với authentication
2. Cập nhật ClientHandler với authorization
3. Route commands đến services
4. Test với Postman hoặc test client

### Tuần 4: GUI Client
1. Tạo LoginDialog
2. Cập nhật MainFrame với JTabbedPane
3. Tạo các Dialog cho từng entity
4. Test end-to-end

## 🎯 PHẠM VI TỐI THIỂU (MVP)

Nếu thời gian hạn chế, tập trung vào:

### Core Features (BẮT BUỘC)
1. ✅ Login/Logout với token
2. ✅ Quản lý Khoa (CRUD)
3. ✅ Quản lý Lớp (CRUD)
4. ✅ Quản lý Sinh viên (CRUD) - đã có
5. ✅ Phân quyền cơ bản (ADMIN vs non-ADMIN)

### Advanced Features (NÊN CÓ)
6. Quản lý Giảng viên
7. Quản lý Môn học
8. Mở lớp học phần
9. Sinh viên đăng ký học phần
10. Giảng viên nhập điểm

## 💡 GỢI Ý CÔNG NGHỆ

### Security
```java
// Sử dụng BCrypt cho password
import org.mindrot.jbcrypt.BCrypt;

String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
```

Thêm dependency vào pom.xml:
```xml
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

### Token Management
```java
// Simple UUID token
String token = UUID.randomUUID().toString();

// Lưu trong ConcurrentHashMap
Map<String, String> tokenStore = new ConcurrentHashMap<>();
tokenStore.put(token, username);
```

### JPA Relationships
```java
// Khoa.java
@OneToMany(mappedBy="khoa", cascade=CascadeType.ALL)
private List<LopHoc> dsLopHoc;

// LopHoc.java
@ManyToOne
@JoinColumn(name="ma_khoa")
private Khoa khoa;
```

## 📊 DATABASE SCHEMA

```sql
-- Khoa
CREATE TABLE khoa (
    ma_khoa VARCHAR(10) PRIMARY KEY,
    ten_khoa NVARCHAR(100) NOT NULL
);

-- LopHoc
CREATE TABLE lop_hoc (
    ma_lop VARCHAR(20) PRIMARY KEY,
    ten_lop NVARCHAR(100) NOT NULL,
    nien_khoa VARCHAR(20),
    ma_khoa VARCHAR(10),
    FOREIGN KEY (ma_khoa) REFERENCES khoa(ma_khoa)
);

-- SinhVien
CREATE TABLE sinh_vien (
    ma_sv VARCHAR(20) PRIMARY KEY,
    ho_ten NVARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),
    chuyen_nganh NVARCHAR(50),
    ma_lop VARCHAR(20),
    diem_tb FLOAT,
    FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop)
);

-- TaiKhoan
CREATE TABLE tai_khoan (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    ma_sv VARCHAR(20),
    ma_gv VARCHAR(20),
    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),
    FOREIGN KEY (ma_gv) REFERENCES giang_vien(ma_gv)
);
```

## 🔧 HƯỚNG DẪN SỬ DỤNG

### 1. Chạy Server
```bash
# Đảm bảo SKIP_JPA = false nếu dùng database
# hoặc SKIP_JPA = true để dùng in-memory
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"
```

### 2. Chạy GUI Client
```bash
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.gui.MainFrame"
```

### 3. Login mặc định
```
Username: admin
Password: admin123
Role: ADMIN
```

## 📝 KẾT LUẬN

Hệ thống hiện tại đã có **nền tảng vững chắc**:
- ✅ Protocol đầy đủ (60+ commands)
- ✅ DTO layer hoàn chỉnh
- ✅ GUI framework sẵn sàng

Còn cần triển khai:
- 🚧 10 Entity classes (JPA)
- 🚧 6 Service classes
- 🚧 Authentication & Authorization
- 🚧 GUI nâng cao với JTabbedPane

**Ước tính thời gian:** 20-30 giờ cho full implementation

**Khuyến nghị:** Triển khai theo từng giai đoạn, test kỹ mỗi phần trước khi sang phần tiếp theo.

