-- Migration: add refresh_token column to users table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'refresh_token' AND Object_ID = Object_ID(N'users'))
BEGIN
    ALTER TABLE users ADD refresh_token NVARCHAR(255) NULL;
END
GO

update users 
set password = '$2a$10$IbNzYbXoDyuun3cjojSB.efL.GoQfsJ419KtzqDbnAem3h1uV7qmq' where username = 'admin'

select * from users

select * from students

select * from enrollments

delete from students where id = 4
go
delete from users where id = 2

-- Tạm tắt ràng buộc (chỉ dùng trong trường hợp khẩn cấp)
ALTER TABLE dbo.enrollments NOCHECK CONSTRAINT FK_enrollments_students;

-- Thực hiện xóa
DELETE FROM dbo.students WHERE id = 4;

-- Bật lại ràng buộc
ALTER TABLE dbo.enrollments CHECK CONSTRAINT FK_enrollments_students;