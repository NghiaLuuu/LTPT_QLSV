package iuh.fit.se.controller;

import iuh.fit.se.dto.request.EnrollmentRequest;
import iuh.fit.se.dto.response.MessageResponse;
import iuh.fit.se.model.Enrollment;
import iuh.fit.se.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<?> createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        Enrollment response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> updateEnrollment(@PathVariable Long id,
                                              @Valid @RequestBody EnrollmentRequest request) {
        Enrollment response = enrollmentService.updateEnrollment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<?> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok(new MessageResponse("Xóa đăng ký môn học thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getEnrollmentById(@PathVariable Long id) {
        Enrollment response = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> getAllEnrollments() {
        List<Enrollment> response = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<Enrollment> response = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(response);
    }
}
