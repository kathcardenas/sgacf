package too.sgacf.service;

import java.util.stream.Collectors;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.ProfessionDto;
import too.sgacf.model.ProfessionModel;
import too.sgacf.repository.IProfessionRepository;

@Service
public class ProfessionService {
    @Autowired
    private IProfessionRepository repository;

    public List<ProfessionDto> listAllprofessions(){
        List<ProfessionModel> professions = repository.findAll(Sort.by("id").ascending());
        return professions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<ProfessionDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<ProfessionModel> professions = this.repository.findByStatus(status);
        return professions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<ProfessionDto> listByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
    
        List<ProfessionModel> professions = this.repository.findByQuery(query);
        return professions.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public ProfessionDto findById(Long id) {
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }
    
    public ProfessionModel save(ProfessionDto dto) {
        Optional<ProfessionModel> profession = this.repository.findByName(dto.getName());
        if (profession.isPresent() && (dto.getId()==null || !profession.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("La profesión ya existe.");
        }

        ProfessionModel ProfessionModel;

        if (dto.getId() == null) {
            ProfessionModel = new ProfessionModel();
            ProfessionModel.setStatus(dto.isStatus());
        }
         else {
            Optional<ProfessionModel> professionOptional = this.repository.findById(dto.getId());
            if (professionOptional.isPresent() || professionOptional.get().isStatus()) {
                ProfessionModel = professionOptional.get();
                if (ProfessionModel.getName().equals(dto.getName()) && ProfessionModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new EntityNotFoundException();
            }
        }

        ProfessionModel.setName(dto.getName());

        return this.repository.save(ProfessionModel);
    }
    
    public void delete(Long id) {
        Optional<ProfessionModel> profession = this.repository.findById(id);
        if (profession.isPresent() || !profession.get().isStatus()) {
            ProfessionModel ProfessionModel = profession.get();
            ProfessionModel.setStatus(false);
            this.repository.save(ProfessionModel);
        } else{
            throw new UnsupportedOperationException();
        }
    }

    public ProfessionDto convertToDto(ProfessionModel profession){
        ProfessionDto dto = new ProfessionDto();
        dto.setId(profession.getId());
        dto.setName(profession.getName());
        dto.setStatus(profession.isStatus());
        return dto;
    }
}
