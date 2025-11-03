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
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
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

    @Autowired
    private LocalCacheClient localCacheClient;

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

        Class saved = classRepository.save(clazz);

        // Evict caches
        localCacheClient.evict("classes:all");
        localCacheClient.evict("class:id:" + saved.getId());

        return saved;
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

        Class updated = classRepository.save(clazz);

        // Evict caches
        localCacheClient.evict("classes:all");
        localCacheClient.evict("class:id:" + id);

        return updated;
    }

    @Override
    public void deleteClass(Long id) {
        Class clazz = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));
        classRepository.delete(clazz);

        // Evict caches
        localCacheClient.evict("classes:all");
        localCacheClient.evict("class:id:" + id);
    }

    @Override
    public Class getClassById(Long id) {
        String key = "class:id:" + id;
        return localCacheClient.getOrLoad(key, Class.class, () ->
                classRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"))
        );
    }

    @Override
    public List<Class> getAllClasses() {
        String key = "classes:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<Class>>() {}, () ->
                classRepository.findAll()
        );
    }

    private String generateClassCode() {
        long count = classRepository.count();
        return String.format("LH%08d", count + 1);
    }
}
