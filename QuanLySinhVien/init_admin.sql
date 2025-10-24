-- Script khởi tạo tài khoản admin mặc định
-- Sử dụng: docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "SaPassword123!" -C -d db_quanlysinhvien -i init_admin.sql

USE db_quanlysinhvien;
GO

-- Kiểm tra và tạo tài khoản admin nếu chưa tồn tại
IF NOT EXISTS (SELECT 1 FROM tai_khoan WHERE username = 'admin')
BEGIN
    -- Tài khoản admin mặc định
    -- Username: admin
    -- Password: admin123 (đã được hash bằng BCrypt với cost factor 10)
    -- BCrypt hash của "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

    INSERT INTO tai_khoan (username, password, role, ma_sv, ma_gv)
    VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NULL, NULL);

    PRINT '========================================';
    PRINT 'Tài khoản admin đã được tạo thành công!';
    PRINT '========================================';
    PRINT 'Username: admin';
    PRINT 'Password: admin123';
    PRINT 'Role: ADMIN';
    PRINT '========================================';
END
ELSE
BEGIN
    PRINT 'Tài khoản admin đã tồn tại!';
END
GO

-- Hiển thị thông tin tài khoản admin
SELECT
    username AS 'Username',
    role AS 'Role',
    ma_sv AS 'MaSV',
    ma_gv AS 'MaGV'
FROM tai_khoan
WHERE username = 'admin';
GO
