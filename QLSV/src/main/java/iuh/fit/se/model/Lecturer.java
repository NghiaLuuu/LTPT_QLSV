package iuh.fit.se.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lecturers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Lecturer.class)
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String lecturerCode;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String faculty;

    @Column(nullable = false)
    private String degree; // Học vị: Tiến sĩ, Thạc sĩ, etc.

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Danh sách môn học giảng viên này dạy
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL)
    private List<Subject> subjects;

    // Danh sách lớp mà giảng viên này làm chủ nhiệm
    @OneToMany(mappedBy = "headLecturer", cascade = CascadeType.ALL)
    private List<Class> headClasses;
}
