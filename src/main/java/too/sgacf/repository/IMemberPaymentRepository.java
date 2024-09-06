package too.sgacf.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.MemberPaymentModel;

@Repository
public interface IMemberPaymentRepository extends JpaRepository<MemberPaymentModel, Long>{
    @Query("SELECT mp FROM MemberPaymentModel mp WHERE " +
           "CAST(mp.amount AS string) LIKE CONCAT('%', :q, '%') OR " +
           "CAST(mp.currentDate AS string) LIKE CONCAT('%', :q, '%') OR "+
           "CAST(mp.paymentStatus AS string) LIKE CONCAT('%', :q, '%') OR "+
           "CAST(mp.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<MemberPaymentModel> findByQuery(@Param("q") String q);

    public List<MemberPaymentModel> findByPaymentStatus(@Param("paymentStatus") Boolean paymentStatus);

    public List<MemberPaymentModel> findByStatus(@Param("status") Boolean status);

    Optional<MemberPaymentModel> findByAmount(BigDecimal amount);
}
