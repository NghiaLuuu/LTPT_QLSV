package iuh.fit.se.dto.response;

import iuh.fit.se.model.Student;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class StudentDashboardResponse {

    private Long id;
    private String studentCode;
    private String fullName;
    private String gender;
    private String dob;
    private String email;

    private Long classId;
    private String className;

    private List<EnrollmentResponse> enrollments;

    public StudentDashboardResponse(Student s) {
        if (s == null) return;

        this.id = s.getId();
        this.studentCode = s.getStudentCode();
        this.fullName = s.getFullName();
        this.gender = s.getGender() != null ? s.getGender().name() : null;
        this.dob = s.getDob() != null ? s.getDob().toString() : null;
        this.email = s.getEmail();

        if (s.getStudentClass() != null) {
            this.classId = s.getStudentClass().getId();
            this.className = s.getStudentClass().getName();
        }

        if (s.getEnrollments() != null) {
            this.enrollments = s.getEnrollments()
                    .stream()
                    .map(EnrollmentResponse::new)
                    .collect(Collectors.toList());
        }
    }
}
