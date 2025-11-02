package iuh.fit.se.dto.response;

import iuh.fit.se.model.Faculty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer classCount;

    public FacultyResponse(Faculty faculty) {
        this.id = faculty.getId();
        this.code = faculty.getCode();
        this.name = faculty.getName();
        this.description = faculty.getDescription();
        this.classCount = faculty.getClasses() != null ? faculty.getClasses().size() : 0;
    }
}

