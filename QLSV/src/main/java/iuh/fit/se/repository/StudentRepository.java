package iuh.fit.se.repository;

import iuh.fit.se.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Boolean existsByStudentCode(String studentCode);
    Boolean existsByEmail(String email);
    Optional<Student> findByStudentCode(String studentCode);
}

