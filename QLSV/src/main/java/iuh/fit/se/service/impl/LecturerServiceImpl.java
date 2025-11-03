package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.LecturerRequest;
import iuh.fit.se.dto.response.LecturerResponse;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Lecturer;
import iuh.fit.se.repository.LecturerRepository;
import iuh.fit.se.service.LecturerService;
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LecturerServiceImpl implements LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Override
    public LecturerResponse createLecturer(LecturerRequest request) {
        if (lecturerRepository.existsByLecturerCode(request.getLecturerCode())) {
            throw new ConflictException("Mã giảng viên đã tồn tại");
        }

        if (lecturerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        Lecturer lecturer = new Lecturer();
        // Tự động sinh mã nếu không có
        if (request.getLecturerCode() == null || request.getLecturerCode().isEmpty()) {
            lecturer.setLecturerCode(generateLecturerCode());
        } else {
            lecturer.setLecturerCode(request.getLecturerCode());
        }
        lecturer.setFullName(request.getFullName());
        lecturer.setEmail(request.getEmail());
        lecturer.setPhone(request.getPhone());
        lecturer.setFaculty(request.getFaculty());
        lecturer.setDegree(request.getDegree());
        lecturer.setDob(request.getDob());
        lecturer.setGender(request.getGender());

        Lecturer savedLecturer = lecturerRepository.save(lecturer);

        // Evict caches
        localCacheClient.evict("lecturers:all");
        localCacheClient.evict("lecturer:id:" + savedLecturer.getId());

        return new LecturerResponse(savedLecturer);
    }

    @Override
    public LecturerResponse updateLecturer(Long id, LecturerRequest request) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại"));

        if (!lecturer.getLecturerCode().equals(request.getLecturerCode()) &&
                lecturerRepository.existsByLecturerCode(request.getLecturerCode())) {
            throw new ConflictException("Mã giảng viên đã tồn tại");
        }

        if (!lecturer.getEmail().equals(request.getEmail()) &&
                lecturerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        lecturer.setLecturerCode(request.getLecturerCode());
        lecturer.setFullName(request.getFullName());
        lecturer.setEmail(request.getEmail());
        lecturer.setPhone(request.getPhone());
        lecturer.setFaculty(request.getFaculty());
        lecturer.setDegree(request.getDegree());
        lecturer.setDob(request.getDob());
        lecturer.setGender(request.getGender());

        Lecturer updatedLecturer = lecturerRepository.save(lecturer);

        // Evict caches
        localCacheClient.evict("lecturers:all");
        localCacheClient.evict("lecturer:id:" + id);

        return new LecturerResponse(updatedLecturer);
    }

    @Override
    public void deleteLecturer(Long id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại"));
        lecturerRepository.delete(lecturer);

        // Evict caches
        localCacheClient.evict("lecturers:all");
        localCacheClient.evict("lecturer:id:" + id);
    }

    @Override
    public LecturerResponse getLecturerById(Long id) {
        String key = "lecturer:id:" + id;
        return localCacheClient.getOrLoad(key, LecturerResponse.class, () -> {
            Lecturer lecturer = lecturerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại"));
            return new LecturerResponse(lecturer);
        });
    }

    @Override
    public List<LecturerResponse> getAllLecturers() {
        String key = "lecturers:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<LecturerResponse>>() {}, () ->
                lecturerRepository.findAll().stream()
                        .map(LecturerResponse::new)
                        .collect(Collectors.toList())
        );
    }

    private String generateLecturerCode() {
        long count = lecturerRepository.count();
        return String.format("GV%08d", count + 1);
    }
}
