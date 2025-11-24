-- =============================================
-- SCRIPT KHỞI TẠO DATABASE HỆ THỐNG QUẢN LÝ SINH VIÊN
-- =============================================

-- Tạo database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'QLSV_DB')
BEGIN
    CREATE DATABASE QLSV_DB;
END
GO

USE QLSV_DB;
GO

-- =============================================
-- Bảng USERS - Quản lý tài khoản người dùng
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        username NVARCHAR(50) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        role NVARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'LECTURER', 'STUDENT')),
        active BIT NOT NULL DEFAULT 1,
        CONSTRAINT CHK_users_role CHECK (role IN ('ADMIN', 'LECTURER', 'STUDENT'))
    );
END
GO

-- =============================================
-- Bảng CLASSES - Quản lý lớp học
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'classes')
BEGIN
    CREATE TABLE classes (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        name NVARCHAR(50) NOT NULL UNIQUE,
        faculty NVARCHAR(100) NOT NULL,
        course_year INT NOT NULL
    );
END
GO

-- =============================================
-- Bảng STUDENTS - Quản lý sinh viên
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'students')
BEGIN
    CREATE TABLE students (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        student_code NVARCHAR(20) NOT NULL UNIQUE,
        full_name NVARCHAR(100) NOT NULL,
        gender NVARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
        dob DATE NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        class_id BIGINT,
        CONSTRAINT FK_students_classes FOREIGN KEY (class_id) REFERENCES classes(id)
    );
END
GO

-- =============================================
-- Bảng SUBJECTS - Quản lý môn học
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'subjects')
BEGIN
    CREATE TABLE subjects (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        code NVARCHAR(20) NOT NULL UNIQUE,
        name NVARCHAR(100) NOT NULL,
        credit INT NOT NULL
    );
END
GO

-- =============================================
-- Bảng ENROLLMENTS - Quản lý đăng ký môn học
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'enrollments')
BEGIN
    CREATE TABLE enrollments (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        student_id BIGINT NOT NULL,
        subject_id BIGINT NOT NULL,
        semester NVARCHAR(20) NOT NULL,
        grade FLOAT,
        CONSTRAINT FK_enrollments_students FOREIGN KEY (student_id) REFERENCES students(id),
        CONSTRAINT FK_enrollments_subjects FOREIGN KEY (subject_id) REFERENCES subjects(id)
    );
END
GO

-- =============================================
-- Bảng NOTIFICATIONS - Quản lý thông báo
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'notifications')
BEGIN
    CREATE TABLE notifications (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        title NVARCHAR(255) NOT NULL,
        message NVARCHAR(MAX) NOT NULL,
        created_at DATETIME NOT NULL DEFAULT GETDATE()
    );
END
GO

-- =============================================
-- Bảng LOGS - Ghi nhận hoạt động hệ thống
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'logs')
BEGIN
    CREATE TABLE logs (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        user_id BIGINT NOT NULL,
        action NVARCHAR(255) NOT NULL,
        timestamp DATETIME NOT NULL DEFAULT GETDATE()
    );
END
GO

-- =============================================
-- DỮ LIỆU MẪU
-- =============================================

-- Thêm user admin mặc định (password: admin123)
-- Lưu ý: Password sẽ được mã hóa bởi BCrypt khi đăng ký qua API
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, password, role, active)
    VALUES ('admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ADMIN', 1);
    -- Password: admin123
END
GO

-- Thêm lớp học mẫu
IF NOT EXISTS (SELECT * FROM classes)
BEGIN
    INSERT INTO classes (name, faculty, course_year) VALUES
    (N'DHKTPM17A', N'Công nghệ Thông tin', 2021),
    (N'DHKTPM17B', N'Công nghệ Thông tin', 2021),
    (N'DHKTPM18A', N'Công nghệ Thông tin', 2022);
END
GO

-- Thêm môn học mẫu
IF NOT EXISTS (SELECT * FROM subjects)
BEGIN
    INSERT INTO subjects (code, name, credit) VALUES
    (N'LTCT', N'Lập trình căn bản', 3),
    (N'CTDL', N'Cấu trúc dữ liệu', 3),
    (N'CSDL', N'Cơ sở dữ liệu', 3),
    (N'LTW', N'Lập trình Web', 4),
    (N'LTDD', N'Lập trình di động', 3);
END
GO

-- Thêm sinh viên mẫu
IF NOT EXISTS (SELECT * FROM students)
BEGIN
    INSERT INTO students (student_code, full_name, gender, dob, email, class_id) VALUES
    (N'2021600001', N'Nguyễn Văn A', 'MALE', '2003-05-15', 'nguyenvana@student.edu.vn', 1),
    (N'2021600002', N'Trần Thị B', 'FEMALE', '2003-08-20', 'tranthib@student.edu.vn', 1),
    (N'2021600003', N'Lê Văn C', 'MALE', '2003-03-10', 'levanc@student.edu.vn', 2);
END
GO

-- Thêm đăng ký môn học mẫu
IF NOT EXISTS (SELECT * FROM enrollments)
BEGIN
    INSERT INTO enrollments (student_id, subject_id, semester, grade) VALUES
    (1, 1, 'HK1-2023', 8.5),
    (1, 2, 'HK1-2023', 7.0),
    (2, 1, 'HK1-2023', 9.0),
    (3, 3, 'HK1-2023', 8.0);
END
GO

-- =============================================
-- TẠO INDEXES ĐỂ TỐI ƯU HIỆU SUẤT
-- =============================================
CREATE INDEX idx_students_student_code ON students(student_code);
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_subjects_code ON subjects(code);
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_subject_id ON enrollments(subject_id);
GO

PRINT 'Database QLSV_DB đã được khởi tạo thành công!';
GO

