package iuh.fit.se.server.service;

import iuh.fit.se.common.model.SinhVien;
import iuh.fit.se.common.model.SinhVienDTO;
import iuh.fit.se.server.util.SinhVienMapper;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter to expose a JPA-backed service as IStudentService (DTO-based).
 */
public class JpaStudentServiceAdapter implements IStudentService {
    private final JpaStudentService jpaService;

    public JpaStudentServiceAdapter(EntityManagerFactory emf) {
        this.jpaService = new JpaStudentService(emf);
    }

    @Override
    public boolean addStudent(SinhVienDTO svDto) {
        SinhVien e = SinhVienMapper.toEntity(svDto);
        return jpaService.addStudent(e);
    }

    @Override
    public SinhVienDTO findStudentById(String maSV) {
        SinhVien e = jpaService.findStudentById(maSV);
        return SinhVienMapper.toDTO(e);
    }

    @Override
    public List<SinhVienDTO> getAllStudents() {
        List<SinhVien> all = jpaService.getAllStudents();
        return all.stream().map(SinhVienMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean updateStudent(SinhVienDTO svDto) {
        SinhVien e = SinhVienMapper.toEntity(svDto);
        return jpaService.updateStudent(e);
    }

    @Override
    public boolean deleteStudent(String maSV) {
        return jpaService.deleteStudent(maSV);
    }
}
