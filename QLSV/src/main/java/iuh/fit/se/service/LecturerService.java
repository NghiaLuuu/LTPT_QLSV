package iuh.fit.se.service;

import iuh.fit.se.dto.request.LecturerRequest;
import iuh.fit.se.dto.response.LecturerResponse;

import java.util.List;

public interface LecturerService {
    LecturerResponse createLecturer(LecturerRequest request);
    LecturerResponse updateLecturer(Long id, LecturerRequest request);
    void deleteLecturer(Long id);
    LecturerResponse getLecturerById(Long id);
    List<LecturerResponse> getAllLecturers();
}

