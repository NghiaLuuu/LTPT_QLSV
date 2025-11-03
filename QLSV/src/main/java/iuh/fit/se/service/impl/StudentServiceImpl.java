package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.StudentRequest;
import iuh.fit.se.dto.response.StudentResponse;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Class;
import iuh.fit.se.model.Faculty;
import iuh.fit.se.model.Notification;
import iuh.fit.se.model.Student;
import iuh.fit.se.model.User;
import iuh.fit.se.model.Role;
import iuh.fit.se.repository.ClassRepository;
import iuh.fit.se.repository.FacultyRepository;
import iuh.fit.se.repository.NotificationRepository;
import iuh.fit.se.repository.StudentRepository;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.StudentService;
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        // Tự động sinh mã nếu không có
        String studentCode = request.getStudentCode();
        if (studentCode == null || studentCode.isEmpty()) {
            studentCode = generateStudentCode();
        }

        if (studentRepository.existsByStudentCode(studentCode)) {
            throw new ConflictException("Mã sinh viên đã tồn tại");
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        // Create user account for student with default password
        User user = new User();
        user.setUsername(studentCode);
        user.setPassword(passwordEncoder.encode("12345678")); // Default password
        user.setRole(Role.STUDENT);
        user.setActive(true);

        Student student = new Student();
        student.setStudentCode(studentCode);
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setDob(request.getDob());
        student.setEmail(request.getEmail());
        student.setUser(user);

        if (request.getClassId() != null) {
            Class studentClass = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));
            student.setStudentClass(studentClass);
        }

        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
            student.setFaculty(faculty);
        }

        Student savedStudent = studentRepository.save(student);

        // Gửi thông báo realtime
        Notification notification = new Notification();
        notification.setTitle("Sinh viên mới");
        notification.setMessage("Sinh viên " + savedStudent.getFullName() + " đã được thêm vào hệ thống");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/students", new StudentResponse(savedStudent));

        return new StudentResponse(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        // Kiểm tra trùng mã sinh viên
        if (!student.getStudentCode().equals(request.getStudentCode()) &&
                studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new ConflictException("Mã sinh viên đã tồn tại");
        }

        // Kiểm tra trùng email
        if (!student.getEmail().equals(request.getEmail()) &&
                studentRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        student.setStudentCode(request.getStudentCode());
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setDob(request.getDob());
        student.setEmail(request.getEmail());

        if (request.getClassId() != null) {
            Class studentClass = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));
            student.setStudentClass(studentClass);
        }

        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
            student.setFaculty(faculty);
        }

        Student updatedStudent = studentRepository.save(student);
        StudentResponse response = new StudentResponse(updatedStudent);

        // Xóa cache khi cập nhật
        localCacheClient.evict("students:all");
        localCacheClient.evict("student:dashboard:" + updatedStudent.getStudentCode());
        localCacheClient.evict("student:id:" + updatedStudent.getId());

        // 🔥 REAL-TIME: Gửi vào group chung cho tất cả admin và students
        messagingTemplate.convertAndSend("/topic/students/updates", response);

        System.out.println("📢 [WEBSOCKET - REAL-TIME] Broadcast cập nhật sinh viên đến group chung");
        System.out.println("   ├─ Mã SV: " + updatedStudent.getStudentCode());
        System.out.println("   ├─ Họ tên: " + updatedStudent.getFullName());
        System.out.println("   ├─ Email: " + updatedStudent.getEmail());
        System.out.println("   └─ Topic: /topic/students/updates");

        return response;
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        // Delete associated user account
        if (student.getUser() != null) {
            userRepository.delete(student.getUser());
        }

        studentRepository.delete(student);

        // Evict caches
        localCacheClient.evict("students:all");
        localCacheClient.evict("student:id:" + id);
        localCacheClient.evict("student:dashboard:" + student.getStudentCode());
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        String key = "student:id:" + id;
        return localCacheClient.getOrLoad(key, StudentResponse.class, () -> {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));
            return new StudentResponse(student);
        });
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        String key = "students:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<StudentResponse>>() {}, () ->
                studentRepository.findAll().stream()
                        .map(StudentResponse::new)
                        .collect(Collectors.toList())
        );
    }

    public StudentDashboardResponse getStudentDashboard(String studentCode) {
        String key = "student:dashboard:" + studentCode;
        return localCacheClient.getOrLoad(key, StudentDashboardResponse.class, () -> {
            Student student = studentRepository.findByStudentCode(studentCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));
            return new StudentDashboardResponse(student);
        });
    }

    @Transactional
    public void resetPassword(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        if (student.getUser() != null) {
            student.getUser().setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(student.getUser());
        } else {
            throw new ResourceNotFoundException("Tài khoản sinh viên không tồn tại");
        }
    }

    private String generateStudentCode() {
        long count = studentRepository.count();
        return String.format("SV%08d", count + 1);
    }
}
