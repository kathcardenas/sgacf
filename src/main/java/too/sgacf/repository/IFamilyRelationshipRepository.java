package too.sgacf.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.FamilyRelationshipModel;

@Repository
public interface IFamilyRelationshipRepository extends JpaRepository<FamilyRelationshipModel, Long>{
    @Query("SELECT fr FROM FamilyRelationshipModel fr WHERE " +
           "LOWER(fr.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(fr.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<FamilyRelationshipModel> findByQuery(@Param("q") String q);

    public List<FamilyRelationshipModel> findByStatus(@Param("status") Boolean status);

    Optional<FamilyRelationshipModel> findByName(String name);
}
