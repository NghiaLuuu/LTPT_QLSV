package iuh.fit.se.service;

import iuh.fit.se.model.Class;

import java.util.List;

public interface ClassService {
    List<Class> getAllClasses();
    Class getClassById(Long id);
    Class createClass(Class clazz, Long facultyId); // Thêm facultyId để map
    Class updateClass(Long id, Class clazz, Long facultyId);
    void deleteClass(Long id);

    String generateCode();
}
