package iuh.fit.se.dto.request;

import iuh.fit.se.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerRequest {

    private String lecturerCode;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không đư���c để trống")
    private String phone;

    @NotBlank(message = "Khoa không được để trống")
    private String faculty;

    @NotBlank(message = "Học vị không được để trống")
    private String degree;

    private LocalDate dob;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;
}
