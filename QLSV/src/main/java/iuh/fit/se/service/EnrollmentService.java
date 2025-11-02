package iuh.fit.se.service;

import iuh.fit.se.dto.request.EnrollmentRequest;
import iuh.fit.se.model.Enrollment;

import java.util.List;

public interface EnrollmentService {
    Enrollment createEnrollment(EnrollmentRequest request);
    Enrollment updateEnrollment(Long id, EnrollmentRequest request);
    void deleteEnrollment(Long id);
    Enrollment getEnrollmentById(Long id);
    List<Enrollment> getAllEnrollments();
    List<Enrollment> getEnrollmentsByStudentId(Long studentId);
}

