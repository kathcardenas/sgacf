package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.ReferenceTypeDto;
import too.sgacf.model.ReferenceTypeModel;
import too.sgacf.repository.IReferenceTypeRepository;

@Service
public class ReferenceTypeService {

    @Autowired
    private IReferenceTypeRepository repository;

    public List<ReferenceTypeDto> listAllReferencesType(){
        List<ReferenceTypeModel> references = repository.findAll(Sort.by("id").ascending());
        return references.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<ReferenceTypeDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<ReferenceTypeModel> references = this.repository.findByStatus(status);
        return references.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<ReferenceTypeDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<ReferenceTypeModel> referenceTypeModels = this.repository.findByQuery(query);
        return referenceTypeModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ReferenceTypeDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public ReferenceTypeModel save(ReferenceTypeDto dto){
        Optional<ReferenceTypeModel> referenceTyOptional = this.repository.findByName(dto.getName());
        if (referenceTyOptional.isPresent() && (dto.getId()==null || !referenceTyOptional.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("La referencia ya existe.");
        }

        ReferenceTypeModel referenceTypeModel;

        if (dto.getId() == null) {
            referenceTypeModel = new ReferenceTypeModel();
            referenceTypeModel.setStatus(dto.isStatus());
        } else{
            Optional<ReferenceTypeModel> referenceOptional = this.repository.findById(dto.getId());
            if (referenceOptional.isPresent() || referenceOptional.get().isStatus()) {
                referenceTypeModel = referenceOptional.get();
                if (referenceTypeModel.getName().equals(dto.getName()) && referenceTypeModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        referenceTypeModel.setName(dto.getName());

        return repository.save(referenceTypeModel);
    }

    public void delete(Long id){
        Optional<ReferenceTypeModel> referenceOptional = this.repository.findById(id);
        if (referenceOptional.isPresent() || !referenceOptional.get().isStatus()) {
            ReferenceTypeModel referenceTypeModel = referenceOptional.get();
            referenceTypeModel.setStatus(false);
            this.repository.save(referenceTypeModel);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public ReferenceTypeDto convertToDto(ReferenceTypeModel referenceTypeModel){
        ReferenceTypeDto dto = new ReferenceTypeDto();
        dto.setId(referenceTypeModel.getId());
        dto.setName(referenceTypeModel.getName());
        dto.setStatus(referenceTypeModel.isStatus());
        return dto;
    }

}
