package iuh.fit.se.controller;

import iuh.fit.se.dto.request.FacultyRequest;
import iuh.fit.se.dto.response.FacultyResponse;
import iuh.fit.se.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
@CrossOrigin(origins = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponse> createFaculty(@Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.createFaculty(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponse> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.updateFaculty(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable Long id) {
        FacultyResponse response = facultyService.getFacultyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    public ResponseEntity<List<FacultyResponse>> getAllFaculties() {
        List<FacultyResponse> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }
}
