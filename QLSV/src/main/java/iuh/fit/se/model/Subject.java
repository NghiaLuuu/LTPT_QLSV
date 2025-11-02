package iuh.fit.se.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Subject.class)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer credit;

    // Số lượng sinh viên tối đa
    @Column(name = "max_students")
    private Integer maxStudents;

    // Giảng viên dạy môn học này
    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    // Danh sách sinh viên đăng ký môn học (thông qua Enrollment)
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;
}
