package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.MaritalStatusDto;
import too.sgacf.model.MaritalStatusModel;
import too.sgacf.repository.IMaritalStatusRepository;

@Service
public class MaritalStatusService {

    @Autowired
    private IMaritalStatusRepository repository;

    public List<MaritalStatusDto> listAllMaritalStatus(){
        List<MaritalStatusModel> status = repository.findAll(Sort.by("id").ascending());
        return status.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MaritalStatusDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<MaritalStatusModel> genres = this.repository.findByStatus(status);
        return genres.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MaritalStatusDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<MaritalStatusModel> maritalStatusModels = this.repository.findByQuery(query);
        return maritalStatusModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public MaritalStatusDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public MaritalStatusModel save(MaritalStatusDto dto){
        Optional<MaritalStatusModel> maritalstatus = this.repository.findByName(dto.getName());
        if (maritalstatus.isPresent() && (dto.getId()==null || !maritalstatus.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El estado civil ya existe.");
        }

        MaritalStatusModel maritalStatusModel;

        if (dto.getId() == null) {
            maritalStatusModel = new MaritalStatusModel();
            maritalStatusModel.setStatus(dto.isStatus());
        } else{
            Optional<MaritalStatusModel> maritalOptional = this.repository.findById(dto.getId());
            if (maritalOptional.isPresent() || maritalOptional.get().isStatus()) {
                maritalStatusModel = maritalOptional.get();
                if (maritalStatusModel.getName().equals(dto.getName()) && maritalStatusModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        maritalStatusModel.setName(dto.getName());

        return repository.save(maritalStatusModel);
    }

    public void delete(Long id){
        Optional<MaritalStatusModel> status = this.repository.findById(id);
        if (status.isPresent() || !status.get().isStatus()) {
            MaritalStatusModel maritalStatusModel = status.get();
            maritalStatusModel.setStatus(false);
            this.repository.save(maritalStatusModel);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public MaritalStatusDto convertToDto(MaritalStatusModel statusModel){
        MaritalStatusDto dto = new MaritalStatusDto();
        dto.setId(statusModel.getId());
        dto.setName(statusModel.getName());
        dto.setStatus(statusModel.isStatus());
        return dto;
    }

}
