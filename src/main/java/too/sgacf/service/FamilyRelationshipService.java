package too.sgacf.service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.FamilyRelationshipDto;
import too.sgacf.model.FamilyRelationshipModel;
import too.sgacf.repository.IFamilyRelationshipRepository;

@Service
public class FamilyRelationshipService {
    @Autowired
    private IFamilyRelationshipRepository repository;

    public List<FamilyRelationshipDto> listAllrelationships(){
        List<FamilyRelationshipModel> relationships = repository.findAll(Sort.by("id").ascending());
        return relationships.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FamilyRelationshipDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<FamilyRelationshipModel> relationships = this.repository.findByStatus(status);
        return relationships.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FamilyRelationshipDto> listByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
    
        List<FamilyRelationshipModel> relationships = this.repository.findByQuery(query);
        return relationships.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public FamilyRelationshipDto findById(Long id) {
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }
    
    public FamilyRelationshipModel save(FamilyRelationshipDto dto) {
        Optional<FamilyRelationshipModel> relationship = this.repository.findByName(dto.getName());
        if (relationship.isPresent() && (dto.getId()==null || !relationship.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("La profesión ya existe.");
        }

        FamilyRelationshipModel familyRelationshipModel;

        if (dto.getId() == null) {
            familyRelationshipModel = new FamilyRelationshipModel();
            familyRelationshipModel.setStatus(dto.isStatus());
        }
         else {
            Optional<FamilyRelationshipModel> relationshipOptional = this.repository.findById(dto.getId());
            if (relationshipOptional.isPresent() || relationshipOptional.get().isStatus()) {
                familyRelationshipModel = relationshipOptional.get();
                if (familyRelationshipModel.getName().equals(dto.getName()) && familyRelationshipModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new EntityNotFoundException();
            }
        }

        familyRelationshipModel.setName(dto.getName());

        return this.repository.save(familyRelationshipModel);
    }
    
    public void delete(Long id) {
        Optional<FamilyRelationshipModel> relationship = this.repository.findById(id);
        if (relationship.isPresent() || !relationship.get().isStatus()) {
            FamilyRelationshipModel familyRelationshipModel = relationship.get();
            familyRelationshipModel.setStatus(false);
            this.repository.save(familyRelationshipModel);
        } else{
            throw new UnsupportedOperationException();
        }
    }

    public FamilyRelationshipDto convertToDto(FamilyRelationshipModel relationship){
        FamilyRelationshipDto dto = new FamilyRelationshipDto();
        dto.setId(relationship.getId());
        dto.setName(relationship.getName());
        dto.setStatus(relationship.isStatus());
        return dto;
    }
}
