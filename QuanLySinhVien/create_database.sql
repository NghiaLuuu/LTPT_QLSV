-- Tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'db_quanlysinhvien')
BEGIN
    CREATE DATABASE db_quanlysinhvien;
    PRINT 'Database db_quanlysinhvien created successfully';
END
ELSE
BEGIN
    PRINT 'Database db_quanlysinhvien already exists';
END
GO

USE db_quanlysinhvien;
GO

PRINT 'Database is ready!';
GO

