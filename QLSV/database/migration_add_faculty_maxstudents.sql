-- Migration: Thêm faculty cho student và maxStudents cho subject
-- Date: 2025-10-29

-- Thêm cột faculty_id vào bảng students
-- Thêm cột faculty_id vào bảng students
ALTER TABLE students ADD faculty_id BIGINT;
ALTER TABLE students ADD CONSTRAINT fk_students_faculty
    FOREIGN KEY (faculty_id) REFERENCES faculties(id);
select * from faculties
-- Thêm cột max_students vào bảng subjects
ALTER TABLE subjects ADD max_students INT DEFAULT 50;

-- Thêm index cho hiệu suất
CREATE INDEX idx_students_faculty ON students(faculty_id);
CREATE INDEX idx_subjects_lecturer ON subjects(lecturer_id);

-- Thêm mô tả (comment) cho cột trong SQL Server
EXEC sp_addextendedproperty 
    @name = N'MS_Description',
    @value = N'ID của khoa mà sinh viên thuộc về',
    @level0type = N'SCHEMA', @level0name = 'dbo', -- hoặc schema của bạn nếu khác
    @level1type = N'TABLE',  @level1name = 'students',
    @level2type = N'COLUMN', @level2name = 'faculty_id';

EXEC sp_addextendedproperty
    @name = N'MS_Description',
    @value = N'Số lượng sinh viên tối đa có thể đăng ký môn học này',
    @level0type = N'SCHEMA', @level0name = 'dbo', -- hoặc schema của bạn nếu khác
    @level1type = N'TABLE',  @level1name = 'subjects',
    @level2type = N'COLUMN', @level2name = 'max_students';

	select * from students

	select * from users

	update users set password ='$2a$10$ldpOtxQBCtPkYXDUpoqy0OqKrNRVORH6ZYFd8CH2iyVE5o36Oh1/y' where username = 'admin'