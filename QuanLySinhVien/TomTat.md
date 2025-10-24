# TÀI LIỆU TÓM TẮT HỆ THỐNG QUẢN LÝ ĐÀO TẠO

## 📋 THÔNG TIN DỰ ÁN

**Tên dự án:** Hệ thống Quản lý Sinh viên (Student Management System)  
**Mô hình:** Client-Server (Phân tán)  
**Ngôn ngữ:** Java 8  
**Build Tool:** Maven  
**Database:** Microsoft SQL Server  
**Ngày tạo:** 2024

---

## 🎯 TỔNG QUAN HỆ THỐNG

Đây là một **hệ thống quản lý đào tạo đại học** hoàn chỉnh, được xây dựng theo mô hình **Client-Server phân tán**. Hệ thống cho phép quản lý toàn bộ quy trình đào tạo từ quản lý khoa, lớp, sinh viên, giảng viên, môn học, đến đăng ký học phần và quản lý điểm số.

### Đặc điểm nổi bật:
- ✅ **Kiến trúc 3-tier:** Client (GUI) - Server (Business Logic) - Database
- ✅ **Giao tiếp qua Socket:** Sử dụng TCP/IP Socket với Object Serialization
- ✅ **Multi-threading:** Server xử lý nhiều client đồng thời với Thread Pool
- ✅ **Xác thực và phân quyền:** JWT-like token authentication với 3 role
- ✅ **ORM:** Sử dụng JPA/Hibernate để ánh xạ đối tượng
- ✅ **Giao diện đồ họa:** Swing GUI với thiết kế hiện đại
- ✅ **Validation:** Bean Validation API (JSR-380)
- ✅ **Logging:** SLF4J + Logback
- ✅ **Security:** BCrypt password hashing

---

## 🏗️ KIẾN TRÚC HỆ THỐNG

### 1. Cấu trúc Package
```
iuh.fit.se/
├── client/              # Client-side code
│   ├── Client.java      # Console client (legacy)
│   ├── ClientMain.java  # GUI client launcher
│   ├── gui/             # Swing GUI components
│   │   ├── LoginFrame.java
│   │   ├── AdminMainFrame.java      # Giao diện Admin
│   │   ├── StudentMainFrame.java    # Giao diện Sinh viên
│   │   ├── TeacherMainFrame.java    # Giao diện Giảng viên
│   │   └── panels/                  # Các panel chức năng
│   │       ├── KhoaPanel.java
│   │       ├── LopHocPanel.java
│   │       ├── SinhVienPanel.java
│   │       ├── GiangVienPanel.java
│   │       ├── MonHocPanel.java
│   │       ├── HocKyPanel.java
│   │       ├── LopHocPhanPanel.java
│   │       ├── student/             # Panel dành cho sinh viên
│   │       └── teacher/             # Panel dành cho giảng viên
│   └── net/
│       └── NetworkClient.java       # Xử lý kết nối đến server
│
├── server/              # Server-side code
│   ├── Server.java      # Main server application
│   ├── net/
│   │   └── ClientHandler.java       # Xử lý từng client connection
│   ├── service/         # Business logic layer
│   │   ├── AuthService.java         # Xác thực & phân quyền
│   │   ├── KhoaService.java
│   │   ├── LopHocService.java
│   │   ├── SinhVienService.java (legacy)
│   │   ├── JpaStudentService.java
│   │   ├── GiangVienService.java
│   │   ├── MonHocService.java
│   │   ├── HocKyService.java
│   │   └── HocVuService.java        # Nghiệp vụ phức tạp
│   └── util/
│       ├── DataSeeder.java          # Tạo dữ liệu mẫu
│       └── SinhVienMapper.java      # Convert Entity <-> DTO
│
└── common/              # Shared code (Client & Server)
    ├── dto/             # Data Transfer Objects
    │   ├── AuthResponseDTO.java
    │   ├── LoginDTO.java
    │   ├── SinhVienDTO.java
    │   ├── GiangVienDTO.java
    │   ├── KhoaDTO.java
    │   ├── LopHocDTO.java
    │   ├── MonHocDTO.java
    │   ├── HocKyDTO.java
    │   └── LopHocPhanDTO.java
    ├── model/           # JPA Entity classes
    │   ├── TaiKhoan.java            # Tài khoản đăng nhập
    │   ├── SinhVien.java
    │   ├── GiangVien.java
    │   ├── Khoa.java
    │   ├── LopHoc.java              # Lớp hành chính
    │   ├── MonHoc.java
    │   ├── HocKy.java
    │   ├── LopHocPhan.java          # Lớp học phần (môn học + giảng viên + học kỳ)
    │   ├── Diem.java                # Điểm của sinh viên
    │   ├── DiemId.java              # Composite key cho Diem
    │   ├── Gender.java              # Enum: NAM, NU
    │   ├── UserRole.java            # Enum: ADMIN, GIANG_VIEN, SINH_VIEN
    │   └── TrangThaiDiem.java       # Enum: DAT, KHONG_DAT, CHUA_CO
    └── protocol/        # Communication protocol
        ├── Command.java             # Enum các lệnh (70+ commands)
        ├── Request.java             # Request wrapper
        ├── Response.java            # Response wrapper
        └── Status.java              # Enum: SUCCESS, ERROR, UNAUTHORIZED
```

---

## 💾 CÔNG NGHỆ SỬ DỤNG

### 1. Core Technologies
| Công nghệ | Version | Mục đích |
|-----------|---------|----------|
| **Java** | 8 | Ngôn ngữ lập trình chính |
| **Maven** | 4.0.0 | Build tool & dependency management |
| **JPA** | 2.2 | Java Persistence API - ORM standard |
| **Hibernate** | 5.4.32.Final | JPA implementation |
| **MS SQL Server** | JDBC 10.2.0 | Database |
| **Swing** | Built-in | GUI framework |

### 2. Libraries & Frameworks

#### Database & Persistence
- **javax.persistence-api 2.2** - JPA specification
- **hibernate-core 5.4.32.Final** - ORM implementation
- **hibernate-validator 6.1.7.Final** - Bean validation
- **mssql-jdbc 10.2.0.jre8** - SQL Server driver

#### Security
- **BCrypt (jbcrypt 0.4)** - Password hashing algorithm
  - Mã hóa mật khẩu an toàn với salt tự động
  - Bảo vệ chống rainbow table attacks

#### Logging
- **SLF4J 1.7.30** - Logging facade
- **Logback 1.2.3** - Logging implementation
  - Console logging với màu sắc
  - File logging với rotation
  - Configurable via logback.xml

#### Validation
- **validation-api 2.0.1.Final** - JSR-380 Bean Validation
- **jakarta.el 3.0.3** - Expression Language (required by validator)
  - @NotBlank, @NotNull, @Size
  - @Min, @Max, @Positive, @Past

### 3. Networking
- **Java Socket API** - TCP/IP communication
- **ObjectInputStream/ObjectOutputStream** - Object serialization
- **ExecutorService** - Thread pool cho multi-client handling

---

## 🗄️ CƠ SỞ DỮ LIỆU

### Database Schema

#### 1. **tai_khoan** (Tài khoản đăng nhập)
```sql
CREATE TABLE tai_khoan (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,     -- BCrypt hashed
    role VARCHAR(20) NOT NULL,          -- ADMIN, GIANG_VIEN, SINH_VIEN
    ma_sv VARCHAR(20) FOREIGN KEY,      -- NULL nếu không phải sinh viên
    ma_gv VARCHAR(20) FOREIGN KEY       -- NULL nếu không phải giảng viên
);
```

#### 2. **khoa** (Khoa/Viện)
```sql
CREATE TABLE khoa (
    ma_khoa VARCHAR(10) PRIMARY KEY,
    ten_khoa VARCHAR(100) NOT NULL
);
```

#### 3. **lop_hoc** (Lớp hành chính)
```sql
CREATE TABLE lop_hoc (
    ma_lop VARCHAR(20) PRIMARY KEY,
    ten_lop VARCHAR(100) NOT NULL,
    nien_khoa VARCHAR(20),              -- VD: "2020-2024"
    ma_khoa VARCHAR(10) FOREIGN KEY
);
```

#### 4. **sinh_vien**
```sql
CREATE TABLE sinh_vien (
    ma_sv VARCHAR(20) PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),              -- NAM, NU
    chuyen_nganh VARCHAR(50),
    ma_lop VARCHAR(20) FOREIGN KEY,
    diem_tb FLOAT
);
```

#### 5. **giang_vien**
```sql
CREATE TABLE giang_vien (
    ma_gv VARCHAR(20) PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    hoc_vi VARCHAR(50),                 -- Thạc sĩ, Tiến sĩ
    ma_khoa VARCHAR(10) FOREIGN KEY
);
```

#### 6. **mon_hoc**
```sql
CREATE TABLE mon_hoc (
    ma_mh VARCHAR(20) PRIMARY KEY,
    ten_mh VARCHAR(100) NOT NULL,
    so_tin_chi INT NOT NULL,
    ma_khoa VARCHAR(10) FOREIGN KEY
);
```

#### 7. **hoc_ky**
```sql
CREATE TABLE hoc_ky (
    ma_hoc_ky VARCHAR(20) PRIMARY KEY,
    ten_hoc_ky VARCHAR(100) NOT NULL,   -- VD: "HK1 2023-2024"
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE
);
```

#### 8. **lop_hoc_phan** (Lớp học - môn học cụ thể)
```sql
CREATE TABLE lop_hoc_phan (
    ma_lhp BIGINT PRIMARY KEY IDENTITY,
    ma_mh VARCHAR(20) FOREIGN KEY,      -- Môn học
    ma_gv VARCHAR(20) FOREIGN KEY,      -- Giảng viên dạy
    ma_hoc_ky VARCHAR(20) FOREIGN KEY,  -- Học kỳ
    so_luong_toi_da INT                 -- Sĩ số tối đa
);
```

#### 9. **diem** (Điểm của sinh viên)
```sql
CREATE TABLE diem (
    ma_sv VARCHAR(20),
    ma_lhp BIGINT,
    diem_chuyen_can FLOAT,              -- 10%
    diem_giua_ky FLOAT,                 -- 30%
    diem_cuoi_ky FLOAT,                 -- 60%
    diem_tong_ket FLOAT,                -- Tự động tính
    trang_thai VARCHAR(20),             -- DAT, KHONG_DAT, CHUA_CO
    PRIMARY KEY (ma_sv, ma_lhp),
    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),
    FOREIGN KEY (ma_lhp) REFERENCES lop_hoc_phan(ma_lhp)
);
```

### Database Connection (persistence.xml)
```xml
<property name="javax.persistence.jdbc.url" 
    value="jdbc:sqlserver://localhost:1433;
           databaseName=db_quanlysinhvien;
           encrypt=true;
           trustServerCertificate=true;" />
<property name="javax.persistence.jdbc.user" value="sa" />
<property name="javax.persistence.jdbc.password" value="SaPassword123!" />
<property name="hibernate.hbm2ddl.auto" value="update" />
```

---

## 🔐 HỆ THỐNG XÁC THỰC & PHÂN QUYỀN

### Authentication Flow

1. **Login Process:**
   ```
   Client → LOGIN Command → Server
   Server → Validate username/password (BCrypt)
   Server → Generate UUID token
   Server → Store token in memory (ConcurrentHashMap)
   Server → Return AuthResponseDTO
   Client → Store token for subsequent requests
   ```

2. **Request Authentication:**
   ```
   Client → Include authToken in Request
   Server → Validate token
   Server → Check authorization based on UserRole
   Server → Process request or return UNAUTHORIZED
   ```

### User Roles & Permissions

#### 1. **ADMIN (Giáo vụ/Quản trị viên)**
**Quyền hạn:** Toàn quyền quản lý hệ thống

**Chức năng:**
- ✅ Quản lý Khoa (CRUD)
- ✅ Quản lý Lớp hành chính (CRUD)
- ✅ Quản lý Sinh viên (CRUD)
- ✅ Quản lý Giảng viên (CRUD)
- ✅ Quản lý Môn học (CRUD)
- ✅ Quản lý Học kỳ (CRUD)
- ✅ Quản lý Lớp học phần (CRUD - mở lớp, phân giảng viên)
- ✅ Quản lý Tài khoản (CRUD, reset password)
- ✅ Xem toàn bộ báo cáo, thống kê

**GUI:** AdminMainFrame với 7 tabs

#### 2. **GIANG_VIEN (Giảng viên)**
**Quyền hạn:** Quản lý lớp học và điểm số

**Chức năng:**
- ✅ Xem danh sách lớp học phần được phân công
- ✅ Xem danh sách sinh viên trong lớp
- ✅ Nhập điểm (chuyên cần, giữa kỳ, cuối kỳ)
- ✅ Xem thống kê lớp học (tỷ lệ đậu/rớt, điểm TB)
- ❌ Không được sửa thông tin sinh viên
- ❌ Không được tạo/xóa lớp học phần

**GUI:** TeacherMainFrame với 3 tabs

#### 3. **SINH_VIEN (Sinh viên)**
**Quyền hạn:** Xem thông tin cá nhân và đăng ký học

**Chức năng:**
- ✅ Xem thông tin cá nhân
- ✅ Xem danh sách lớp học phần có thể đăng ký
- ✅ Đăng ký/Hủy đăng ký học phần
- ✅ Xem lớp học phần đã đăng ký
- ✅ Xem bảng điểm (transcript)
- ❌ Không được sửa điểm
- ❌ Không được xem thông tin sinh viên khác

**GUI:** StudentMainFrame với 4 tabs

### Default Admin Account
```
Username: admin
Password: admin123
Role: ADMIN
```
*Tự động tạo khi khởi động server lần đầu*

---

## 📡 GIAO THỨC TRUYỀN THÔNG (PROTOCOL)

### Request-Response Pattern

#### Request Structure
```java
public class Request implements Serializable {
    private Command command;        // Loại lệnh (enum)
    private Object data;           // Dữ liệu đầu vào (DTO)
    private String authToken;      // Token xác thực
}
```

#### Response Structure
```java
public class Response implements Serializable {
    private Status status;         // SUCCESS, ERROR, UNAUTHORIZED
    private String message;        // Thông báo
    private Object data;          // Dữ liệu trả về (DTO hoặc List<DTO>)
}
```

### Commands (70+ operations)

#### Authentication Commands
- `LOGIN` - Đăng nhập
- `LOGOUT` - Đăng xuất

#### Khoa Management
- `KHOA_ADD` - Thêm khoa
- `KHOA_UPDATE` - Cập nhật khoa
- `KHOA_DELETE` - Xóa khoa
- `KHOA_GET_BY_ID` - Lấy thông tin khoa
- `KHOA_GET_ALL` - Lấy danh sách khoa

#### Lớp hành chính Management
- `LOPHOC_ADD`, `LOPHOC_UPDATE`, `LOPHOC_DELETE`
- `LOPHOC_GET_ALL_BY_KHOA`, `LOPHOC_GET_ALL`

#### Sinh viên Management
- `SINHVIEN_ADD`, `SINHVIEN_UPDATE`, `SINHVIEN_DELETE`
- `SINHVIEN_GET_BY_ID`, `SINHVIEN_GET_ALL_BY_LOPHOC`, `SINHVIEN_GET_ALL`

#### Giảng viên Management
- `GIANGVIEN_ADD`, `GIANGVIEN_UPDATE`, `GIANGVIEN_DELETE`
- `GIANGVIEN_GET_ALL_BY_KHOA`, `GIANGVIEN_GET_ALL`

#### Môn học Management
- `MONHOC_ADD`, `MONHOC_UPDATE`, `MONHOC_DELETE`
- `MONHOC_GET_ALL_BY_KHOA`, `MONHOC_GET_ALL`

#### Học kỳ Management
- `HOCKY_ADD`, `HOCKY_UPDATE`, `HOCKY_DELETE`, `HOCKY_GET_ALL`

#### Lớp học phần Management
- `LOPHOCPHAN_ADD`, `LOPHOCPHAN_UPDATE`, `LOPHOCPHAN_DELETE`
- `LOPHOCPHAN_GET_ALL_BY_HOCKY`, `LOPHOCPHAN_GET_ALL`

#### Nghiệp vụ Sinh viên
- `SV_GET_LOPHOCPHAN_TO_REGISTER` - Lấy danh sách lớp có thể đăng ký
- `SV_REGISTER_COURSE` - Đăng ký học phần
- `SV_GET_TRANSCRIPT` - Xem bảng điểm

#### Nghiệp vụ Giảng viên
- `GV_GET_MY_CLASSES` - Lấy lớp giảng viên dạy
- `GV_GET_STUDENT_LIST_OF_CLASS` - Lấy danh sách sinh viên
- `GV_ENTER_GRADE` - Nhập điểm

#### Tài khoản Management
- `TAIKHOAN_ADD`, `TAIKHOAN_UPDATE`, `TAIKHOAN_DELETE`
- `TAIKHOAN_GET_ALL`, `TAIKHOAN_CHANGE_PASSWORD`

### Example Communication

**Client Request:**
```java
LoginDTO loginData = new LoginDTO("admin", "admin123");
Request request = new Request(Command.LOGIN, loginData);
Response response = networkClient.sendRequest(request);
```

**Server Response:**
```java
if (status == Status.SUCCESS) {
    AuthResponseDTO auth = (AuthResponseDTO) response.getData();
    String token = auth.getAuthToken();
    UserRole role = auth.getRole();
}
```

---

## 🔧 CÁC SERVICE CHÍNH

### 1. AuthService (Singleton)
**Mục đích:** Xác thực và quản lý phiên đăng nhập

**Chức năng chính:**
- `login(username, password)` → AuthResponseDTO
- `logout(token)` → boolean
- `validateToken(token)` → boolean
- `getAccountByToken(token)` → TaiKhoan
- `checkPermission(token, resource, action)` → boolean
- `initializeDefaultAdmin()` - Tạo admin mặc định

**Implementation:**
- Sử dụng `ConcurrentHashMap` lưu token và session
- Token: UUID random
- Password: BCrypt hashing với salt

### 2. KhoaService (Singleton)
**Mục đích:** Quản lý Khoa/Viện

**Chức năng:**
- `addKhoa(KhoaDTO)` → KhoaDTO
- `updateKhoa(KhoaDTO)` → KhoaDTO
- `deleteKhoa(String maKhoa)` → boolean
- `getKhoaById(String maKhoa)` → KhoaDTO
- `getAllKhoa()` → List<KhoaDTO>

### 3. SinhVienService (Legacy) + JpaStudentService
**Mục đích:** Quản lý Sinh viên

**In-memory mode (Legacy):**
- Sử dụng `ArrayList<SinhVien>` lưu trong RAM
- Dùng cho testing nhanh

**JPA mode (Production):**
- CRUD operations với database
- Transaction management
- Validation

### 4. HocVuService (Singleton)
**Mục đích:** Xử lý nghiệp vụ phức tạp

**Chức năng:**
- `moLopHocPhan(...)` - Mở lớp học phần mới
- `dangKyHocPhan(maSV, maLHP)` - Sinh viên đăng ký
- `huyDangKy(maSV, maLHP)` - Hủy đăng ký
- `nhapDiem(maSV, maLHP, diemCC, diemGK, diemCK)` - Nhập điểm
- `getBangDiem(maSV)` - Lấy bảng điểm
- `kiemTraSiSo(maLHP)` - Kiểm tra còn chỗ không

**Business Rules:**
- Kiểm tra sĩ số tối đa trước khi đăng ký
- Kiểm tra trùng lịch học
- Tự động tính điểm tổng kết: 10%CC + 30%GK + 60%CK
- Tự động cập nhật trạng thái ĐẠT/KHÔNG ĐẠT

### 5. GiangVienService, MonHocService, HocKyService, LopHocService
**Mục đích:** Quản lý các entity tương ứng

**Pattern:** Tương tự KhoaService
- Singleton instance
- CRUD operations
- DTO conversion
- Transaction management

---

## 🖥️ CLIENT-SIDE ARCHITECTURE

### Network Layer

#### NetworkClient
**Mục đích:** Quản lý kết nối TCP/IP đến server

**Features:**
- Socket connection với auto-reconnect
- Object serialization (Request/Response)
- Error handling và timeout
- AutoCloseable interface

**Usage:**
```java
NetworkClient client = new NetworkClient("localhost", 8888);
Response response = client.sendRequest(request);
client.close();
```

### GUI Architecture

#### Modern Swing Design
- **Look and Feel:** System native
- **Color Scheme:** 
  - Primary: #2980b9 (Blue)
  - Danger: #e74c3c (Red)
  - Success: #28a745 (Green)
  - Header: #34495e (Dark Gray)
- **Fonts:** Segoe UI
- **Icons:** Unicode emoji (🎓📚👨‍🎓👨‍🏫📊)

#### LoginFrame
**Mục đích:** Màn hình đăng nhập

**Features:**
- Modern card-style design
- Username/Password input
- Remember me (planning)
- Auto-focus và Enter key support
- Error messages với màu đỏ

**Flow:**
```
LoginFrame → Authenticate → AuthResponseDTO
  ↓
Based on role:
  - ADMIN → AdminMainFrame
  - GIANG_VIEN → TeacherMainFrame
  - SINH_VIEN → StudentMainFrame
```

#### AdminMainFrame
**Mục đích:** Giao diện quản trị hệ thống

**Tabs:**
1. **Quản lý Khoa** (KhoaPanel)
   - Table view với CRUD
   - Search/Filter
2. **Quản lý Lớp học** (LopHocPanel)
   - Filter theo khoa
   - Phân công lớp
3. **Quản lý Giảng viên** (GiangVienPanel)
   - CRUD với học vị
   - Filter theo khoa
4. **Quản lý Môn học** (MonHocPanel)
   - Số tín chỉ, khoa
5. **Quản lý Học kỳ** (HocKyPanel)
   - Ngày bắt đầu/kết thúc
6. **Quản lý Sinh viên** (SinhVienPanel)
   - CRUD đầy đủ
   - Import/Export (planning)
7. **Quản lý Lớp học phần** (LopHocPhanPanel)
   - Mở lớp, phân giảng viên
   - Quản lý sĩ số

#### StudentMainFrame
**Mục đích:** Giao diện sinh viên

**Tabs:**
1. **Thông tin cá nhân** - Xem/sửa thông tin
2. **Đăng ký học phần** - Chọn lớp đăng ký
3. **Lớp đã đăng ký** - Xem lớp đã đăng ký
4. **Bảng điểm** - Xem transcript

#### TeacherMainFrame
**Mục đích:** Giao diện giảng viên

**Tabs:**
1. **Lớp học phần của tôi** - Danh sách lớp được phân công
2. **Nhập điểm** - Form nhập điểm sinh viên
3. **Thống kê** - Biểu đồ phân bố điểm, tỷ lệ đậu/rớt

### Panel Design Pattern

**RefreshablePanel Interface:**
```java
public interface RefreshablePanel {
    void refresh();  // Reload data from server
}
```

All panels implement this for data synchronization.

---

## 🚀 SERVER-SIDE ARCHITECTURE

### Server Startup Flow

```
1. Server.main()
   ↓
2. Initialize JPA EntityManagerFactory
   ↓
3. Check SKIP_JPA flag
   ├─ true → Use in-memory StudentService
   └─ false → Use JpaStudentServiceAdapter
   ↓
4. Initialize all Service singletons (lazy)
   ↓
5. AuthService.initializeDefaultAdmin()
   ↓
6. DataSeeder.seedAll() - Seed sample data
   ↓
7. Create ServerSocket(8888)
   ↓
8. ExecutorService.newFixedThreadPool(10)
   ↓
9. Accept client connections (infinite loop)
   ↓
10. For each client → new ClientHandler(socket)
    ↓
11. ExecutorService.submit(clientHandler)
```

### Multi-threading Model

**Thread Pool:**
- Type: FixedThreadPool
- Size: 10 concurrent clients
- Pattern: One thread per client connection

**Thread Safety:**
- Services: Singleton with synchronized getInstance()
- Sessions: ConcurrentHashMap for token storage
- Database: JPA EntityManager per request (thread-local)

### ClientHandler Flow

```
1. Accept Socket connection
   ↓
2. Create ObjectInputStream/ObjectOutputStream
   ↓
3. Read Request objects in loop
   ↓
4. For each Request:
   ├─ If LOGIN → Skip auth check
   ├─ Else → Validate token
   ↓
5. Check authorization based on role
   ↓
6. Route to appropriate Service method
   ↓
7. Create Response (SUCCESS/ERROR/UNAUTHORIZED)
   ↓
8. Write Response back to client
   ↓
9. Repeat until client disconnects
```

### Request Handling Example

```java
private Response handleAuthenticatedRequest(Request req, TaiKhoan account) {
    Command cmd = req.getCommand();
    
    switch(cmd) {
        case KHOA_ADD:
            if (account.getRole() != UserRole.ADMIN) {
                return new Response(Status.UNAUTHORIZED, 
                    "Chỉ Admin mới có quyền thêm khoa", null);
            }
            KhoaDTO dto = (KhoaDTO) req.getData();
            KhoaDTO result = khoaService.addKhoa(dto);
            return new Response(Status.SUCCESS, "Thêm thành công", result);
            
        case SV_GET_TRANSCRIPT:
            String maSV = (String) req.getData();
            // Sinh viên chỉ xem được bảng điểm của mình
            if (account.getRole() == UserRole.SINH_VIEN) {
                if (!maSV.equals(account.getSinhVien().getMaSV())) {
                    return new Response(Status.UNAUTHORIZED, 
                        "Không có quyền xem", null);
                }
            }
            List<DiemDTO> bangDiem = hocVuService.getBangDiem(maSV);
            return new Response(Status.SUCCESS, "OK", bangDiem);
            
        // ... 70+ more commands
    }
}
```

---

## 📊 DATA FLOW EXAMPLES

### Example 1: Đăng nhập

**Client:**
```java
// 1. User nhập username/password
LoginDTO loginDTO = new LoginDTO("admin", "admin123");

// 2. Tạo Request
Request request = new Request(Command.LOGIN, loginDTO);

// 3. Gửi đến server
Response response = networkClient.sendRequest(request);

// 4. Xử lý Response
if (response.getStatus() == Status.SUCCESS) {
    AuthResponseDTO auth = (AuthResponseDTO) response.getData();
    String token = auth.getAuthToken();
    UserRole role = auth.getRole();
    
    // 5. Mở giao diện tương ứng
    if (role == UserRole.ADMIN) {
        new AdminMainFrame(auth, networkClient).setVisible(true);
    }
}
```

**Server:**
```java
// 1. ClientHandler nhận Request
Request req = (Request) in.readObject();

// 2. Route đến AuthService
LoginDTO loginDTO = (LoginDTO) req.getData();
AuthResponseDTO authResponse = authService.login(
    loginDTO.getUsername(), 
    loginDTO.getPassword()
);

// 3. Tạo Response
Response response;
if (authResponse.isSuccess()) {
    response = new Response(Status.SUCCESS, 
        "Đăng nhập thành công", authResponse);
} else {
    response = new Response(Status.ERROR, 
        authResponse.getMessage(), null);
}

// 4. Gửi về client
out.writeObject(response);
```

### Example 2: Thêm sinh viên mới

**Client (AdminMainFrame → SinhVienPanel):**
```java
// 1. Admin điền form và click "Thêm"
SinhVienDTO dto = new SinhVienDTO(
    "20001234", "Nguyễn Văn A", 
    LocalDate.of(2002, 1, 1),
    Gender.NAM, "Kỹ thuật phần mềm", 8.5
);

// 2. Gửi request với token
Request req = new Request(Command.SINHVIEN_ADD, dto);
req.setAuthToken(authToken);
Response res = networkClient.sendRequest(req);

// 3. Hiển thị kết quả
if (res.getStatus() == Status.SUCCESS) {
    JOptionPane.showMessageDialog(this, "Thêm thành công!");
    loadData(); // Refresh table
}
```

**Server:**
```java
// 1. Validate token
if (!authService.validateToken(req.getAuthToken())) {
    return new Response(Status.UNAUTHORIZED, "Hết phiên", null);
}

// 2. Check permission
TaiKhoan account = authService.getAccountByToken(req.getAuthToken());
if (account.getRole() != UserRole.ADMIN) {
    return new Response(Status.UNAUTHORIZED, "Không có quyền", null);
}

// 3. Process request
SinhVienDTO dto = (SinhVienDTO) req.getData();
JpaStudentService service = JpaStudentService.getInstance();
SinhVienDTO result = service.addSinhVien(dto);

// 4. Return success
return new Response(Status.SUCCESS, "Đã thêm sinh viên", result);
```

### Example 3: Sinh viên đăng ký học phần

**Business Flow:**
```
1. Sinh viên login → StudentMainFrame
2. Tab "Đăng ký học phần"
3. Load danh sách lớp học phần (SV_GET_LOPHOCPHAN_TO_REGISTER)
   - Filter theo học kỳ hiện tại
   - Chỉ hiển thị lớp còn chỗ
   - Không hiển thị lớp đã đăng ký
4. Sinh viên chọn lớp → Click "Đăng ký"
5. Client gửi Command.SV_REGISTER_COURSE
6. Server kiểm tra:
   - Token hợp lệ?
   - Sinh viên có phải chủ tài khoản?
   - Lớp còn chỗ?
   - Đã đăng ký chưa?
   - Trùng lịch học?
7. Nếu OK → Tạo record trong bảng Diem
8. Return SUCCESS
9. Client refresh danh sách
```

---

## 🎨 GUI DESIGN HIGHLIGHTS

### Modern Design Principles
1. **Consistency:** Màu sắc, font, spacing đồng nhất
2. **Clarity:** Label rõ ràng, validation message cụ thể
3. **Feedback:** Loading spinner, success/error messages
4. **Accessibility:** Keyboard shortcuts, tab navigation

### Component Styling

**Buttons:**
```java
private void styleButton(JButton btn, Color color) {
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btn.setFocusPainted(false);
    btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
}
```

**Tables:**
```java
table.setRowHeight(30);
table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
table.getTableHeader().setBackground(new Color(52, 73, 94));
table.getTableHeader().setForeground(Color.WHITE);
table.setSelectionBackground(new Color(41, 128, 185));
table.setSelectionForeground(Color.WHITE);
```

**Input Fields:**
```java
txtField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
txtField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
    BorderFactory.createEmptyBorder(8, 12, 8, 12)
));
```

---

## 🔄 DATA SEEDING

### DataSeeder Utility
**Mục đích:** Tạo dữ liệu mẫu khi khởi động server

**Dữ liệu được seed:**

1. **3 Khoa:**
   - CNTT - Công nghệ Thông tin
   - KHMT - Khoa học Máy tính
   - HTTT - Hệ thống Thông tin

2. **5 Lớp hành chính:**
   - DHKTPM16A, DHKTPM16B, DHKTPM16C
   - DHKHMT16A
   - DHHTTT16A

3. **10 Sinh viên mẫu:**
   - Phân bổ đều các lớp
   - Có thông tin đầy đủ

4. **10 Giảng viên:**
   - Thạc sĩ, Tiến sĩ
   - Phân bổ các khoa

5. **Môn học:**
   - Lập trình Java, C++
   - Cơ sở dữ liệu
   - Lập trình phân tán
   - ...

6. **Học kỳ:**
   - HK1 2023-2024
   - HK2 2023-2024
   - HK1 2024-2025

**Smart Seeding:**
- Kiểm tra dữ liệu đã tồn tại → Skip
- Log chi tiết: "created", "skipped"
- Error handling cho từng record

---

## ⚙️ CONFIGURATION FILES

### 1. pom.xml (Maven Configuration)
```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>
```

### 2. persistence.xml (JPA Configuration)
```xml
<persistence-unit name="StudentPU_SQLServer">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <!-- 9 Entity classes -->
    <properties>
        <!-- SQL Server connection -->
        <!-- Hibernate settings -->
        <property name="hibernate.hbm2ddl.auto" value="update" />
        <property name="hibernate.show_sql" value="true" />
    </properties>
</persistence-unit>
```

### 3. logback.xml (Logging Configuration)
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

---

## 🚦 HOW TO RUN

### Prerequisites
1. **Java JDK 8+** installed
2. **Maven 3.6+** installed
3. **Microsoft SQL Server** running
4. **Database** `db_quanlysinhvien` created

### Setup Database
```sql
-- 1. Tạo database
CREATE DATABASE db_quanlysinhvien;

-- 2. Hibernate sẽ tự động tạo tables (hbm2ddl.auto = update)
-- HOẶC chạy script SQL có sẵn:
-- - create_database.sql
-- - seed_data.sql
-- - init_admin.sql
```

### Build Project
```bash
cd QuanLySinhVien
mvn clean install
```

### Run Server
```bash
# Option 1: From IDE
Run: iuh.fit.se.server.Server.main()

# Option 2: From command line
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"

# Server sẽ khởi động trên port 8888
# Log: ✓ Server started successfully!
```

### Run Client (GUI)
```bash
# Option 1: From IDE
Run: iuh.fit.se.client.ClientMain.main()

# Option 2: From command line
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.ClientMain"

# Giao diện đăng nhập sẽ hiện ra
```

### Default Login
```
Username: admin
Password: admin123
Role: ADMIN
```

---

## 🐛 TROUBLESHOOTING

### 1. Cannot connect to SQL Server
**Error:** `Cannot create PoolableConnectionFactory`

**Solution:**
- Kiểm tra SQL Server đang chạy
- Kiểm tra username/password trong persistence.xml
- Kiểm tra port 1433 mở
- Enable TCP/IP trong SQL Server Configuration Manager

### 2. ClassNotFoundException
**Error:** `java.lang.ClassNotFoundException`

**Solution:**
```bash
mvn clean install -U
```

### 3. Port 8888 already in use
**Error:** `Address already in use`

**Solution:**
- Tắt server đang chạy
- Đổi port trong Server.java và Client code

### 4. Authentication failed
**Error:** `Status.UNAUTHORIZED`

**Solution:**
- Kiểm tra token có được gửi kèm Request
- Kiểm tra token còn hợp lệ (chưa logout)
- Server restart → All tokens invalid

---

## 📈 PERFORMANCE & SCALABILITY

### Current Capacity
- **Concurrent Clients:** 10 (Thread Pool size)
- **Database Connections:** Auto-managed by Hibernate
- **Session Storage:** In-memory (ConcurrentHashMap)

### Optimization Techniques
1. **Lazy Loading:** Entity relationships loaded on-demand
2. **Connection Pooling:** Hibernate built-in pool
3. **DTO Pattern:** Reduce data transfer size
4. **Singleton Services:** Reduce object creation overhead

### Scalability Limitations
❌ **Session storage in memory** → Cannot scale horizontally
❌ **No load balancer** → Single server instance
❌ **No caching layer** → Every request hits database

### Future Improvements
- [ ] Redis for session storage
- [ ] REST API instead of Socket
- [ ] Microservices architecture
- [ ] Docker containerization
- [ ] Kubernetes orchestration

---

## 🔒 SECURITY FEATURES

### Implemented
✅ **Password Hashing:** BCrypt với automatic salt  
✅ **Token-based Authentication:** UUID tokens  
✅ **Role-based Authorization:** 3 user roles  
✅ **Input Validation:** Bean Validation API  
✅ **SQL Injection Prevention:** JPA Prepared Statements  
✅ **Encrypted Connection:** (Database level - TrustServerCertificate)

### Not Implemented (TODO)
❌ HTTPS/TLS for client-server communication  
❌ Token expiration (TTL)  
❌ Refresh tokens  
❌ Rate limiting  
❌ Audit logging  
❌ Two-factor authentication (2FA)

---

## 📚 DESIGN PATTERNS USED

### Creational Patterns
- **Singleton:** All Service classes
- **Factory:** EntityManagerFactory

### Structural Patterns
- **Adapter:** JpaStudentServiceAdapter
- **DTO (Data Transfer Object):** All DTO classes
- **Facade:** Service layer hides JPA complexity

### Behavioral Patterns
- **Strategy:** Different authentication strategies (in-memory vs JPA)
- **Command:** Command enum for request types
- **Observer:** GUI event listeners

### Architectural Patterns
- **Layered Architecture:** Presentation → Business → Data
- **Client-Server:** Network architecture
- **Repository Pattern:** Service classes act as repositories
- **MVC:** GUI panels (View), Services (Controller), Entities (Model)

---

## 📝 CODE QUALITY

### Validation
- Bean Validation annotations (@NotBlank, @Size, @Min, @Max)
- Custom validation trong Service layer
- GUI input validation với error messages

### Error Handling
```java
try {
    // Business logic
    em.getTransaction().commit();
    return new Response(Status.SUCCESS, "OK", data);
} catch (Exception e) {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
    logger.error("Error: ", e);
    return new Response(Status.ERROR, e.getMessage(), null);
}
```

### Logging
- **Level INFO:** Important events (login, CRUD operations)
- **Level DEBUG:** Detailed flow (request/response)
- **Level ERROR:** Exceptions with stack trace
- **Level WARN:** Non-critical issues

### Documentation
- JavaDoc cho classes và public methods
- Inline comments cho business logic phức tạp
- README.md, TomTat.md

---

## 🎓 HỌC ĐƯỢC GÌ TỪ DỰ ÁN NÀY?

### 1. Lập trình phân tán (Distributed Systems)
- ✅ Client-Server architecture
- ✅ Socket programming (TCP/IP)
- ✅ Object serialization
- ✅ Multi-threading với Thread Pool
- ✅ Concurrent programming (ConcurrentHashMap)

### 2. Database & ORM
- ✅ JPA/Hibernate
- ✅ Entity relationships (@OneToMany, @ManyToOne)
- ✅ Transaction management
- ✅ JPQL queries
- ✅ Database design & normalization

### 3. Software Engineering
- ✅ Layered architecture
- ✅ Design patterns (Singleton, DTO, Adapter, etc.)
- ✅ Separation of concerns
- ✅ Dependency management (Maven)
- ✅ Configuration management

### 4. Security
- ✅ Authentication & Authorization
- ✅ Password hashing (BCrypt)
- ✅ Token-based auth
- ✅ Role-based access control (RBAC)

### 5. GUI Programming
- ✅ Swing components
- ✅ Event-driven programming
- ✅ Layout managers
- ✅ Custom component styling
- ✅ User experience design

### 6. Best Practices
- ✅ Logging (SLF4J)
- ✅ Validation (Bean Validation)
- ✅ Error handling
- ✅ Code organization
- ✅ Naming conventions

---

## 📊 PROJECT STATISTICS

### Lines of Code (Estimated)
- **Total:** ~15,000 lines
- **Java:** ~14,000 lines
- **XML:** ~500 lines
- **SQL:** ~500 lines

### File Count
- **Java files:** ~60 files
- **Entity classes:** 9
- **DTO classes:** 9
- **Service classes:** 10
- **GUI classes:** 20+
- **Configuration:** 3 XML files

### Features Count
- **Commands:** 70+ operations
- **User roles:** 3
- **Database tables:** 9
- **CRUD modules:** 7

---

## 🎯 KẾT LUẬN

Đây là một **dự án hoàn chỉnh** về hệ thống quản lý đào tạo đại học, áp dụng nhiều công nghệ và kỹ thuật lập trình hiện đại:

### ✨ Điểm mạnh:
1. **Kiến trúc rõ ràng:** Phân tách client-server, layered architecture
2. **Bảo mật tốt:** Authentication, authorization, password hashing
3. **Có thể mở rộng:** Service layer, DTO pattern, protocol abstraction
4. **Giao diện đẹp:** Modern Swing GUI với màu sắc hài hòa
5. **Đầy đủ chức năng:** CRUD, nghiệp vụ phức tạp, phân quyền

### 🔧 Có thể cải thiện:
1. Chuyển sang REST API (thay Socket)
2. Implement caching (Redis)
3. Add unit tests & integration tests
4. Token expiration & refresh
5. HTTPS/TLS encryption
6. Microservices architecture
7. CI/CD pipeline

### 🎓 Giá trị học tập:
Dự án này **rất phù hợp** để học về:
- Lập trình phân tán Java
- JPA/Hibernate ORM
- Multi-threading
- Security (Authentication/Authorization)
- GUI programming
- Software architecture & design patterns

---

## 📞 CONTACT & SUPPORT

**Tác giả:** IUH - FIT - Software Engineering  
**Năm học:** 2024  
**Môn học:** Lập trình phân tán

**Email:** [your-email@example.com]  
**GitHub:** [repository-url]

---

*Tài liệu được tạo tự động bởi GitHub Copilot - Ngày 24/10/2025*

