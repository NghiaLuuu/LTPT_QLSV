package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.FacultyRequest;
import iuh.fit.se.dto.response.FacultyResponse;
import iuh.fit.se.exception.ConflictException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.model.Faculty;
import iuh.fit.se.repository.FacultyRepository;
import iuh.fit.se.service.FacultyService;
import iuh.fit.se.util.LocalCacheClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // thêm WebSocket

    @Override
    @CacheEvict(value = "faculties:all", allEntries = true)
    public FacultyResponse createFaculty(FacultyRequest request) {
        String facultyCode = request.getCode();
        if (facultyCode == null || facultyCode.isEmpty()) {
            facultyCode = generateFacultyCode();
        }

        if (facultyRepository.existsByCode(facultyCode)) {
            throw new ConflictException("Mã khoa đã tồn tại");
        }

        if (facultyRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên khoa đã tồn tại");
        }

        Faculty faculty = new Faculty();
        faculty.setCode(facultyCode);
        faculty.setName(request.getName());
        faculty.setDescription(request.getDescription());

        Faculty savedFaculty = facultyRepository.save(faculty);

        // 🔥 Evict caches
        localCacheClient.evict("faculties:all");
        localCacheClient.evict("faculty:id:" + savedFaculty.getId());

        // 🔥 WebSocket broadcast
        messagingTemplate.convertAndSend("/topic/faculties/updates",
                "Khoa " + savedFaculty.getName() + " đã được tạo");

        return new FacultyResponse(savedFaculty);
    }

    @Override
    @CacheEvict(value = "faculties:all", allEntries = true)
    public FacultyResponse updateFaculty(Long id, FacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));

        if (!faculty.getCode().equals(request.getCode()) &&
                facultyRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã khoa đã tồn tại");
        }

        if (!faculty.getName().equals(request.getName()) &&
                facultyRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên khoa đã tồn tại");
        }

        faculty.setCode(request.getCode());
        faculty.setName(request.getName());
        faculty.setDescription(request.getDescription());

        Faculty updatedFaculty = facultyRepository.save(faculty);

        // 🔥 Evict caches
        localCacheClient.evict("faculties:all");
        localCacheClient.evict("faculty:id:" + id);

        // 🔥 WebSocket broadcast
        messagingTemplate.convertAndSend("/topic/faculties/updates",
                "Khoa " + updatedFaculty.getName() + " đã được cập nhật");

        return new FacultyResponse(updatedFaculty);
    }

    @Override
    @CacheEvict(value = "faculties:all", allEntries = true)
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
        facultyRepository.delete(faculty);

        // 🔥 Evict caches
        localCacheClient.evict("faculties:all");
        localCacheClient.evict("faculty:id:" + id);

        // 🔥 WebSocket broadcast
        messagingTemplate.convertAndSend("/topic/faculties/updates",
                "Khoa " + faculty.getName() + " đã bị xóa");
    }

    @Override
    public FacultyResponse getFacultyById(Long id) {
        String key = "faculty:id:" + id;
        return localCacheClient.getOrLoad(key, FacultyResponse.class, () -> {
            Faculty faculty = facultyRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Khoa không tồn tại"));
            return new FacultyResponse(faculty);
        });
    }

    @Override
    @Cacheable(value = "faculties:all")
    public List<FacultyResponse> getAllFaculties() {
        String key = "faculties:all";
        return localCacheClient.getOrLoad(key, new TypeReference<List<FacultyResponse>>() {}, () ->
                facultyRepository.findAll().stream()
                        .map(FacultyResponse::new)
                        .collect(Collectors.toList())
        );
    }

    private String generateFacultyCode() {
        long count = facultyRepository.count();
        return String.format("K%08d", count + 1);
    }
}
