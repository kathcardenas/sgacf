package too.sgacf.service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.AccountTypeDto;
import too.sgacf.model.AccountTypeModel;
import too.sgacf.repository.IAccountTypeRepository;

@Service
public class AccountTypeService {

    @Autowired
    private IAccountTypeRepository repository;

    public List<AccountTypeDto> listAllAccountTypes(){
        List<AccountTypeModel> account = repository.findAll(Sort.by("id").ascending());
        return account.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountTypeDto> listByStatus(Boolean account){
        if (account == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<AccountTypeModel> accounts = this.repository.findByStatus(account);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountTypeDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<AccountTypeModel> accountTypeModels = this.repository.findByQuery(query);
        return accountTypeModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AccountTypeDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public AccountTypeModel save(AccountTypeDto dto){
        Optional<AccountTypeModel> account = this.repository.findByName(dto.getName());
        if (account.isPresent() && (dto.getId()==null || !account.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("La referencia ya existe.");
        }

        AccountTypeModel accountTypeModel;

        if (dto.getId() == null) {
            accountTypeModel = new AccountTypeModel();
            accountTypeModel.setStatus(dto.isStatus());
        } else{
            Optional<AccountTypeModel> accountOptional = this.repository.findById(dto.getId());
            if (accountOptional.isPresent() || accountOptional.get().isStatus()) {
                accountTypeModel = accountOptional.get();
                if (accountTypeModel.getName().equals(dto.getName()) && accountTypeModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        accountTypeModel.setName(dto.getName());

        return repository.save(accountTypeModel);
    }

    public void delete(Long id){
        Optional<AccountTypeModel> status = this.repository.findById(id);
        if (status.isPresent() || !status.get().isStatus()) {
            AccountTypeModel accountTypeModel = status.get();
            accountTypeModel.setStatus(false);
            this.repository.save(accountTypeModel);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public AccountTypeDto convertToDto(AccountTypeModel accountTypeModel){
        AccountTypeDto dto = new AccountTypeDto();
        dto.setId(accountTypeModel.getId());
        dto.setName(accountTypeModel.getName());
        dto.setStatus(accountTypeModel.isStatus());
        return dto;
    }
}
