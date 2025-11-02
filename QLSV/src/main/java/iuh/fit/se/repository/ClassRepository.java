package iuh.fit.se.repository;

import iuh.fit.se.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Boolean existsByName(String name);
}

