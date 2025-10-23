# HƯỚNG DẪN KHỞI ĐỘNG HỆ THỐNG - QUAN TRỌNG!

## ⚠️ VẤN ĐỀ BẠN VỪA GẶP

**Lỗi:** `An established connection was aborted by the software in your host machine`

**Nguyên nhân:**
1. Server đã khởi động và lắng nghe port 8888 ✓
2. Client kết nối thành công ✓
3. NHƯNG khi gửi LOGIN request, **Server bị crash** vì:
   - Server.java đang ở chế độ `SKIP_JPA = true` (in-memory mode)
   - Các Service mới (AuthService, KhoaService...) cần JPA để hoạt động
   - Khi xử lý LOGIN, Server không tìm thấy AuthService → crash → đóng kết nối

**Đã sửa:**
✅ Đổi `SKIP_JPA = false` trong Server.java
✅ Thêm log chi tiết để debug
✅ Xử lý lỗi tốt hơn

---

## 📋 CHECKLIST TRƯỚC KHI CHẠY

### 1. Kiểm tra SQL Server
```cmd
# Mở SQL Server Management Studio hoặc Azure Data Studio
# Kiểm tra SQL Server đang chạy
```

### 2. Tạo Database
```sql
-- Chạy trong SQL Server Management Studio
CREATE DATABASE db_quanlysinhvien;
GO

-- Kiểm tra database đã tạo
SELECT name FROM sys.databases WHERE name = 'db_quanlysinhvien';
```

### 3. Kiểm tra Cấu hình
Mở file: `src/main/resources/META-INF/persistence.xml`

**Kiểm tra các thông tin:**
```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:sqlserver://localhost:1433;databaseName=db_quanlysinhvien;encrypt=true;trustServerCertificate=true;" />
<property name="javax.persistence.jdbc.user" value="sa" />
<property name="javax.persistence.jdbc.password" value="sapassword" />
```

**Lưu ý:**
- Nếu SQL Server dùng Windows Authentication → đổi user/password
- Nếu port khác 1433 → đổi port trong URL
- Nếu tên database khác → đổi databaseName

---

## 🚀 CÁCH CHẠY ĐÚNG (THEO THỨ TỰ)

### BƯỚC 1: Khởi động Server (BẮT BUỘC CHẠY TRƯỚC)

#### Cách 1: Chạy trong IntelliJ IDEA
1. Mở class: `iuh.fit.se.server.Server`
2. Click chuột phải → **Run 'Server.main()'**
3. Chờ console hiển thị:

```
========================================
✓ Server started successfully!
✓ Listening on port: 8888
✓ Thread pool size: 10
✓ JPA mode: ENABLED (SQL Server)
========================================
Waiting for clients...
```

**Nếu thấy lỗi JPA:**
```
✗ Cannot initialize JPA EntityManagerFactory!
Please check:
  1. SQL Server is running
  2. Database 'db_quanlysinhvien' exists
  3. Connection string in persistence.xml is correct
  4. Username/Password are correct
```
→ **DỪNG LẠI**, sửa lỗi theo hướng dẫn, rồi mới chạy tiếp!

#### Cách 2: Chạy từ Terminal
```cmd
cd C:\Users\ADMINS\Downloads\BaiTapPhanTan_QuanLySinhVien\QuanLySinhVien
mvn exec:java -Dexec.mainClass="iuh.fit.se.server.Server"
```

### BƯỚC 2: Chạy Seed Data (TẠO DỮ LIỆU MẪU)

**Chỉ chạy 1 LẦN sau khi Server khởi động thành công lần đầu!**

1. Mở **SQL Server Management Studio**
2. Connect tới server
3. Mở file: `seed_data.sql`
4. Chọn database: `db_quanlysinhvien`
5. Click **Execute** (F5)

**Kết quả mong đợi:**
```
Đã tạo dữ liệu mẫu thành công!

=== THÔNG TIN ĐĂNG NHẬP MẶC ĐỊNH ===
Admin:
  Username: admin
  Password: 123456

Giảng viên:
  Username: gv001 | Password: 123456
  Username: gv002 | Password: 123456

Sinh viên:
  Username: 20001001 | Password: 123456
  Username: 20001002 | Password: 123456
```

### BƯỚC 3: Khởi động GUI Client

#### Cách 1: Chạy trong IntelliJ IDEA
1. Mở class: `iuh.fit.se.client.gui.LoginFrame`
2. Click chuột phải → **Run 'LoginFrame.main()'**
3. Màn hình đăng nhập sẽ hiển thị

#### Cách 2: Chạy từ Terminal
```cmd
cd C:\Users\ADMINS\Downloads\BaiTapPhanTan_QuanLySinhVien\QuanLySinhVien
mvn exec:java -Dexec.mainClass="iuh.fit.se.client.gui.LoginFrame"
```

**Console của Client sẽ hiển thị:**
```
[AWT-EventQueue-0] INFO  NetworkClient - Connected to server localhost:8888
```

### BƯỚC 4: Đăng nhập

**Màn hình đăng nhập:**
```
Username: admin
Password: 123456
```

Click **"Đăng nhập"** → Chờ 1-2 giây → Màn hình chính hiển thị!

---

## 🔍 KIỂM TRA KHI GẶP LỖI

### Lỗi 1: "Cannot connect to server"
**Console Client:** `Cannot connect to localhost:8888`

**Nguyên nhân:** Server chưa chạy

**Giải pháp:**
1. Kiểm tra Server có đang chạy không
2. Xem console của Server có dòng "Waiting for clients..." không
3. Nếu không → Start Server lại

### Lỗi 2: "Connection aborted" (Lỗi bạn vừa gặp)
**Console Client:** `An established connection was aborted`

**Nguyên nhân:** Server bị crash khi xử lý request

**Giải pháp:**
1. **DỪNG Server** (Ctrl+C hoặc Stop button)
2. Kiểm tra console Server có lỗi gì không
3. Nếu có lỗi JPA → Sửa persistence.xml
4. **RESTART Server**
5. Chạy lại Client

### Lỗi 3: JPA EntityManagerFactory Error
**Console Server:** `Cannot initialize JPA EntityManagerFactory`

**Nguyên nhân:** Không kết nối được SQL Server

**Giải pháp:**
```cmd
# 1. Kiểm tra SQL Server đang chạy
services.msc → Tìm "SQL Server" → Status = "Running"

# 2. Kiểm tra port 1433
netstat -an | findstr 1433

# 3. Kiểm tra database tồn tại
# Mở SSMS → Connect → Xem database "db_quanlysinhvien" có tồn tại không

# 4. Test connection string
# Trong SSMS, thử connect với username/password trong persistence.xml
```

### Lỗi 4: "Tài khoản không tồn tại"
**GUI Client:** Dialog lỗi "Tài khoản không tồn tại"

**Nguyên nhân:** Chưa chạy seed_data.sql

**Giải pháp:**
1. Mở SSMS
2. Execute file `seed_data.sql`
3. Kiểm tra:
```sql
USE db_quanlysinhvien;
SELECT * FROM tai_khoan;
-- Phải có 6 records
```

---

## 📊 KIỂM TRA HỆ THỐNG HOẠT ĐỘNG ĐÚNG

### Console Server (khi hoạt động tốt):
```
[main] INFO Server - Starting server on port 8888...
[main] INFO Server - Initializing JPA with SQL Server...
[main] INFO Server - ✓ JPA initialized successfully with SQL Server
[main] INFO Server - ✓ All Service singletons will be initialized on first use
[main] INFO Server - ========================================
[main] INFO Server - ✓ Server started successfully!
[main] INFO Server - ✓ Listening on port: 8888
[main] INFO Server - ✓ Thread pool size: 10
[main] INFO Server - ✓ JPA mode: ENABLED (SQL Server)
[main] INFO Server - ========================================
[main] INFO Server - Waiting for clients...
[pool-1-thread-1] INFO Server - → New client connected: 127.0.0.1 (Port: 54321)
[pool-1-thread-1] INFO ClientHandler - Handling client: 127.0.0.1
[pool-1-thread-1] INFO ClientHandler - Received from 127.0.0.1: LOGIN
[pool-1-thread-1] INFO AuthService - User 'admin' logged in successfully with role ADMIN
[pool-1-thread-1] INFO ClientHandler - Sent to 127.0.0.1: SUCCESS
```

### Console Client (khi hoạt động tốt):
```
[AWT-EventQueue-0] INFO NetworkClient - Connected to server localhost:8888
[SwingWorker-pool-1-thread-1] INFO NetworkClient - Request sent successfully
[AWT-EventQueue-0] INFO LoginFrame - Login successful for user: admin
```

---

## 🎯 TÓM TẮT NHANH

**THỨ TỰ CHẠY:**
1. ✅ **Start SQL Server** (services.msc)
2. ✅ **Create Database** (nếu chưa có)
3. ✅ **Start Server** (Server.java) → Chờ thấy "Waiting for clients..."
4. ✅ **Run seed_data.sql** (chỉ 1 lần)
5. ✅ **Start Client GUI** (LoginFrame.java)
6. ✅ **Login:** admin / 123456

**QUAN TRỌNG:**
- Server PHẢI chạy TRƯỚC Client
- Seed data PHẢI chạy TRƯỚC khi login
- Nếu Server crash → RESTART Server trước khi chạy Client lại

---

## 💡 GỢI Ý DEBUG

### Nếu vẫn không chạy được:
1. **Copy toàn bộ console log của Server** → Gửi cho tôi
2. **Copy toàn bộ console log của Client** → Gửi cho tôi
3. **Chụp màn hình lỗi** → Gửi cho tôi

### Test nhanh SQL Server:
```sql
-- Chạy trong SSMS
USE db_quanlysinhvien;

-- Kiểm tra tables đã được tạo
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;
-- Phải có: tai_khoan, khoa, lop_hoc, sinh_vien, giang_vien, mon_hoc, hoc_ky, lop_hoc_phan, diem

-- Kiểm tra dữ liệu
SELECT username, role FROM tai_khoan;
-- Phải có: admin, gv001, gv002, 20001001, 20001002
```

---

**Chúc bạn thành công!** 🚀

