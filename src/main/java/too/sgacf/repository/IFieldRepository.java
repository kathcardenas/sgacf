package too.sgacf.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.FieldModel;

@Repository
public interface IFieldRepository extends JpaRepository<FieldModel, Long>{
    @Query("SELECT f FROM FieldModel f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(f.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<FieldModel> findByQuery(@Param("q") String q);

    public List<FieldModel> findByStatus(@Param("status") Boolean status);

    Optional<FieldModel> findByName(String name);
}
