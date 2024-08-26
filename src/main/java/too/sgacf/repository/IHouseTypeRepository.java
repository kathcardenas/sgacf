package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.HouseTypeModel;

@Repository
public interface IHouseTypeRepository extends JpaRepository<HouseTypeModel, Long>{

    @Query("SELECT h FROM HouseTypeModel h WHERE "+
    "LOWER(h.name) LIKE LOWER(CONCAT('%', :q, '%')) OR "+
    "CAST(h.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<HouseTypeModel> findByQuery(@Param("q") String q);

    public List<HouseTypeModel> findByStatus(@Param("status") Boolean status);

    Optional<HouseTypeModel> findByName(String name);

}
