package iuh.fit.se.dto.response;

import iuh.fit.se.model.Enrollment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private Long subjectId;
    private String subjectName;
    private Integer credits;

    private String semester;
    private Double grade;

    private String lecturerName;

    public EnrollmentResponse(Enrollment e) {
        if (e == null) return;

        this.id = e.getId();
        this.semester = e.getSemester();
        this.grade = e.getGrade();

        if (e.getSubject() != null) {
            this.subjectId = e.getSubject().getId();
            this.subjectName = e.getSubject().getName();
            this.credits = e.getSubject().getCredit();
        }

        // 🔹 Lecturer lấy từ Enrollment, nếu null lấy từ Subject
        if (e.getLecturer() != null) {
            this.lecturerName = e.getLecturer().getFullName();
        } else if (e.getSubject() != null && e.getSubject().getLecturer() != null) {
            this.lecturerName = e.getSubject().getLecturer().getFullName();
        } else {
            this.lecturerName = "Chưa có";
        }
    }
}
