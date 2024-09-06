package too.sgacf.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.PhoneTypeModel;

@Repository
public interface IPhoneTypeRepository extends JpaRepository<PhoneTypeModel, Long>{
    @Query("SELECT pt FROM PhoneTypeModel pt WHERE " +
           "LOWER(pt.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(pt.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<PhoneTypeModel> findByQuery(@Param("q") String q);

    public List<PhoneTypeModel> findByStatus(@Param("status") Boolean status);

    Optional<PhoneTypeModel> findByName(String name);
}
