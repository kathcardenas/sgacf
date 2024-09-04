package too.sgacf.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.AccountWithdrawalDetailsModel;

@Repository
public interface IAccountWithdrawalDetailsRepository extends JpaRepository<AccountWithdrawalDetailsModel, Long>{
    @Query("SELECT aw FROM AccountWithdrawalDetailsModel aw WHERE " +
           "CAST(aw.amount AS string) LIKE CONCAT('%', :q, '%') OR " +
           "CAST(aw.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<AccountWithdrawalDetailsModel> findByQuery(@Param("q") String q);

    public List<AccountWithdrawalDetailsModel> findByStatus(@Param("status") Boolean status);

    Optional<AccountWithdrawalDetailsModel> findByAmount(BigDecimal amount);
}
