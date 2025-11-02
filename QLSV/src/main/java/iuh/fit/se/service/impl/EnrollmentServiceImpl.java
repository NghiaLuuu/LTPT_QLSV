package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.EnrollmentRequest;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Enrollment;
import iuh.fit.se.model.Student;
import iuh.fit.se.model.Subject;
import iuh.fit.se.repository.EnrollmentRepository;
import iuh.fit.se.repository.StudentRepository;
import iuh.fit.se.repository.SubjectRepository;
import iuh.fit.se.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Enrollment createEnrollment(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollment.setSemester(request.getSemester());
        enrollment.setGrade(request.getGrade());

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment updateEnrollment(Long id, EnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký không tồn tại"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"));

        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollment.setSemester(request.getSemester());
        enrollment.setGrade(request.getGrade());

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký không tồn tại"));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký không tồn tại"));
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}
