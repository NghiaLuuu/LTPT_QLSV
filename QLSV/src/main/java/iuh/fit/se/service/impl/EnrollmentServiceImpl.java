package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.EnrollmentRequest;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Enrollment;
import iuh.fit.se.model.Student;
import iuh.fit.se.model.Subject;
import iuh.fit.se.repository.EnrollmentRepository;
import iuh.fit.se.repository.StudentRepository;
import iuh.fit.se.repository.SubjectRepository;
import iuh.fit.se.service.EnrollmentService;
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Override
    @Transactional
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

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Evict caches
        localCacheClient.evict("enrollments:all");
        localCacheClient.evict("enrollments:student:" + request.getStudentId());
        localCacheClient.evict("enrollment:id:" + savedEnrollment.getId());
        localCacheClient.evict("student:dashboard:" + student.getStudentCode());

        // 🔥 EVENT-DRIVEN: Gửi WebSocket event đến sinh viên khi được thêm vào môn học
        String studentUsername = student.getStudentCode();

        // Reload student với enrollments để gửi đầy đủ thông tin
        Student reloadedStudent = studentRepository.findByIdWithEnrollments(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        StudentDashboardResponse dashboardData = new StudentDashboardResponse(reloadedStudent);

        // Gửi event đến topic cá nhân của sinh viên
        messagingTemplate.convertAndSend("/topic/student/" + studentUsername + "/enrollments", dashboardData);

        System.out.println("📢 [EVENT-DRIVEN] Đã gửi event thêm môn học đến sinh viên");
        System.out.println("   ├─ Sinh viên: " + studentUsername);
        System.out.println("   ├─ Môn học: " + subject.getName() + " (" + subject.getCode() + ")");
        System.out.println("   ├─ Học kỳ: " + request.getSemester());
        System.out.println("   └─ Topic: /topic/student/" + studentUsername + "/enrollments");

        return savedEnrollment;
    }

    @Override
    @Transactional
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

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        // Evict caches
        localCacheClient.evict("enrollments:all");
        localCacheClient.evict("enrollments:student:" + request.getStudentId());
        localCacheClient.evict("enrollment:id:" + id);
        localCacheClient.evict("student:dashboard:" + student.getStudentCode());

        // 🔥 EVENT-DRIVEN: Gửi WebSocket event khi cập nhật enrollment (thường là cập nhật điểm)
        String studentUsername = student.getStudentCode();

        // Reload student với enrollments để gửi đầy đủ thông tin
        Student reloadedStudent = studentRepository.findByIdWithEnrollments(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        StudentDashboardResponse dashboardData = new StudentDashboardResponse(reloadedStudent);

        // Gửi event đến topic cá nhân của sinh viên
        messagingTemplate.convertAndSend("/topic/student/" + studentUsername + "/enrollments", dashboardData);

        System.out.println("📢 [EVENT-DRIVEN] Đã gửi event cập nhật enrollment đến sinh viên");
        System.out.println("   ├─ Sinh viên: " + studentUsername);
        System.out.println("   ├─ Môn học: " + subject.getName() + " (" + subject.getCode() + ")");
        System.out.println("   ├─ Điểm mới: " + (request.getGrade() != null ? request.getGrade() : "Chưa có"));
        System.out.println("   └─ Topic: /topic/student/" + studentUsername + "/enrollments");

        return updatedEnrollment;
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký không tồn tại"));

        Student student = enrollment.getStudent();
        String studentUsername = student.getStudentCode();
        String subjectName = enrollment.getSubject().getName();
        Long studentId = student.getId();

        enrollmentRepository.delete(enrollment);

        // Evict caches
        localCacheClient.evict("enrollments:all");
        localCacheClient.evict("enrollments:student:" + studentId);
        localCacheClient.evict("enrollment:id:" + id);
        localCacheClient.evict("student:dashboard:" + studentUsername);

        // 🔥 EVENT-DRIVEN: Gửi WebSocket event khi xóa enrollment
        // Reload student với enrollments mới sau khi xóa
        Student reloadedStudent = studentRepository.findByIdWithEnrollments(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        StudentDashboardResponse dashboardData = new StudentDashboardResponse(reloadedStudent);

        // Gửi event đến topic cá nhân của sinh viên
        messagingTemplate.convertAndSend("/topic/student/" + studentUsername + "/enrollments", dashboardData);

        System.out.println("📢 [EVENT-DRIVEN] Đã gửi event xóa enrollment đến sinh viên");
        System.out.println("   ├─ Sinh viên: " + studentUsername);
        System.out.println("   ├─ Môn học đã xóa: " + subjectName);
        System.out.println("   └─ Topic: /topic/student/" + studentUsername + "/enrollments");
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        String key = "enrollment:id:" + id;
        return localCacheClient.getOrLoad(key, Enrollment.class, () ->
                enrollmentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Đăng ký không tồn tại"))
        );
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        String key = "enrollments:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<Enrollment>>() {}, () ->
                enrollmentRepository.findAll()
        );
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        String key = "enrollments:student:" + studentId;
        return localCacheClient.getOrLoad(key, new TypeReference<List<Enrollment>>() {}, () ->
                enrollmentRepository.findByStudentId(studentId)
        );
    }
}
