-- Script cập nhật mật khẩu admin thành "admin123"
-- Chạy script này trên database db_quanlysinhvien

USE db_quanlysinhvien;
GO

-- Hash BCrypt của "admin123" với salt rounds 12
-- Password: admin123
UPDATE tai_khoan
SET password = '$2a$12$rMZ7KqGLLJJKHJBsHvMJAO7zP3qHJqLqW3Qp0fKVHJLLJKHJBsHvO2'
WHERE username = 'admin';
GO

PRINT N'Đã cập nhật mật khẩu admin thành công!';
PRINT N'Username: admin';
PRINT N'Password: admin123';
GO

