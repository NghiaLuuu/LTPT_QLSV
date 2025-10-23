-- Script tạo dữ liệu mẫu cho hệ thống Quản lý Sinh viên
-- Chạy script này sau khi JPA đã tạo các bảng

USE db_quanlysinhvien;
GO

-- 1. Thêm dữ liệu Khoa
INSERT INTO khoa (ma_khoa, ten_khoa) VALUES
('CNTT', N'Công nghệ Thông tin'),
('KT', N'Kinh tế'),
('DTVT', N'Điện tử - Viễn thông'),
('SPKT', N'Sư phạm Kỹ thuật');
GO

-- 2. Thêm dữ liệu Lớp học hành chính
INSERT INTO lop_hoc (ma_lop, ten_lop, nien_khoa, ma_khoa) VALUES
('D20CNPM01', N'Đại học CNTT K20 - Lớp 1', '2020-2024', 'CNTT'),
('D20CNPM02', N'Đại học CNTT K20 - Lớp 2', '2020-2024', 'CNTT'),
('D21CNPM01', N'Đại học CNTT K21 - Lớp 1', '2021-2025', 'CNTT'),
('D20KT01', N'Đại học Kinh tế K20 - Lớp 1', '2020-2024', 'KT');
GO

-- 3. Thêm dữ liệu Giảng viên
INSERT INTO giang_vien (ma_gv, ho_ten, hoc_vi, ma_khoa) VALUES
('GV001', N'Nguyễn Văn A', N'Tiến sĩ', 'CNTT'),
('GV002', N'Trần Thị B', N'Thạc sĩ', 'CNTT'),
('GV003', N'Lê Văn C', N'Tiến sĩ', 'CNTT'),
('GV004', N'Phạm Thị D', N'Thạc sĩ', 'KT');
GO

-- 4. Thêm dữ liệu Môn học
INSERT INTO mon_hoc (ma_mh, ten_mh, so_tin_chi, ma_khoa) VALUES
('IT001', N'Lập trình Java', 4, 'CNTT'),
('IT002', N'Cơ sở dữ liệu', 3, 'CNTT'),
('IT003', N'Lập trình phân tán', 3, 'CNTT'),
('IT004', N'Phát triển ứng dụng Web', 4, 'CNTT'),
('KT001', N'Kinh tế vi mô', 3, 'KT');
GO

-- 5. Thêm dữ liệu Học kỳ
INSERT INTO hoc_ky (ma_hoc_ky, ten_hoc_ky, ngay_bat_dau, ngay_ket_thuc) VALUES
('HK1_2024_2025', N'Học kỳ 1 năm 2024-2025', '2024-09-01', '2025-01-15'),
('HK2_2024_2025', N'Học kỳ 2 năm 2024-2025', '2025-01-20', '2025-06-30'),
('HK1_2023_2024', N'Học kỳ 1 năm 2023-2024', '2023-09-01', '2024-01-15');
GO

-- 6. Thêm dữ liệu Sinh viên
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, chuyen_nganh, ma_lop, diem_tb) VALUES
('20001001', N'Nguyễn Minh Tuấn', '2002-05-15', 'NAM', N'Công nghệ Phần mềm', 'D20CNPM01', 7.5),
('20001002', N'Trần Thu Hương', '2002-08-20', 'NU', N'Công nghệ Phần mềm', 'D20CNPM01', 8.2),
('20001003', N'Lê Hoàng Nam', '2002-03-10', 'NAM', N'Công nghệ Phần mềm', 'D20CNPM01', 7.8),
('20001004', N'Phạm Lan Anh', '2002-11-25', 'NU', N'Công nghệ Phần mềm', 'D20CNPM02', 8.5),
('21001001', N'Võ Minh Quân', '2003-07-08', 'NAM', N'Công nghệ Phần mềm', 'D21CNPM01', 7.2);
GO

-- 7. Thêm Tài khoản (mật khẩu mặc định đã hash: "123456")
-- Hash BCrypt của "123456" với salt rounds 12
INSERT INTO tai_khoan (username, password, role, ma_sv, ma_gv) VALUES
-- Admin account
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIHBMYo1Em', 'ADMIN', NULL, NULL),

-- Giảng viên accounts
('gv001', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIHBMYo1Em', 'GIANG_VIEN', NULL, 'GV001'),
('gv002', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIHBMYo1Em', 'GIANG_VIEN', NULL, 'GV002'),

-- Sinh viên accounts
('20001001', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIHBMYo1Em', 'SINH_VIEN', '20001001', NULL),
('20001002', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIHBMYo1Em', 'SINH_VIEN', '20001002', NULL);
GO

-- 8. Thêm dữ liệu Lớp học phần
INSERT INTO lop_hoc_phan (ma_mh, ma_gv, ma_hoc_ky, so_luong_toi_da) VALUES
('IT001', 'GV001', 'HK1_2024_2025', 40),
('IT002', 'GV002', 'HK1_2024_2025', 35),
('IT003', 'GV003', 'HK1_2024_2025', 30),
('IT001', 'GV001', 'HK2_2024_2025', 40);
GO

-- 9. In ra thông báo hoàn thành
PRINT N'Đã tạo dữ liệu mẫu thành công!';
PRINT N'';
PRINT N'=== THÔNG TIN ĐĂNG NHẬP MẶC ĐỊNH ===';
PRINT N'Admin:';
PRINT N'  Username: admin';
PRINT N'  Password: 123456';
PRINT N'';
PRINT N'Giảng viên:';
PRINT N'  Username: gv001 | Password: 123456';
PRINT N'  Username: gv002 | Password: 123456';
PRINT N'';
PRINT N'Sinh viên:';
PRINT N'  Username: 20001001 | Password: 123456';
PRINT N'  Username: 20001002 | Password: 123456';
GO

