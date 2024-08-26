package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.MaritalStatusModel;

@Repository
public interface IMaritalStatusRepository extends JpaRepository<MaritalStatusModel, Long>{
    
    @Query("SELECT m FROM MaritalStatusModel m WHERE " +
    "LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
    "CAST(m.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<MaritalStatusModel> findByQuery(@Param("q") String q);

    public List<MaritalStatusModel> findByStatus(@Param("status") Boolean status);

    Optional<MaritalStatusModel> findByName(String name);
}
