# Hệ thống Quản lý Sinh viên (Client-Server)

## Mô tả
Hệ thống quản lý sinh viên sử dụng kiến trúc Client-Server với:
- **Server**: Java đa luồng (Thread Pool), JPA/Hibernate, SQL Server
- **Client**: Console application với giao diện menu
- **Protocol**: Socket communication với Request/Response pattern

## Cấu trúc dự án

```
QuanLySinhVien/
├── src/main/java/iuh/fit/se/
│   ├── common/
│   │   ├── model/          # Entity và DTO classes
│   │   │   ├── Gender.java
│   │   │   ├── SinhVien.java (JPA Entity)
│   │   │   └── SinhVienDTO.java
│   │   └── protocol/       # Giao thức truyền thông
│   │       ├── Command.java
│   │       ├── Status.java
│   │       ├── Request.java
│   │       └── Response.java
│   ├── server/
│   │   ├── Server.java     # Main server application
│   │   ├── net/
│   │   │   └── ClientHandler.java
│   │   ├── service/
│   │   │   ├── IStudentService.java
│   │   │   ├── StudentService.java (In-memory)
│   │   │   ├── JpaStudentService.java (Database)
│   │   │   └── JpaStudentServiceAdapter.java
│   │   └── util/
│   │       └── SinhVienMapper.java
│   └── client/
│       ├── Client.java     # Main client application
│       └── net/
│           └── NetworkClient.java
└── src/main/resources/
    ├── META-INF/persistence.xml
    └── logback.xml
```

## Yêu cầu hệ thống

### Phần mềm cần thiết
- Java JDK 8 trở lên
- Maven 3.x
- SQL Server 2012 trở lên (tùy chọn - có thể dùng in-memory)

### Cơ sở dữ liệu (nếu dùng SQL Server)
1. Cài đặt SQL Server
2. Tạo database:
```sql
CREATE DATABASE db_quanlysinhvien;
```
3. Cập nhật thông tin kết nối trong `src/main/resources/META-INF/persistence.xml`:
   - URL: jdbc:sqlserver://localhost:1433;databaseName=db_quanlysinhvien
   - Username: sa
   - Password: sapassword (thay đổi theo cấu hình của bạn)

## Cài đặt và Build

### 1. Clone hoặc tải project về

### 2. Build project với Maven
```bash
cd QuanLySinhVien
mvn clean package
```

### 3. Chạy ứng dụng

#### Chạy Server (Terminal 1)
```bash
# Với Maven
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"

# Hoặc với Java
java -cp target/QuanLySinhVien-1.0-SNAPSHOT.jar iuh.fit.se.server.Server
```

**Lưu ý:** 
- Server sẽ tự động chọn JPA-backed service nếu kết nối database thành công
- Nếu không kết nối được database, server sẽ fallback về in-memory service
- Server lắng nghe trên port 8080

#### Chạy Client (Terminal 2)
```bash
# Với Maven
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.Client"

# Hoặc với Java
java -cp target/QuanLySinhVien-1.0-SNAPSHOT.jar iuh.fit.se.client.Client
```

## Sử dụng

### Menu chính
```
╔═══════════════════════════════════════╗
║   HỆ THỐNG QUẢN LÝ SINH VIÊN         ║
╠═══════════════════════════════════════╣
║ 1. Thêm sinh viên                     ║
║ 2. Cập nhật sinh viên                 ║
║ 3. Xóa sinh viên                      ║
║ 4. Tìm sinh viên theo mã             ║
║ 5. Xem tất cả sinh viên               ║
║ 0. Thoát                              ║
╚═══════════════════════════════════════╝
```

### Chức năng

#### 1. Thêm sinh viên
- Nhập thông tin: Mã SV, Họ tên, Ngày sinh, Giới tính, Chuyên ngành, Lớp, Điểm TB
- Validation tự động theo Bean Validation:
  - Mã SV: 5-20 ký tự, không trùng
  - Điểm TB: 0-10
  - Ngày sinh: phải là ngày quá khứ

#### 2. Cập nhật sinh viên
- Tìm sinh viên theo mã
- Hiển thị thông tin hiện tại
- Nhập thông tin mới (Enter để giữ nguyên giá trị cũ)

#### 3. Xóa sinh viên
- Nhập mã sinh viên
- Xác nhận trước khi xóa

#### 4. Tìm sinh viên
- Tìm theo mã sinh viên
- Hiển thị thông tin chi tiết

#### 5. Xem tất cả sinh viên
- Hiển thị danh sách dạng bảng
- Tổng hợp số lượng sinh viên

## Kiến trúc kỹ thuật

### Server
- **Thread Pool**: ExecutorService với 10 threads đồng thời
- **JPA/Hibernate**: Quản lý persistence
- **Thread-safe**: Mỗi request tạo EntityManager riêng
- **Logging**: SLF4J + Logback
- **Graceful shutdown**: ShutdownHook để đóng tài nguyên

### Client
- **Socket communication**: ObjectInputStream/ObjectOutputStream
- **Input validation**: Kiểm tra định dạng và giá trị hợp lệ
- **Error handling**: Xử lý mất kết nối và lỗi server

### Protocol
- **Request**: Command + Data
- **Response**: Status + Message + Data
- **Serialization**: Java Object Serialization

### Data Flow
```
Client → Request → Socket → Server → ClientHandler → Service → Database
                                                              ↓
Client ← Response ← Socket ← Server ← ClientHandler ← Service ← Database
```

## Các tính năng nâng cao đã implement

1. ✅ **Thread-safe Service**: Mỗi request có EntityManager riêng
2. ✅ **DTO Pattern**: Tách biệt Entity (database) và DTO (network)
3. ✅ **Mapper**: Chuyển đổi Entity ↔ DTO
4. ✅ **Logging**: Chi tiết request/response và exceptions
5. ✅ **Protocol Versioning**: Hỗ trợ version trong Request
6. ✅ **Dual-mode Service**: JPA hoặc In-memory fallback
7. ✅ **Bean Validation**: Validation annotations trên Entity
8. ✅ **Input Validation**: Client-side validation

## Troubleshooting

### Server không khởi động
- Kiểm tra port 8080 có bị chiếm không
- Kiểm tra thông tin kết nối database trong persistence.xml

### Client không kết nối được
- Đảm bảo Server đang chạy
- Kiểm tra host và port trong Client.java (mặc định localhost:8080)

### Lỗi JPA/Database
- Server sẽ tự động fallback về in-memory mode
- Kiểm tra log để xem chi tiết lỗi
- Kiểm tra SQL Server có đang chạy không

### Lỗi compile
- Chạy `mvn clean install -U` để tải lại dependencies
- Kiểm tra Java version (cần JDK 8+)

## Mở rộng

### Thêm chức năng mới
1. Thêm Command mới vào `Command.java`
2. Thêm logic xử lý trong `ClientHandler.handleRequest()`
3. Thêm method trong `IStudentService` và implementations
4. Thêm menu item trong `Client.java`

### Thêm Entity mới
1. Tạo Entity class trong `common/model/`
2. Tạo DTO tương ứng
3. Tạo Mapper
4. Tạo Service interface và implementations
5. Cập nhật persistence.xml

## Tác giả
Dự án bài tập Lập trình phân tán - IUH

## License
Educational purpose only

