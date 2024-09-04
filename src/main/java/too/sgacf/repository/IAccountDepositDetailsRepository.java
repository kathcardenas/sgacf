package too.sgacf.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.AccountDepositDetailsModel;

@Repository
public interface IAccountDepositDetailsRepository extends JpaRepository<AccountDepositDetailsModel, Long>{
    @Query("SELECT ad FROM AccountDepositDetailsModel ad WHERE " +
           "CAST(ad.amount AS string) LIKE CONCAT('%', :q, '%') OR " +
           "CAST(ad.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<AccountDepositDetailsModel> findByQuery(@Param("q") String q);

    public List<AccountDepositDetailsModel> findByStatus(@Param("status") Boolean status);

    Optional<AccountDepositDetailsModel> findByAmount(BigDecimal amount);
}
