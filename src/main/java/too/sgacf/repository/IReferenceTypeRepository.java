package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.ReferenceTypeModel;

@Repository
public interface IReferenceTypeRepository extends JpaRepository<ReferenceTypeModel, Long>{

    @Query("SELECT r FROM ReferenceTypeModel r WHERE " +
    "LOWER(r.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
    "CAST(r.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<ReferenceTypeModel> findByQuery(@Param("q") String q);

    public List<ReferenceTypeModel> findByStatus(@Param("status") Boolean status);

    Optional<ReferenceTypeModel> findByName(String name);
}
