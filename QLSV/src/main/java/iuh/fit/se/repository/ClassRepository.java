package iuh.fit.se.repository;

import iuh.fit.se.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    boolean existsByCode(String code);
    @Query(value = "SELECT TOP 1 * FROM classes ORDER BY CAST(SUBSTRING(code, 3, LEN(code)) AS INT) DESC", nativeQuery = true)
    Class findTopByCodeNumberDesc();
}
