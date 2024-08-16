package too.sgacf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import too.sgacf.model.GeneroModel;

@Repository
public interface IGeneroRepository extends JpaRepository<GeneroModel, Long>{

}
