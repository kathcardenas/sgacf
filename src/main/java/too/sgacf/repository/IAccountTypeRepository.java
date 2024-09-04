package too.sgacf.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.AccountTypeModel;

@Repository
public interface IAccountTypeRepository extends JpaRepository<AccountTypeModel, Long>{
    @Query("SELECT a FROM AccountTypeModel a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(a.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<AccountTypeModel> findByQuery(@Param("q") String q);

    public List<AccountTypeModel> findByStatus(@Param("status") Boolean status);

    Optional<AccountTypeModel> findByName(String name);

}
