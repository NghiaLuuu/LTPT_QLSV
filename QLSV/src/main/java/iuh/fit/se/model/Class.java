package iuh.fit.se.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Class.class)
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Column(nullable = false)
    private Integer courseYear;

    // Giảng viên chủ nhiệm
    @ManyToOne
    @JoinColumn(name = "head_lecturer_id")
    private Lecturer headLecturer;

    // Danh sách sinh viên trong lớp
    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL)
    private List<Student> students;
}
