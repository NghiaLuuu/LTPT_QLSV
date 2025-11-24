package iuh.fit.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDTO {
    private Long id;
    private String code;
    private String name;
    private Integer courseYear;
    private FacultyDto faculty;
    private List<StudentResponseDTO> students; // chỉ chứa DTO, không entity
}
