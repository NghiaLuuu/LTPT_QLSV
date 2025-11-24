package iuh.fit.se.mapper;

import iuh.fit.se.dto.response.ClassResponseDTO;
import iuh.fit.se.dto.response.FacultyDto;
import iuh.fit.se.dto.response.StudentResponseDTO;
import iuh.fit.se.model.Class;

import java.util.stream.Collectors;

public class ClassMapper {

    public static ClassResponseDTO toDto(Class clazz) {
        ClassResponseDTO dto = new ClassResponseDTO();
        dto.setId(clazz.getId());
        dto.setCode(clazz.getCode());
        dto.setName(clazz.getName());
        dto.setCourseYear(clazz.getCourseYear());

        if(clazz.getFaculty() != null){
            FacultyDto facultyDto = new FacultyDto();
            facultyDto.setId(clazz.getFaculty().getId());
            facultyDto.setName(clazz.getFaculty().getName());
            dto.setFaculty(facultyDto);
        }

        if (clazz.getStudents() != null) {
            dto.setStudents(clazz.getStudents().stream()
                    .map(s -> {
                        StudentResponseDTO studentDto = new StudentResponseDTO();
                        studentDto.setId(s.getId());
                        studentDto.setStudentCode(s.getStudentCode());
                        studentDto.setFullName(s.getFullName());
                        studentDto.setClassId(clazz.getId());
                        studentDto.setClassName(clazz.getName());
                        return studentDto;
                    })
                    .collect(Collectors.toList()) // <-- phải chắc chắn dto.setStudents nhận List<StudentResponseDTO>
            );
        }

        return dto;
    }
}
