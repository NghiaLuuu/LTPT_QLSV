package iuh.fit.se.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {

    @NotBlank(message = "Tên lớp không được để trống")
    private String name;

    private Long facultyId;

    @NotNull(message = "Khóa học không được để trống")
    private Integer courseYear;

    private Long headLecturerId; // Giảng viên chủ nhiệm
}