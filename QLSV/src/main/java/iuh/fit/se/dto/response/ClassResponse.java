package iuh.fit.se.dto.response;

import lombok.Data;

@Data
public class ClassResponse {
    private Long id;
    private String name;
    private int courseYear;
    private Long facultyId;
    private String facultyName;
}
