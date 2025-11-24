package iuh.fit.se.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tạo hoặc cập nhật Enrollment
 * - lecturerId có thể null: nếu null sẽ dùng giảng viên mặc định của môn học
 */
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

    private Long lecturerId;

    private Double grade; // có thể null nếu chưa có điểm
}
