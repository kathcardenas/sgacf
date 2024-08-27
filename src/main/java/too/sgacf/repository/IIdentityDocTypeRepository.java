package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.IdentityDocTypeModel;

@Repository
public interface IIdentityDocTypeRepository extends JpaRepository<IdentityDocTypeModel, Long> {

    @Query("SELECT i FROM IdentityDocTypeModel i WHERE "+
    "LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR "+
    "CAST(i.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<IdentityDocTypeModel> findByQuery(@Param("q") String q);

    public List<IdentityDocTypeModel> findByStatus(@Param("status") Boolean status);

    Optional<IdentityDocTypeModel> findByName(String name);
}
