package too.sgacf.service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.FieldDto;
import too.sgacf.model.FieldModel;
import too.sgacf.repository.IFieldRepository;

@Service
public class FieldService {
    @Autowired
    private IFieldRepository repository;

    public List<FieldDto> listAllFields(){
        List<FieldModel> fields = repository.findAll(Sort.by("id").ascending());
        return fields.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FieldDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<FieldModel> fields = this.repository.findByStatus(status);
        return fields.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FieldDto> listByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
    
        List<FieldModel> fields = this.repository.findByQuery(query);
        return fields.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public FieldDto findById(Long id) {
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }
    
    public FieldModel save(FieldDto dto) {
        Optional<FieldModel> field = this.repository.findByName(dto.getName());
        if (field.isPresent() && (dto.getId()==null || !field.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El rubro ya existe.");
        }

        FieldModel fieldModel;

        if (dto.getId() == null) {
            fieldModel = new FieldModel();
            fieldModel.setStatus(dto.isStatus());
        }
         else {
            Optional<FieldModel> fieldOptional = this.repository.findById(dto.getId());
            if (fieldOptional.isPresent() || fieldOptional.get().isStatus()) {
                fieldModel = fieldOptional.get();
                if (fieldModel.getName().equals(dto.getName()) && fieldModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new EntityNotFoundException();
            }
        }

        fieldModel.setName(dto.getName());

        return this.repository.save(fieldModel);
    }
    
    public void delete(Long id) {
        Optional<FieldModel> field = this.repository.findById(id);
        if (field.isPresent() || !field.get().isStatus()) {
            FieldModel fieldModel = field.get();
            fieldModel.setStatus(false);
            this.repository.save(fieldModel);
        } else{
            throw new UnsupportedOperationException();
        }
    }

    public FieldDto convertToDto(FieldModel field){
        FieldDto dto = new FieldDto();
        dto.setId(field.getId());
        dto.setName(field.getName());
        dto.setStatus(field.isStatus());
        return dto;
    }
}
