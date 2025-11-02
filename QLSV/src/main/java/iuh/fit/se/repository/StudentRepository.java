package iuh.fit.se.repository;

import iuh.fit.se.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Boolean existsByStudentCode(String studentCode);
    Boolean existsByEmail(String email);
    Optional<Student> findByStudentCode(String studentCode);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrollments e LEFT JOIN FETCH e.subject sub LEFT JOIN FETCH sub.lecturer WHERE s.id = :id")
    Optional<Student> findByIdWithEnrollments(@Param("id") Long id);
}
