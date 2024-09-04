package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.AccountDepositDetailsDto;
import too.sgacf.model.AccountDepositDetailsModel;
import too.sgacf.repository.IAccountDepositDetailsRepository;

@Service
public class AccountDepositDetailsService {
    @Autowired
    private IAccountDepositDetailsRepository repository;

    public List<AccountDepositDetailsDto> listAllAccountDepositsDetails(){
        List<AccountDepositDetailsModel> accounts = repository.findAll(Sort.by("id").ascending());
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountDepositDetailsDto> listByStatus(Boolean account){
        if (account == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<AccountDepositDetailsModel> accounts = this.repository.findByStatus(account);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountDepositDetailsDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<AccountDepositDetailsModel> accountDepositDetailsModels = this.repository.findByQuery(query);
        return accountDepositDetailsModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AccountDepositDetailsDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public AccountDepositDetailsModel save(AccountDepositDetailsDto dto){
        Optional<AccountDepositDetailsModel> account = this.repository.findByAmount(dto.getAmount());
        if (account.isPresent() && (dto.getId()==null || !account.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El monto de límite de depósito ya existe.");
        }

        AccountDepositDetailsModel accountDepositDetailsModel;

        if (dto.getId() == null) {
            accountDepositDetailsModel = new AccountDepositDetailsModel();
            accountDepositDetailsModel.setStatus(dto.isStatus());
        } else{
            Optional<AccountDepositDetailsModel> amouOptional = this.repository.findById(dto.getId());
            if (amouOptional.isPresent() || amouOptional.get().isStatus()) {
                accountDepositDetailsModel = amouOptional.get();
                if (accountDepositDetailsModel.getAmount().equals(dto.getAmount()) && accountDepositDetailsModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        accountDepositDetailsModel.setAmount(dto.getAmount());

        return repository.save(accountDepositDetailsModel);
    }

    public void delete(Long id){
        Optional<AccountDepositDetailsModel> ammouOptional = this.repository.findById(id);
        if (ammouOptional.isPresent() || !ammouOptional.get().isStatus()) {
            AccountDepositDetailsModel amount = ammouOptional.get();
            amount.setStatus(false);
            this.repository.save(amount);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public AccountDepositDetailsDto convertToDto(AccountDepositDetailsModel accountDepositDetailsModel){
        AccountDepositDetailsDto dto = new AccountDepositDetailsDto();
        dto.setId(accountDepositDetailsModel.getId());
        dto.setAmount(accountDepositDetailsModel.getAmount());
        dto.setStatus(accountDepositDetailsModel.isStatus());
        return dto;
    }
}
