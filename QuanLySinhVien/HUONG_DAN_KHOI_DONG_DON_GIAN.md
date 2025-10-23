# 🚀 HƯỚNG DẪN KHỞI ĐỘNG HỆ THỐNG

## ⚠️ QUAN TRỌNG: PHẢI KHỞI ĐỘNG THEO THỨ TỰ!

### Bước 1: KHỞI ĐỘNG SERVER (BẮT BUỘC CHẠY TRƯỚC)

1. Trong IntelliJ IDEA, mở file: `src/main/java/iuh/fit/se/server/Server.java`
2. **Nhấn chuột phải vào file** → chọn **"Run 'Server.main()'"**
3. Đợi cho đến khi thấy log hiển thị:
   ```
   ✓ Server started successfully!
   ✓ Listening on port: 8888
   Waiting for clients...
   ```
4. **QUAN TRỌNG**: GIỮ CỬA SỔ NÀY CHẠY, ĐỪNG TẮT!

### Bước 2: SAU ĐÓ MỚI KHỞI ĐỘNG CLIENT

1. Trong IntelliJ IDEA, mở file: `src/main/java/iuh/fit/se/client/gui/LoginFrame.java`
2. **Nhấn chuột phải vào file** → chọn **"Run 'LoginFrame.main()'"**
3. Cửa sổ đăng nhập sẽ hiện ra

### Bước 3: ĐĂNG NHẬP

#### Tài khoản mặc định (In-Memory Mode):

**Admin:**
- Username: `admin`
- Password: `admin123`

**Sinh viên mẫu:**
- Username: `sv001`
- Password: `123456`

**Giảng viên mẫu:**
- Username: `gv001`
- Password: `123456`

---

## ❌ NẾU GẶP LỖI "Connection refused"

**Nguyên nhân**: Server chưa được khởi động

**Giải pháp**:
1. Kiểm tra xem Server có đang chạy không (xem tab Run/Console trong IntelliJ)
2. Nếu không thấy → Quay lại **Bước 1** và khởi động Server
3. Nếu Server đã chạy nhưng vẫn lỗi → **Dừng Server** (nhấn nút Stop đỏ) rồi khởi động lại

---

## 🔧 CHẾ ĐỘ HOẠT ĐỘNG

Hiện tại server đang chạy ở **IN-MEMORY MODE** (không cần SQL Server):
- ✅ Khởi động nhanh
- ✅ Không cần cấu hình database
- ⚠️ Dữ liệu sẽ MẤT khi tắt server

### Để dùng SQL Server:

1. Đảm bảo SQL Server đang chạy
2. Tạo database `db_quanlysinhvien`
3. Chạy file `seed_data.sql`
4. Sửa file `Server.java`: Đổi `SKIP_JPA = true` thành `SKIP_JPA = false`
5. Khởi động lại Server

---

## 📝 GHI CHÚ

- **Server** phải chạy TRƯỚC Client
- Một Server có thể phục vụ nhiều Client cùng lúc
- Khi tắt Server, tất cả Client sẽ bị ngắt kết nối

