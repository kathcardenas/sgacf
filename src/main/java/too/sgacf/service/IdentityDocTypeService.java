package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.IdentityDocTypeDto;
import too.sgacf.model.IdentityDocTypeModel;
import too.sgacf.repository.IIdentityDocTypeRepository;

@Service
public class IdentityDocTypeService {

    @Autowired
    private IIdentityDocTypeRepository repository;

    public List<IdentityDocTypeDto> listAllIDType(){
        List<IdentityDocTypeModel> identitiesDocs = this.repository.findAll(Sort.by("id").ascending());
        return identitiesDocs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<IdentityDocTypeDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }

        List<IdentityDocTypeModel> identitiesDocs = this.repository.findByStatus(status);
        return identitiesDocs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<IdentityDocTypeDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
        List<IdentityDocTypeModel> identitiesDocs = this.repository.findByQuery(query);
        return identitiesDocs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public IdentityDocTypeDto findById(Long id){
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public IdentityDocTypeModel save(IdentityDocTypeDto dto){
        Optional<IdentityDocTypeModel> identity = this.repository.findByName(dto.getName());
        if (identity.isPresent() && (dto.getId()==null || !identity.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El tipo de documento de identidad ya existe.");
        }

        IdentityDocTypeModel identityDocTypeModel;

        if (dto.getId() == null) {
            identityDocTypeModel = new IdentityDocTypeModel();
            identityDocTypeModel.setStatus(dto.isStatus());
        } else{
            Optional<IdentityDocTypeModel> identityOptional = this.repository.findById(dto.getId());
            if (identityOptional.isPresent() || identityOptional.get().isStatus()) {
                identityDocTypeModel = identityOptional.get();
                if (identityDocTypeModel.getName().equals(dto.getName()) && identityDocTypeModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        identityDocTypeModel.setName(dto.getName());

        return repository.save(identityDocTypeModel);
    }

    public void delete(Long id){
        Optional<IdentityDocTypeModel> identity = this.repository.findById(id);
        if (identity.isPresent() || !identity.get().isStatus()) {
            IdentityDocTypeModel identityModel = identity.get();
            identityModel.setStatus(false);
            this.repository.save(identityModel);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public IdentityDocTypeDto convertToDto(IdentityDocTypeModel model){
        IdentityDocTypeDto dto = new IdentityDocTypeDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setStatus(model.isStatus());
        return dto;
    }
}
