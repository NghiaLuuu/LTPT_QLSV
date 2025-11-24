# 🔧 Debug & Fix Lỗi 403 Forbidden

## ❌ Lỗi hiện tại:
```
╔═══════════════════════════════════════════════════════════════╗
║ ❌ [API-ERROR] Request failed
║ 🔢 Status: 403
║ 🌐 URL: http://localhost:8080/api/students/1
║ 📝 Response: 
╚═══════════════════════════════════════════════════════════════╝
```

---

## 🔍 NGUYÊN NHÂN

Lỗi **403 Forbidden** xảy ra khi:
1. ✅ Request đến đúng endpoint
2. ✅ Backend nhận được request
3. ❌ Backend **TỪ CHỐI** vì thiếu quyền (Authorization failed)

### Endpoint yêu cầu:
```java
@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")  // ← Chỉ ADMIN mới được phép!
public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request)
```

---

## 🚨 CÁC TRƯỜNG HỢP GÂY LỖI 403

### Trường hợp 1: Login bằng tài khoản STUDENT
**Vấn đề:** Đang login bằng sinh viên (role: STUDENT) nhưng cố gắng cập nhật thông tin sinh viên khác.

**Console log sẽ hiển thị:**
```
╔════════════════════════════════════════════════════════════════╗
║ 🔄 [API-PUT] Đang gửi PUT request
║ 🌐 Endpoint: /students/1
║ 👤 Username: SV00000001          ← Đây là STUDENT!
║ 🎭 Role: STUDENT                  ← Không phải ADMIN!
║ 🔑 Token: Bearer eyJhbGciOiJIUzI1...
╚════════════════════════════════════════════════════════════════╝
```

**Giải pháp:**
- ✅ **Đăng xuất** và đăng nhập lại với tài khoản ADMIN
- ✅ Tài khoản ADMIN mặc định: `admin` / `admin123`

---

### Trường hợp 2: Token hết hạn hoặc không hợp lệ
**Vấn đề:** JWT token đã hết hạn hoặc bị corrupt.

**Console log sẽ hiển thị:**
```
╔════════════════════════════════════════════════════════════════╗
║ 🔄 [API-PUT] Đang gửi PUT request
║ 🌐 Endpoint: /students/1
║ 👤 Username: admin
║ 🎭 Role: ADMIN                    ← Role đúng rồi!
║ 🔑 Token: Bearer eyJhbGciOiJIUzI1...
╚════════════════════════════════════════════════════════════════╝

⚠️  [API-401] Token expired, attempting refresh...
❌ [API-REFRESH-FAILED] Không thể refresh token
```

**Giải pháp:**
- ✅ Đăng xuất và đăng nhập lại
- ✅ Backend sẽ cấp token mới

---

### Trường hợp 3: Token NULL hoặc rỗng
**Vấn đề:** Token không được lưu sau khi login.

**Console log sẽ hiển thị:**
```
╔════════════════════════════════════════════════════════════════╗
║ 🔄 [API-PUT] Đang gửi PUT request
║ 🌐 Endpoint: /students/1
║ 👤 Username: admin
║ 🎭 Role: ADMIN
║ 🔑 Token: NULL                    ← Token bị mất!
╚════════════════════════════════════════════════════════════════╝
```

**Giải pháp:**
- ✅ Kiểm tra lại luồng login
- ✅ Đảm bảo `ApiClient.login()` đã được gọi thành công
- ✅ Restart application và login lại

---

## 📋 HƯỚNG DẪN DEBUG CHI TIẾT

### Bước 1: Xem console log khi PUT request
Sau khi thêm code mới, khi bạn cập nhật sinh viên, console sẽ hiển thị:

```
╔════════════════════════════════════════════════════════════════╗
║ 🔄 [API-PUT] Đang gửi PUT request
║ 🌐 Endpoint: /students/1
║ 👤 Username: ???                  ← Xem username ở đây
║ 🎭 Role: ???                      ← Xem role ở đây
║ 🔑 Token: ???                     ← Xem token có NULL không
╚════════════════════════════════════════════════════════════════╝
```

### Bước 2: Kiểm tra giá trị
| Field | Giá trị mong đợi | Nếu sai |
|-------|------------------|---------|
| Username | `admin` hoặc tài khoản admin khác | Đang login sai tài khoản |
| Role | `ADMIN` | Đang login bằng STUDENT hoặc LECTURER |
| Token | `Bearer eyJ...` (có giá trị) | Token NULL → Chưa login hoặc token bị mất |

### Bước 3: Xem response lỗi chi tiết
```
╔═══════════════════════════════════════════════════════════════╗
║ ❌ [API-ERROR] Request failed
║ 🔢 Status: 403
║ 🌐 URL: http://localhost:8080/api/students/1
║ 📝 Response: Access Denied      ← Backend message
╚═══════════════════════════════════════════════════════════════╝
```

---

## ✅ GIẢI PHÁP NHANH

### Giải pháp 1: Đảm bảo login đúng tài khoản ADMIN

1. **Đăng xuất** khỏi ứng dụng hiện tại
2. **Login lại** với tài khoản ADMIN:
   - Username: `admin`
   - Password: `admin123` (hoặc password admin của bạn)

3. **Kiểm tra console sau khi login:**
```
╔════════════════════════════════════════════════════════════════╗
║ ✅ [CLIENT-AUTH] Đăng nhập thành công
║ 👤 Username: admin
║ 🎭 Role: ADMIN                    ← Phải là ADMIN!
╚════════════════════════════════════════════════════════════════╝
```

4. **Thử cập nhật sinh viên lại** và xem console

---

### Giải pháp 2: Kiểm tra Backend có chạy không

**Chạy backend:**
```bash
cd C:\Users\ADMINS\Downloads\QLSV\QLSV
mvn spring-boot:run
```

**Kiểm tra backend log:**
```
Started Application in X.XXX seconds
```

---

### Giải pháp 3: Kiểm tra SecurityConfig

Đảm bảo backend SecurityConfig cho phép ADMIN cập nhật sinh viên:

```java
// Backend: SecurityConfig.java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.PUT, "/api/students/**").hasRole("ADMIN")  // ← Phải có config này
    .anyRequest().authenticated()
);
```

---

## 🧪 TEST SAU KHI FIX

### Test Case 1: Login ADMIN và cập nhật sinh viên

1. **Login với ADMIN:**
   - Username: `admin`
   - Password: `admin123`

2. **Vào Quản lý sinh viên → Chọn sinh viên → Sửa → Lưu**

3. **Xem console:**
```
╔════════════════════════════════════════════════════════════════╗
║ 🔄 [API-PUT] Đang gửi PUT request
║ 🌐 Endpoint: /students/1
║ 👤 Username: admin
║ 🎭 Role: ADMIN
║ 🔑 Token: Bearer eyJhbGciOiJIUzI1...
╚════════════════════════════════════════════════════════════════╝

🌐 [API-CALL] PUT /students/1
✅ [API-SUCCESS] PUT /students/1 - Status: 200
✅ [API-PUT] Request thành công

📢 [WEBSOCKET - REAL-TIME] Broadcast cập nhật sinh viên đến group chung
   ├─ Mã SV: SV00000001
   ├─ Họ tên: Nguyễn Văn A (Đã cập nhật)
   ├─ Email: newemail@example.com
   └─ Topic: /topic/students/updates
```

4. **Kiểm tra UI sinh viên:** Thông tin tự động cập nhật real-time

---

### Test Case 2: Login STUDENT và xem thông tin (không được sửa)

1. **Login với STUDENT:**
   - Username: `SV00000001`
   - Password: `12345678`

2. **Vào Thông tin cá nhân** (chỉ xem, không được sửa)

3. **Nếu cố gắng gọi API PUT sẽ bị lỗi 403** (đúng như mong đợi)

---

## 📊 CHECKLIST DEBUG

- [ ] Console có hiển thị log PUT request không?
- [ ] Username trong log có phải ADMIN không?
- [ ] Role trong log có phải ADMIN không?
- [ ] Token có giá trị (không NULL) không?
- [ ] Backend đang chạy ở http://localhost:8080 không?
- [ ] Đã login thành công chưa?
- [ ] Refresh token có hợp lệ không?

---

## 🔑 TÀI KHOẢN MẶC ĐỊNH

Nếu bạn chưa có tài khoản ADMIN, hãy tạo trong database hoặc dùng tài khoản mặc định:

```sql
-- Tài khoản ADMIN mặc định
Username: admin
Password: admin123
Role: ADMIN

-- Tài khoản STUDENT (không có quyền sửa sinh viên khác)
Username: SV00000001
Password: 12345678
Role: STUDENT
```

---

## 📞 NẾU VẪN BỊ LỖI

### In toàn bộ console log và gửi cho dev:

1. **Copy console log từ lúc login đến lúc lỗi**
2. **Chụp màn hình dialog lỗi**
3. **Kiểm tra backend log xem có error gì không**

### Backend log cần kiểm tra:
```
ERROR: Access Denied
Forbidden: User with role [STUDENT] attempted to access endpoint requiring [ADMIN]
```

---

## 🎯 TÓM TẮT

**Lỗi 403 = Thiếu quyền truy cập**

**Giải pháp:**
1. ✅ Login bằng tài khoản **ADMIN**
2. ✅ Đảm bảo token hợp lệ
3. ✅ Backend đang chạy
4. ✅ Xem console log để biết chính xác lỗi gì

**Console log mới sẽ giúp bạn:**
- 🔍 Biết đang login bằng tài khoản nào
- 🔍 Biết role hiện tại là gì
- 🔍 Biết token có hợp lệ không
- 🔍 Debug nhanh hơn!

---

🎉 **Chạy lại và xem console log để biết chính xác vấn đề!**

