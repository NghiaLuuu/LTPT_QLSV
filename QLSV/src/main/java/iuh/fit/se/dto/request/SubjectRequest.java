package iuh.fit.se.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "Mã môn học không được để trống")
    private String code;

    @NotBlank(message = "Tên môn học không được để trống")
    private String name;

    @NotNull(message = "Số tín chỉ không được để trống")
    private Integer credit;

    private Integer maxStudents;

    private Long lecturerId;

    // Danh sách ID sinh viên để thêm vào môn học
    private List<Long> studentIds;
}