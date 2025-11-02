package iuh.fit.se.dto.response;

import iuh.fit.se.model.Gender;
import iuh.fit.se.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String email;
    private String className;
    private Long classId;
    private String facultyName;
    private Long facultyId;

    public StudentResponse(Student student) {
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
        if (student.getFaculty() != null) {
            this.facultyName = student.getFaculty().getName();
            this.facultyId = student.getFaculty().getId();
        }
    }
}
