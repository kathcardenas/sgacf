package too.sgacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import too.sgacf.model.GenreModel;

@Repository
public interface IGenreRepository extends JpaRepository<GenreModel, Long>{

    @Query("SELECT g FROM GenreModel g WHERE " +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(g.status AS string) LIKE CONCAT('%', :q, '%')")
    public List<GenreModel> findByQuery(@Param("q") String q);

    public List<GenreModel> findByStatus(@Param("status") Boolean status);

    Optional<GenreModel> findByName(String name);

    Optional<GenreModel> findByIdAndName(Long id, String name);

}
