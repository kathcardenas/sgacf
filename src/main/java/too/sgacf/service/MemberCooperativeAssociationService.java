package too.sgacf.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.MemberCooperativeAssociationDto;
import too.sgacf.model.MemberCooperativeAssociationModel;
import too.sgacf.repository.IMemberCooperativeAssociationRepository;

@Service
public class MemberCooperativeAssociationService {

    @Autowired
    private IMemberCooperativeAssociationRepository repository;

    public List<MemberCooperativeAssociationDto> listAllMemberAssociations(){
        List<MemberCooperativeAssociationModel> memberAssociation = repository.findAll(Sort.by("id").ascending());
        return memberAssociation.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MemberCooperativeAssociationDto> listByStatus(Boolean status){
        if (status == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<MemberCooperativeAssociationModel> asociations = this.repository.findByStatus(status);
        return asociations.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MemberCooperativeAssociationDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");   
        }
        List<MemberCooperativeAssociationModel> asociations = this.repository.findByQuery(query);
        return asociations.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public MemberCooperativeAssociationDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public MemberCooperativeAssociationModel save(MemberCooperativeAssociationDto dto){
        Optional<MemberCooperativeAssociationModel> memberAssociation = this.repository.findByName(dto.getName());
        if (memberAssociation.isPresent() && (dto.getId()==null || !memberAssociation.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("La asociación ya existe.");
        }

        MemberCooperativeAssociationModel memberAssociationModel;

        if (dto.getId() == null) {
            memberAssociationModel = new MemberCooperativeAssociationModel();
            memberAssociationModel.setStatus(dto.isStatus());
        } else{
            Optional<MemberCooperativeAssociationModel> associationOptional = this.repository.findById(dto.getId());
            if (associationOptional.isPresent() || associationOptional.get().isStatus()) {
                memberAssociationModel = associationOptional.get();
                if (memberAssociationModel.getName().equals(dto.getName()) && memberAssociationModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        memberAssociationModel.setName(dto.getName());

        return repository.save(memberAssociationModel);
    }

    public void delete(Long id){
        Optional<MemberCooperativeAssociationModel> association = this.repository.findById(id);
        if (association.isPresent() || !association.get().isStatus()) {
            MemberCooperativeAssociationModel memberAssociation = association.get();
            memberAssociation.setStatus(false);
            this.repository.save(memberAssociation);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public MemberCooperativeAssociationDto convertToDto(MemberCooperativeAssociationModel model){
        MemberCooperativeAssociationDto dto = new MemberCooperativeAssociationDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setStatus(model.isStatus());
        return dto;
    }
}
