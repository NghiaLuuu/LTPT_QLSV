package iuh.fit.se.service;

import iuh.fit.se.dto.request.ClassRequest;
import iuh.fit.se.model.Class;

import java.util.List;

public interface ClassService {
    Class createClass(ClassRequest request);
    Class updateClass(Long id, ClassRequest request);
    void deleteClass(Long id);
    Class getClassById(Long id);
    List<Class> getAllClasses();
}

