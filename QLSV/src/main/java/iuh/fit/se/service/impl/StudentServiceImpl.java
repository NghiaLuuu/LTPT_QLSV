package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.StudentRequest;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.dto.response.StudentResponseDTO;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Role;
import iuh.fit.se.model.Student;
import iuh.fit.se.model.User;
import iuh.fit.se.repository.ClassRepository;
import iuh.fit.se.repository.FacultyRepository;
import iuh.fit.se.repository.StudentRepository;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = "students:all")
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));
        return mapToDTO(student);
    }

    @Override
    @CacheEvict(value = "students:all", allEntries = true)
    public StudentResponseDTO createStudent(StudentRequest request) {
        // 1. Tạo và lưu Student
        Student student = new Student();
        student.setStudentCode(request.getStudentCode());
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setDob(request.getDob());
        student.setEmail(request.getEmail());
        student.setStudentClass(classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại")));
        student.setFaculty(facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại")));

        Student saved = studentRepository.save(student);

        // 2. Tạo User tương ứng với Student
        User user = new User();
        user.setUsername(String.valueOf(saved.getStudentCode())); // username = student_id
        user.setPassword(passwordEncoder.encode("12345678")); // mật khẩu mặc định
        user.setRole(Role.STUDENT); // nếu có role
        user = userRepository.save(user);

        saved.setUser(user);
        studentRepository.save(saved);

        return mapToDTO(saved);
    }

    @Override
    @CacheEvict(value = "students:all", allEntries = true)
    public StudentResponseDTO updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setDob(request.getDob());
        student.setEmail(request.getEmail());
        student.setStudentClass(classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại")));
        student.setFaculty(facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại")));

        Student updated = studentRepository.save(student);
        return mapToDTO(updated);
    }

    @Override
    @CacheEvict(value = "students:all", allEntries = true)
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));
        studentRepository.delete(student);
    }

    @Override
    @CacheEvict(value = "students:all", allEntries = true)
    public void resetPassword(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));

        if (student.getUser() == null) {
            throw new ResourceNotFoundException("Tài khoản User chưa tồn tại cho sinh viên");
        }

        student.getUser().setPassword(passwordEncoder.encode("12345678"));
        userRepository.save(student.getUser());
    }

    @Override
    public StudentDashboardResponse getStudentDashboard(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại"));
        return new StudentDashboardResponse(student);
    }

    private StudentResponseDTO mapToDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setStudentCode(student.getStudentCode());
        dto.setFullName(student.getFullName());
        dto.setGender(student.getGender() != null ? student.getGender().name() : null);
        dto.setDob(String.valueOf(student.getDob()));
        dto.setEmail(student.getEmail());
        if (student.getStudentClass() != null) {
            dto.setClassId(student.getStudentClass().getId());
            dto.setClassName(student.getStudentClass().getName());
        }
        if (student.getFaculty() != null) {
            dto.setFacultyId(student.getFaculty().getId());
            dto.setFacultyName(student.getFaculty().getName());
        }
        return dto;
    }
}
