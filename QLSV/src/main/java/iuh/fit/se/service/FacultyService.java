package iuh.fit.se.service;

import iuh.fit.se.dto.request.FacultyRequest;
import iuh.fit.se.dto.response.FacultyResponse;

import java.util.List;

public interface FacultyService {
    FacultyResponse createFaculty(FacultyRequest request);
    FacultyResponse updateFaculty(Long id, FacultyRequest request);
    void deleteFaculty(Long id);
    FacultyResponse getFacultyById(Long id);
    List<FacultyResponse> getAllFaculties();
}

