package iuh.fit.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String studentCode;
    private String fullName;
    private String gender;
    private String dob;
    private Long classId;       // id lớp
    private String className;   // tên lớp
    private Long facultyId;     // id khoa
    private String facultyName; // tên khoa
    private String email;
    private String name;
}


