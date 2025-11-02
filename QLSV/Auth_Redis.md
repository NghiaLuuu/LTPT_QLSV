🚀 Authentication & Redis Integration Tasks
🧩 1. Authentication System

Mục tiêu: Thêm cơ chế xác thực sử dụng Access Token và Refresh Token.

Tasks:

Tạo logic authentication (login, register, logout).

Sinh Access Token (JWT) và Refresh Token sau khi đăng nhập.

Lưu Refresh Token trong database (theo user) để có thể thu hồi hoặc kiểm tra.

Lưu Access Token trong Redis Server (key = userId, value = token).

Tạo endpoint /auth/refresh để cấp lại Access Token mới khi hết hạn.

⚙️ 2. Redis Configuration
2.1 Redis Server

Mục tiêu: Dùng Redis Server để lưu Access Token và các giới hạn truy cập toàn hệ thống.

Tasks:

Cấu hình Redis Server (RedisTemplate / Lettuce / Jedis).

Lưu accessToken theo userId tại Redis Server.

Lưu số lần truy cập API của từng user tại Redis Server (dùng để giới hạn truy cập toàn server).

Giới hạn người dùng chỉ được thao tác tối đa 3 lần trên toàn server trong mỗi phiên.

2.2 Redis Client (Local Cache)

Mục tiêu: Dùng Redis Client (cache nội bộ của app/service) để tránh truy vấn DB nhiều lần.

Tasks:

Cấu hình Redis Client (cache cục bộ của ứng dụng).

Mọi dữ liệu được gọi lần đầu từ DB → lưu vào Redis Client.

Nếu dữ liệu đã có trong Redis Client → lấy từ cache, không truy vấn DB.

Khi có hành động create / update / delete → phải cập nhật lại Redis Client để load dữ liệu mới.

Thêm console log hoặc logger để hiển thị:

Khi Redis Client cache dữ liệu lần đầu

Khi Redis Client lấy lại dữ liệu từ cache (lần 2 trở đi)

🔁 3. Retry & Rate Limiting

Mục tiêu: Giới hạn tần suất thao tác người dùng cả ở phía Client và Server.

Tasks:

Dùng Redis để lưu số lần thao tác của từng user:

Redis Client → đếm số lần thao tác ở phía giao diện (UI client)

Redis Server → đếm số lần request đến backend

Nếu vượt quá 3 lần thao tác → chặn request và trả về lỗi 429 Too Many Requests.

Reset lại giới hạn sau khoảng thời gian nhất định (vd: 5 phút).

Cho phép retry logic khi request bị timeout hoặc thất bại (tối đa 3 lần).
