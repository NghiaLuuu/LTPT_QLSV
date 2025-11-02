package iuh.fit.se.dto.response;

import iuh.fit.se.model.Gender;
import iuh.fit.se.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String email;
    private String className;
    private Long classId;
    private List<EnrollmentInfo> enrollments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentInfo {
        private Long id;
        private String subjectCode;
        private String subjectName;
        private Integer credit;
        private String semester;
        private Double grade;
        private String lecturerName;
    }

    public StudentDashboardResponse(Student student) {
        this.id = student.getId();
        this.studentCode = student.getStudentCode();
        this.fullName = student.getFullName();
        this.gender = student.getGender();
        this.dob = student.getDob();
        this.email = student.getEmail();
        if (student.getStudentClass() != null) {
            this.className = student.getStudentClass().getName();
            this.classId = student.getStudentClass().getId();
        }
        if (student.getEnrollments() != null) {
            this.enrollments = student.getEnrollments().stream()
                    .map(enrollment -> new EnrollmentInfo(
                            enrollment.getId(),
                            enrollment.getSubject().getCode(),
                            enrollment.getSubject().getName(),
                            enrollment.getSubject().getCredit(),
                            enrollment.getSemester(),
                            enrollment.getGrade(),
                            enrollment.getSubject().getLecturer() != null ?
                                enrollment.getSubject().getLecturer().getFullName() : null
                    ))
                    .collect(Collectors.toList());
        }
    }
}
