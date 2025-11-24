package iuh.fit.se.service.impl;

import iuh.fit.se.model.Class;
import iuh.fit.se.model.Faculty;
import iuh.fit.se.repository.ClassRepository;
import iuh.fit.se.repository.FacultyRepository;
import iuh.fit.se.service.ClassService;
import iuh.fit.se.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final FacultyRepository facultyRepository;

    @Override
    @Cacheable(value = "classes:all")
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    @Override
    public Class getClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
    }

    @Override
    @CacheEvict(value = "classes:all", allEntries = true)
    public Class createClass(Class clazz, Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        clazz.setFaculty(faculty);
        clazz.setCode(generateCode()); // sinh code tự động

        return classRepository.save(clazz);
    }




    @Override
    @CacheEvict(value = "classes:all", allEntries = true)
    public Class updateClass(Long id, Class clazz, Long facultyId) {
        Class existing = getClassById(id);

        existing.setName(clazz.getName());
        existing.setCourseYear(clazz.getCourseYear());

        if (facultyId != null) {
            Faculty faculty = facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + facultyId));
            existing.setFaculty(faculty);
        }

        return classRepository.save(existing);
    }

    @Override
    @CacheEvict(value = "classes:all", allEntries = true)
    public void deleteClass(Long id) {
        Class existing = getClassById(id);
        classRepository.delete(existing);
    }

    @Override
    public String generateCode() {
        Class lastClass = classRepository.findTopByCodeNumberDesc();
        int number = 1;

        if (lastClass != null && lastClass.getCode() != null) {
            try {
                number = Integer.parseInt(lastClass.getCode().substring(2)) + 1;
            } catch (NumberFormatException e) {
                number = 1;
            }
        }

        return String.format("LH%03d", number);
    }
}
