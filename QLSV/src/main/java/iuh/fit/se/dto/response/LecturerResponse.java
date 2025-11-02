package iuh.fit.se.dto.response;

import iuh.fit.se.model.Lecturer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerResponse {

    private Long id;
    private String lecturerCode;
    private String fullName;
    private String email;
    private String phone;
    private String faculty;
    private String degree;
    private LocalDate dob;
    private String gender;

    // Constructor từ Lecturer entity
    public LecturerResponse(Lecturer lecturer) {
        this.id = lecturer.getId();
        this.lecturerCode = lecturer.getLecturerCode();
        this.fullName = lecturer.getFullName();
        this.email = lecturer.getEmail();
        this.phone = lecturer.getPhone();
        this.faculty = lecturer.getFaculty();
        this.degree = lecturer.getDegree();
        this.dob = lecturer.getDob();
        this.gender = lecturer.getGender() != null ? lecturer.getGender().name() : null;
    }
}
