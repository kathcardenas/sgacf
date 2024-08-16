package too.sgacf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import too.sgacf.model.GeneroModel;
import too.sgacf.repository.IGeneroRepository;

@Service
public class GeneroService {

    @Autowired
    private IGeneroRepository repository;

    public List<GeneroModel> list(){
        return this.repository.findAll(Sort.by("id").descending());
    }

    public GeneroModel findById(Long id){
        Optional<GeneroModel> optional = this.repository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public void save(GeneroModel model){
        this.repository.save(model);
    }

}
