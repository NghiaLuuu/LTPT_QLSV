package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.SinhVienDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple thread-safe in-memory StudentService used as a placeholder
 * until JPA/DB integration is available.
 */
public class StudentService implements IStudentService {
    private final ConcurrentMap<String, SinhVienDTO> store = new ConcurrentHashMap<>();

    public boolean addStudent(SinhVienDTO sv) {
        if (sv == null || sv.getMaSV() == null) return false;
        return store.putIfAbsent(sv.getMaSV(), sv) == null;
    }

    public SinhVienDTO findStudentById(String maSV) {
        if (maSV == null) return null;
        return store.get(maSV);
    }

    public List<SinhVienDTO> getAllStudents() {
        return new ArrayList<>(store.values());
    }

    public boolean updateStudent(SinhVienDTO sv) {
        if (sv == null || sv.getMaSV() == null) return false;
        return store.replace(sv.getMaSV(), sv) != null;
    }

    public boolean deleteStudent(String maSV) {
        if (maSV == null) return false;
        return store.remove(maSV) != null;
    }
}
