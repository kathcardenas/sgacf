package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.MemberCooperativeAssociationModel;

@Repository
public interface IMemberCooperativeAssociationRepository extends JpaRepository<MemberCooperativeAssociationModel, Long>{

    @Query("SELECT mc FROM MemberCooperativeAssociationModel mc WHERE "+
            "LOWER(mc.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "CAST(mc.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<MemberCooperativeAssociationModel> findByQuery(@Param("q") String q);

    public List<MemberCooperativeAssociationModel> findByStatus(@Param("status") Boolean status);

    Optional<MemberCooperativeAssociationModel> findByName(String name);
}
