package iuh.fit.se.service;

import iuh.fit.se.dto.request.StudentRequest;
import iuh.fit.se.dto.response.StudentResponse;
import iuh.fit.se.dto.response.StudentDashboardResponse;

import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
    StudentResponse getStudentById(Long id);
    List<StudentResponse> getAllStudents();
    StudentDashboardResponse getStudentDashboard(String studentCode);
    void resetPassword(Long studentId);
}
