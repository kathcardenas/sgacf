package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.HouseTypeDto;
import too.sgacf.model.HouseTypeModel;
import too.sgacf.repository.IHouseTypeRepository;

@Service
public class HouseTypeService {

    @Autowired
    private IHouseTypeRepository repository;

    public List<HouseTypeDto> listAllHousesTypes(){
        List<HouseTypeModel> housesTypes = this.repository.findAll(Sort.by("id").ascending());
        return housesTypes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<HouseTypeDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<HouseTypeModel> houseTypeModels = this.repository.findByStatus(status);
        return houseTypeModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<HouseTypeDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }
        List<HouseTypeModel> housesTypes = this.repository.findByQuery(query);
        return housesTypes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public HouseTypeDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public HouseTypeModel save(HouseTypeDto dto){
        Optional<HouseTypeModel> houseOptional = this.repository.findByName(dto.getName());
        if (houseOptional.isPresent() && (dto.getId()==null || !houseOptional.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El tipo de vivienda ya existe.");
        }

        HouseTypeModel houseTypeModel;

        if (dto.getId() == null) {
            houseTypeModel = new HouseTypeModel();
            houseTypeModel.setStatus(dto.isStatus());
        } else{
            Optional<HouseTypeModel> houseTypeOptional = this.repository.findById(dto.getId());
            if (houseTypeOptional.isPresent() || houseOptional.get().isStatus()) {
                houseTypeModel = houseTypeOptional.get();
                if (houseTypeModel.getName().equals(dto.getName()) && houseTypeModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        houseTypeModel.setName(dto.getName());

        return repository.save(houseTypeModel);
    }

    public void delete(Long id){
        Optional<HouseTypeModel> house = this.repository.findById(id);
        if (house.isPresent() || !house.get().isStatus()) {
            HouseTypeModel houseTypeModel = house.get();
            houseTypeModel.setStatus(false);
            this.repository.save(houseTypeModel);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public HouseTypeDto convertToDto(HouseTypeModel HouseTypeModel){
        HouseTypeDto dto = new HouseTypeDto();
        dto.setId(HouseTypeModel.getId());
        dto.setName(HouseTypeModel.getName());
        dto.setStatus(HouseTypeModel.isStatus());
        return dto;
    }
}
