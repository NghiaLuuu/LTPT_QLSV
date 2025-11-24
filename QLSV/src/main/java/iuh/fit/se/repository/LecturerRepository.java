package iuh.fit.se.repository;

import iuh.fit.se.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByLecturerCode(String lecturerCode);
    boolean existsByLecturerCode(String lecturerCode);
    boolean existsByEmail(String email);
}
