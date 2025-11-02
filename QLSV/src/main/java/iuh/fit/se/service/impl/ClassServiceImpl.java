package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.ClassRequest;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Class;
import iuh.fit.se.model.Faculty;
import iuh.fit.se.model.Lecturer;
import iuh.fit.se.repository.ClassRepository;
import iuh.fit.se.repository.FacultyRepository;
import iuh.fit.se.repository.LecturerRepository;
import iuh.fit.se.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Override
    public Class createClass(ClassRequest request) {
        // Tự động sinh mã lớp nếu không có
        String className = request.getName();
        if (className == null || className.isEmpty()) {
            className = generateClassCode();
        }

        if (classRepository.existsByName(className)) {
            throw new ConflictException("Tên lớp đã tồn tại");
        }

        Class clazz = new Class();
        clazz.setName(className);

        // Lấy Faculty object từ facultyId
        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
            clazz.setFaculty(faculty);
        }

        clazz.setCourseYear(request.getCourseYear());

        return classRepository.save(clazz);
    }

    @Override
    public Class updateClass(Long id, ClassRequest request) {
        Class clazz = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));

        if (!clazz.getName().equals(request.getName()) &&
                classRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên lớp đã tồn tại");
        }

        clazz.setName(request.getName());

        // Cập nhật Faculty object
        if (request.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
            clazz.setFaculty(faculty);
        }

        clazz.setCourseYear(request.getCourseYear());

        return classRepository.save(clazz);
    }

    @Override
    public void deleteClass(Long id) {
        Class clazz = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));
        classRepository.delete(clazz);
    }

    @Override
    public Class getClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));
    }

    @Override
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    private String generateClassCode() {
        long count = classRepository.count();
        return String.format("LH%08d", count + 1);
    }
}
