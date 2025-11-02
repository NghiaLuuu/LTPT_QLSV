package iuh.fit.se.controller;

import iuh.fit.se.dto.request.ClassRequest;
import iuh.fit.se.dto.response.MessageResponse;
import iuh.fit.se.model.Class;
import iuh.fit.se.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassController {

    @Autowired
    private ClassService classService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@Valid @RequestBody ClassRequest request) {
        Class response = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClass(@PathVariable Long id,
                                        @Valid @RequestBody ClassRequest request) {
        Class response = classService.updateClass(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(new MessageResponse("Xóa lớp thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        Class response = classService.getClassById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<?> getAllClasses() {
        List<Class> response = classService.getAllClasses();
        return ResponseEntity.ok(response);
    }
}

