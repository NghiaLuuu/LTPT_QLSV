package iuh.fit.se.repository;

import iuh.fit.se.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Boolean existsByCode(String code);
    Optional<Subject> findByCode(String code);
}

