package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.SubjectRequest;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Enrollment;
import iuh.fit.se.model.Subject;
import iuh.fit.se.model.Lecturer;
import iuh.fit.se.model.Student;
import iuh.fit.se.repository.EnrollmentRepository;
import iuh.fit.se.repository.SubjectRepository;
import iuh.fit.se.repository.LecturerRepository;
import iuh.fit.se.repository.StudentRepository;
import iuh.fit.se.service.SubjectService;
import iuh.fit.se.service.StudentService;
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StudentService studentService;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Override
    @Transactional
    public Subject createSubject(SubjectRequest request) {
        // Tự động sinh mã môn học nếu không có
        String subjectCode = request.getCode();
        if (subjectCode == null || subjectCode.isEmpty()) {
            subjectCode = generateSubjectCode();
        }

        if (subjectRepository.existsByCode(subjectCode)) {
            throw new ConflictException("Mã môn học đã tồn tại");
        }

        Subject subject = new Subject();
        subject.setCode(subjectCode);
        subject.setName(request.getName());
        subject.setCredit(request.getCredit());
        subject.setMaxStudents(request.getMaxStudents());

        if (request.getLecturerId() != null) {
            Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại"));
            subject.setLecturer(lecturer);
        }

        Subject saved = subjectRepository.save(subject);

        // Evict subject list cache
        localCacheClient.evict("subjects:all");

        return saved;
    }

    @Override
    @Transactional
    public Subject updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"));

        if (!subject.getCode().equals(request.getCode()) &&
                subjectRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã môn học đã tồn tại");
        }

        subject.setCode(request.getCode());
        subject.setName(request.getName());
        subject.setCredit(request.getCredit());
        subject.setMaxStudents(request.getMaxStudents());

        if (request.getLecturerId() != null) {
            Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại"));
            subject.setLecturer(lecturer);
        } else {
            subject.setLecturer(null);
        }

        Subject updatedSubject = subjectRepository.save(subject);

        // Evict caches
        localCacheClient.evict("subjects:all");
        localCacheClient.evict("subject:id:" + id);

        // Xử lý thêm sinh viên vào môn học
        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            String currentSemester = getCurrentSemester();

            for (Long studentId : request.getStudentIds()) {
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với ID: " + studentId));

                // Kiểm tra xem sinh viên đã đăng ký môn học này chưa
                boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndSubjectId(studentId, id);

                if (!alreadyEnrolled) {
                    // Tạo enrollment mới
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setSubject(updatedSubject);
                    enrollment.setSemester(currentSemester);
                    enrollment.setGrade(null);
                    enrollmentRepository.save(enrollment);

                    // Gửi WebSocket notification cho sinh viên cụ thể
                    String studentUsername = student.getUser() != null ?
                            student.getUser().getUsername() : student.getStudentCode();

                    try {
                        StudentDashboardResponse dashboardData = studentService.getStudentDashboard(student.getStudentCode());
                        messagingTemplate.convertAndSend("/topic/student/" + studentUsername + "/enrollments", dashboardData);
                        System.out.println("📢 [WEBSOCKET] Đã gửi thông báo cập nhật môn học cho sinh viên: " + student.getStudentCode());
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi gửi WebSocket cho sinh viên " + student.getStudentCode() + ": " + e.getMessage());
                    }
                }
            }
        }

        return updatedSubject;
    }

    @Override
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"));
        subjectRepository.delete(subject);

        // Evict caches
        localCacheClient.evict("subjects:all");
        localCacheClient.evict("subject:id:" + id);
    }

    @Override
    public Subject getSubjectById(Long id) {
        String key = "subject:id:" + id;
        return localCacheClient.getOrLoad(key, Subject.class, () ->
                subjectRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"))
        );
    }

    @Override
    public List<Subject> getAllSubjects() {
        String key = "subjects:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<Subject>>() {}, () ->
                subjectRepository.findAll()
        );
    }

    private String generateSubjectCode() {
        long count = subjectRepository.count();
        return String.format("MH%08d", count + 1);
    }

    private String getCurrentSemester() {
        // Logic để lấy học kỳ hiện tại (ví dụ: HK1-2024, HK2-2024)
        int year = java.time.Year.now().getValue();
        int month = java.time.LocalDate.now().getMonthValue();

        if (month >= 9 || month <= 1) {
            return "HK1-" + year;
        } else if (month >= 2 && month <= 6) {
            return "HK2-" + year;
        } else {
            return "HKH-" + year; // Học kỳ hè
        }
    }
}
