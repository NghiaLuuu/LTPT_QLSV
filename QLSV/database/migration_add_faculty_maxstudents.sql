-- Migration: Thêm faculty cho student và maxStudents cho subject
-- Date: 2025-10-29

-- Thêm cột faculty_id vào bảng students
ALTER TABLE students ADD COLUMN faculty_id BIGINT;
ALTER TABLE students ADD CONSTRAINT fk_students_faculty
    FOREIGN KEY (faculty_id) REFERENCES faculties(id);

-- Thêm cột max_students vào bảng subjects
ALTER TABLE subjects ADD COLUMN max_students INT DEFAULT 50;

-- Thêm index cho hiệu suất
CREATE INDEX idx_students_faculty ON students(faculty_id);
CREATE INDEX idx_subjects_lecturer ON subjects(lecturer_id);

-- Comments
COMMENT ON COLUMN students.faculty_id IS 'ID của khoa mà sinh viên thuộc về';
COMMENT ON COLUMN subjects.max_students IS 'Số lượng sinh viên tối đa có thể đăng ký môn học này';

