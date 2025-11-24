package iuh.fit.se.controller;

import iuh.fit.se.dto.request.LecturerRequest;
import iuh.fit.se.dto.response.LecturerResponse;
import iuh.fit.se.dto.response.MessageResponse;
import iuh.fit.se.service.LecturerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
@CrossOrigin(origins = "*")
public class LecturerController {

    @Autowired
    private LecturerService lecturerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLecturer(@Valid @RequestBody LecturerRequest request) {
        LecturerResponse response = lecturerService.createLecturer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLecturer(@PathVariable Long id,
                                            @Valid @RequestBody LecturerRequest request) {
        LecturerResponse response = lecturerService.updateLecturer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.ok(new MessageResponse("Xóa giảng viên thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> getLecturerById(@PathVariable Long id) {
        LecturerResponse response = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> getAllLecturers() {
        List<LecturerResponse> response = lecturerService.getAllLecturers();
        return ResponseEntity.ok(response);
    }
}