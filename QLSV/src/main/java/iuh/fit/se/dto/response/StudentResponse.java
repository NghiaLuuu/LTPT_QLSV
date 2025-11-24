package iuh.fit.se.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iuh.fit.se.model.Gender;
import iuh.fit.se.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse implements Serializable {
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


    @JsonIgnoreProperties("studentClass") // ignore field studentClass khi serialize StudentResponse
    private List<StudentResponse> students;

    public StudentResponse(Student student) {
    }
}