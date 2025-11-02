package iuh.fit.se.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    @NotNull(message = "Student ID không được để trống")
    private Long studentId;

    @NotNull(message = "Subject ID không được để trống")
    private Long subjectId;

    @NotBlank(message = "Học kỳ không được để trống")
    private String semester;

    private Double grade;
}

