package iuh.fit.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDto {
    private Long id;
    private String name;
    // Không chứa List<Class> classes
}

