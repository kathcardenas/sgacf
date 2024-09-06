package too.sgacf.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.ProfessionModel;

@Repository
public interface IProfessionRepository extends JpaRepository<ProfessionModel, Long>{
    @Query("SELECT p FROM ProfessionModel p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(p.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<ProfessionModel> findByQuery(@Param("q") String q);

    public List<ProfessionModel> findByStatus(@Param("status") Boolean status);

    Optional<ProfessionModel> findByName(String name);
}
