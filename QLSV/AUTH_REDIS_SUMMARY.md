# TỔNG KẾT TÍCH HỢP AUTH / REDIS

Tệp này tóm tắt những gì đã được thực hiện theo nội dung của `Auth_Redis.md`. Mỗi mục ghi trạng thái (Hoàn thành / Một phần / Chưa), các file đã thay đổi, cách kiểm tra nhanh và ghi chú quan trọng.

---

## 1) Hệ thống xác thực (Authentication)

Mục tiêu: Thêm Access Token (JWT) + Refresh Token, hỗ trợ login/register/logout, lưu refresh token vào DB, lưu access token trên Redis, và bổ sung endpoint `/api/auth/refresh`.

Các công việc & trạng thái:
- Đăng nhập (Login): HOÀN THÀNH
  - Mô tả: Xác thực bằng username/password, trả về `JwtResponse` chứa `token` (access JWT) và `refreshToken` (UUID).
  - File liên quan: `src/main/java/iuh/fit/se/service/impl/AuthServiceImpl.java`, `src/main/java/iuh/fit/se/controller/AuthController.java`.
- Đăng ký (Register): HOÀN THÀNH
  - File: `AuthServiceImpl.java`, `AuthController.java`.
- Đăng xuất (Logout) — revoke phía server: HOÀN THÀNH
  - Mô tả: Endpoint `POST /api/auth/logout` xóa `refresh_token` trong DB và xóa access token trên Redis (key `auth:token:{username}`).
  - File: `AuthController.java`, `AuthServiceImpl.java`.
- Sinh Access Token (JWT): HOÀN THÀNH
  - File: `src/main/java/iuh/fit/se/config/JwtUtils.java`.
- Sinh Refresh Token: HOÀN THÀNH
  - Lưu refresh token vào cột `users.refresh_token` trong DB.
  - Model: `src/main/java/iuh/fit/se/model/User.java` (thêm trường `refreshToken`), migration: `database/migration_add_refresh_token.sql`.
- Lưu Access Token vào Redis: HOÀN THÀNH
  - Quy ước key: `auth:token:{username}`; value = JWT; TTL = thời hạn của JWT.
  - File: `AuthServiceImpl.java` (sử dụng `StringRedisTemplate`).
- Endpoint `/api/auth/refresh`: HOÀN THÀNH
  - File: `AuthController.java` và phương thức `AuthService.refreshToken(...)` được triển khai trong `AuthServiceImpl.java`.

Ghi chú: Backend trả cả `token` và `refreshToken` trong `JwtResponse`.

---

## 2) Cấu hình Redis

### 2.1 Redis phía server
- Thêm dependency: `spring-boot-starter-data-redis` và `lettuce-core` trong `pom.xml` (chạy Maven để tải các dependency này).
- Cấu hình kết nối Redis nằm trong `src/main/resources/application.properties` (`spring.redis.host`, `spring.redis.port`).
- Bean Redis: `src/main/java/iuh/fit/se/config/RedisConfig.java` cung cấp `StringRedisTemplate`.
- Ứng dụng Redis:
  - Lưu access token: `AuthServiceImpl` (key `auth:token:{username}`).
  - Rate limiter (phía server) dùng bộ đếm trên Redis: `src/main/java/iuh/fit/se/route/RateLimitFilter.java`.

### 2.2 Redis client / cache cục bộ
- Đã hiện thực một cache cục bộ trong ứng dụng `LocalCacheClient` (ConcurrentHashMap) để cache kết quả tra cứu `User` và giảm truy vấn DB.
  - File: `src/main/java/iuh/fit/se/util/LocalCacheClient.java`.
  - `UserDetailsServiceImpl` sử dụng `LocalCacheClient`. Cache được xóa (evict) khi user register hoặc đổi mật khẩu trong `AuthServiceImpl`.
  - `LocalCacheClient` in console thông báo MISS/HIT (ví dụ: `[LocalCache] MISS for key=...`).

Ghi chú: Đây là cache chạy trong tiến trình ứng dụng. Nếu muốn cache phân tán, có thể mở rộng để dùng Redis.

---

## 3) Retry & Giới hạn tần suất (Rate Limiting)

### Phía server
- Đã triển khai `RateLimitFilter` (`src/main/java/iuh/fit/se/route/RateLimitFilter.java`) thực hiện:
  - Tăng bộ đếm Redis `rate:requests:{username}` cho mỗi request.
  - Thiết lập TTL cho cửa sổ giới hạn ( cấu hình `app.rate-limit.window-seconds`, mặc định 300s).
  - Nếu bộ đếm vượt `app.rate-limit.max-requests` (mặc định 3) thì trả HTTP 429.
  - Filter được đăng ký trong `SecurityConfig` trước filter xác thực.

### Phía client (FE)
- `ApiClient` (ứng dụng GUI Swing) hiện có logic retry/backoff:
  - Khi nhận HTTP 401: tự gọi `/api/auth/refresh` rồi thử lại request một lần.
  - Khi nhận HTTP 429: áp dụng exponential backoff (thử lại tối đa 3 lần), nếu header `Retry-After` trả về sẽ tôn trọng giá trị đó; nếu vẫn thất bại sẽ hiển thị cảnh báo cho người dùng.
  - File: `src/main/java/iuh/fit/se/gui/util/ApiClient.java` (chứa `refresh()`, `sendWithAuth()` và logic backoff/notification).

Ghi chú: `ApiClient` cũng lưu `refreshToken` (đã mã hoá) và cố auto-refresh khi khởi động nếu có giá trị persisted.

---

## 4) Tích hợp Frontend (GUI)

- Repo đã có GUI Swing gọi backend thông qua `ApiClient`.
  - `src/main/java/iuh/fit/se/gui/util/ApiClient.java`:
    - Lưu token (access + refresh), hỗ trợ auto-refresh trên 401, xử lý 429 với backoff, lưu `refreshToken` được mã hoá trong Java Preferences, hiển thị dialog Swing khi auto-refresh thất bại.
  - `LoginFrame` và `StudentLoginFrame` sử dụng `ApiClient.login(...)` để đăng nhập.
- Lưu ý bảo mật:
  - `refreshToken` được lưu trong Java Preferences dưới dạng ciphertext (AES-GCM), khóa AES lưu ở file `%USERPROFILE%\\.qlsv\\secret.key` (tạo nếu chưa có). Cải thiện so với lưu plaintext nhưng vẫn có rủi ro nếu attacker có truy cập hệ thống tệp.

---

## 5) Danh sách file đã thay đổi / tạo (một số chính)

- Backend (server):
  - `src/main/java/iuh/fit/se/config/JwtUtils.java` (hàm hỗ trợ JWT)
  - `src/main/java/iuh/fit/se/config/RedisConfig.java`
  - `src/main/java/iuh/fit/se/config/SecurityConfig.java` (điều chỉnh thứ tự filter + luật auth)
  - `src/main/java/iuh/fit/se/controller/AuthController.java` (login, register, refresh, change-password, logout)
  - `src/main/java/iuh/fit/se/service/AuthService.java` (mở rộng giao diện)
  - `src/main/java/iuh/fit/se/service/impl/AuthServiceImpl.java` (các flow auth + logout)
  - `src/main/java/iuh/fit/se/route/AuthTokenFilter.java` (validate JWT và đối chiếu token với Redis)
  - `src/main/java/iuh/fit/se/route/RateLimitFilter.java` (giới hạn tần suất phía server)
  - `src/main/java/iuh/fit/se/util/LocalCacheClient.java` (cache cục bộ)
  - `src/main/java/iuh/fit/se/service/impl/UserDetailsServiceImpl.java` (dùng LocalCacheClient)
  - `src/main/java/iuh/fit/se/model/User.java` (thêm trường `refreshToken`)

- Frontend (client GUI):
  - `src/main/java/iuh/fit/se/gui/util/ApiClient.java` (thay đổi lớn: refresh, retry, backoff, persistence, encryption, logout)
  - `src/main/java/iuh/fit/se/gui/view/LoginFrame.java` (gọi ApiClient.login)

- DB migration:
  - `database/migration_add_refresh_token.sql` (thêm cột `refresh_token` vào bảng `users`)

- Repo metadata:
  - `pom.xml` (thêm dependency Redis)
  - `src/main/resources/application.properties` (thêm cấu hình Redis và rate-limit)

---

## 6) Hướng dẫn chạy & kiểm tra nhanh

Yêu cầu: Java 17, Maven, Redis, SQL Server (theo `application.properties`).

1) Khởi Redis (Docker):
```bat
docker run -d --name qlsv-redis -p 6379:6379 redis:7
```

2) Đảm bảo DB có cột `users.refresh_token` (chạy `database/migration_add_refresh_token.sql` nếu cần).

3) Build & chạy backend:
```bat
cd C:\Users\ADMINS\Downloads\QLSV\QLSV
mvn -DskipTests spring-boot:run
```

4) Mở GUI (IDE hoặc chạy class chính). Đăng nhập bằng `admin` / `admin123` (mặc định trong `database/init.sql`).

Các kịch bản kiểm thử:
- Đăng nhập: nhận access + refresh token; GUI lưu refresh token (mã hoá).
- Đóng & mở lại GUI: background auto-refresh cố lấy access token mới; nếu thất bại sẽ hiển thị cảnh báo.
- Làm cho access token không hợp lệ (xóa key Redis) rồi thực hiện thao tác: client sẽ gọi `/api/auth/refresh` và retry.
- Gây 429 bằng cách gửi quá nhiều request (hoặc tạm cấu hình `app.rate-limit.max-requests=1`) để kiểm thử backoff; client sẽ retry theo backoff rồi hiện thông báo nếu hết lượt thử.
- Logout: client gọi `POST /api/auth/logout`; kiểm tra DB để thấy `users.refresh_token` = NULL và Redis key bị xóa.

---

## 7) Hạn chế & ghi chú bảo mật

- Khóa mã hoá được lưu ở `%USERPROFILE%\\.qlsv\\secret.key` (hoặc `$HOME/.qlsv/secret.key`); tốt hơn plaintext nhưng vẫn có rủi ro nếu attacker có truy cập file system. Với môi trường production, nên dùng keystore hệ điều hành (Credential Manager, Keychain) hoặc HSM.
- Rate limiting hiện tính theo user (dựa trên `request.getUserPrincipal()` hoặc "anonymous") — nếu muốn giới hạn theo IP, cần đổi logic key.
- LocalCacheClient là cache cục bộ; nếu cần scale nhiều instance, cân nhắc cache phân tán (Redis).
- `ApiClient` dùng dialog modal Swing để thông báo; nếu muốn UX mượt hơn, có thể đổi sang banner/notification trong UI.

---

## 8) Các bước tiếp theo tôi có thể làm (bạn chọn)
- Triển khai lưu token an toàn bằng keystore hệ điều hành (Credential Manager / Keychain).
- Thay dialog modal bằng banner/notification trong UI.
- Viết unit/integration tests cho các luồng auth/refresh/logout và rate-limit.
- Mở rộng LocalCacheClient để dùng Redis làm cache phân tán.

---

Nếu bạn muốn tôi cập nhật phần cụ thể, hoặc chạy kiểm tra và gửi log `mvn` khi có lỗi runtime, hãy nói tên tác vụ và tôi sẽ tiếp tục thực hiện.
