package too.sgacf.service;

import java.util.stream.Collectors;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.PhoneTypeDto;
import too.sgacf.model.PhoneTypeModel;
import too.sgacf.repository.IPhoneTypeRepository;

@Service
public class PhoneTypeService {
    @Autowired
    private IPhoneTypeRepository repository;

    public List<PhoneTypeDto> listAllphones(){
        List<PhoneTypeModel> phones = repository.findAll(Sort.by("id").ascending());
        return phones.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<PhoneTypeDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<PhoneTypeModel> phones = this.repository.findByStatus(status);
        return phones.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<PhoneTypeDto> listByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
    
        List<PhoneTypeModel> phones = this.repository.findByQuery(query);
        return phones.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public PhoneTypeDto findById(Long id) {
        return this.repository.findById(id).map(this::convertToDto).orElse(null);
    }
    
    public PhoneTypeModel save(PhoneTypeDto dto) {
        Optional<PhoneTypeModel> phone = this.repository.findByName(dto.getName());
        if (phone.isPresent() && (dto.getId()==null || !phone.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El tipo de teléfono ya existe.");
        }

        PhoneTypeModel PhoneTypeModel;

        if (dto.getId() == null) {
            PhoneTypeModel = new PhoneTypeModel();
            PhoneTypeModel.setStatus(dto.isStatus());
        }
         else {
            Optional<PhoneTypeModel> phoneOptional = this.repository.findById(dto.getId());
            if (phoneOptional.isPresent() || phoneOptional.get().isStatus()) {
                PhoneTypeModel = phoneOptional.get();
                if (PhoneTypeModel.getName().equals(dto.getName()) && PhoneTypeModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new EntityNotFoundException();
            }
        }

        PhoneTypeModel.setName(dto.getName());

        return this.repository.save(PhoneTypeModel);
    }
    
    public void delete(Long id) {
        Optional<PhoneTypeModel> phone = this.repository.findById(id);
        if (phone.isPresent() || !phone.get().isStatus()) {
            PhoneTypeModel PhoneTypeModel = phone.get();
            PhoneTypeModel.setStatus(false);
            this.repository.save(PhoneTypeModel);
        } else{
            throw new UnsupportedOperationException();
        }
    }

    public PhoneTypeDto convertToDto(PhoneTypeModel phone){
        PhoneTypeDto dto = new PhoneTypeDto();
        dto.setId(phone.getId());
        dto.setName(phone.getName());
        dto.setStatus(phone.isStatus());
        return dto;
    }
}
