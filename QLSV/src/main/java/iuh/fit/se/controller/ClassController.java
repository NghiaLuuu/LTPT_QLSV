package iuh.fit.se.controller;

import iuh.fit.se.model.Class;
import iuh.fit.se.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public ResponseEntity<List<Class>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @PostMapping
    public ResponseEntity<Class> createClass(@RequestBody ClassRequestWrapper request) {
        Class clazz = new Class();
        clazz.setName(request.getName());
        clazz.setCourseYear(request.getCourseYear());
        clazz.setCode(classService.generateCode()); // <- sinh code ngay đây

        return ResponseEntity.ok(classService.createClass(clazz, request.getFacultyId()));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Class> updateClass(@PathVariable Long id, @RequestBody ClassRequestWrapper request) {
        Class clazz = new Class();
        clazz.setName(request.getName());
        clazz.setCourseYear(request.getCourseYear());

        return ResponseEntity.ok(classService.updateClass(id, clazz, request.getFacultyId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    // Wrapper để nhận JSON có facultyId
    public static class ClassRequestWrapper {
        private String name;
        private int courseYear;
        private Long facultyId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getCourseYear() { return courseYear; }
        public void setCourseYear(int courseYear) { this.courseYear = courseYear; }
        public Long getFacultyId() { return facultyId; }
        public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }
    }
}
