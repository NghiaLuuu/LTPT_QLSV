package iuh.fit.se.service;

import iuh.fit.se.dto.request.StudentRequest;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.dto.response.StudentResponseDTO;

import java.util.List;

public interface StudentService {
    StudentResponseDTO getStudentById(Long id);
    List<StudentResponseDTO> getAllStudents();
    StudentResponseDTO createStudent(StudentRequest request);
    StudentResponseDTO updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);

    void resetPassword(Long id);

    StudentDashboardResponse getStudentDashboard(String studentCode);
}
