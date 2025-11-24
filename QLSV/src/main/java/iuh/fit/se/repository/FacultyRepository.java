package iuh.fit.se.repository;

import iuh.fit.se.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}

