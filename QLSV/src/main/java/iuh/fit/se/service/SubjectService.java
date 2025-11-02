package iuh.fit.se.service;

import iuh.fit.se.dto.request.SubjectRequest;
import iuh.fit.se.model.Subject;

import java.util.List;

public interface SubjectService {
    Subject createSubject(SubjectRequest request);
    Subject updateSubject(Long id, SubjectRequest request);
    void deleteSubject(Long id);
    Subject getSubjectById(Long id);
    List<Subject> getAllSubjects();
}

