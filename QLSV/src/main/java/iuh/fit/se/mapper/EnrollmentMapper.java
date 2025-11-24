package iuh.fit.se.mapper;

import iuh.fit.se.model.Enrollment;
import iuh.fit.se.dto.response.EnrollmentResponse;

public class EnrollmentMapper {

    public static EnrollmentResponse mapEnrollment(Enrollment enrollment) {
        EnrollmentResponse dto = new EnrollmentResponse();
        dto.setSubjectId(enrollment.getSubject().getId());
        dto.setSubjectName(enrollment.getSubject().getName());
        dto.setCredits(enrollment.getSubject().getCredit());
        dto.setSemester(enrollment.getSemester());
        dto.setGrade(enrollment.getGrade());
        dto.setLecturerName(enrollment.getLecturerNameSafe()); // tránh null
        return dto;
    }
}
