package iuh.fit.se.server.service;

import iuh.fit.se.common.model.SinhVienDTO;

import java.util.List;

public interface IStudentService {
    boolean addStudent(SinhVienDTO sv);
    SinhVienDTO findStudentById(String maSV);
    List<SinhVienDTO> getAllStudents();
    boolean updateStudent(SinhVienDTO sv);
    boolean deleteStudent(String maSV);
}

