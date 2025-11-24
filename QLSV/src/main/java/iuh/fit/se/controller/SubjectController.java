package iuh.fit.se.controller;

import iuh.fit.se.dto.request.SubjectRequest;
import iuh.fit.se.dto.response.MessageResponse;
import iuh.fit.se.model.Subject;
import iuh.fit.se.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubject(@Valid @RequestBody SubjectRequest request) {
        Subject response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubject(@PathVariable Long id,
                                           @Valid @RequestBody SubjectRequest request) {
        Subject response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(new MessageResponse("Xóa môn học thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        Subject response = subjectService.getSubjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getAllSubjects() {
        List<Subject> response = subjectService.getAllSubjects();
        return ResponseEntity.ok(response);
    }
}
