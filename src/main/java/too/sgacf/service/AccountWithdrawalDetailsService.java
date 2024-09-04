package too.sgacf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.AccountWithdrawalDetailsDto;
import too.sgacf.model.AccountWithdrawalDetailsModel;
import too.sgacf.repository.IAccountWithdrawalDetailsRepository;

@Service
public class AccountWithdrawalDetailsService {
    @Autowired
    private IAccountWithdrawalDetailsRepository repository;

    public List<AccountWithdrawalDetailsDto> listAllAccountsWithdrawalDetails(){
        List<AccountWithdrawalDetailsModel> account = repository.findAll(Sort.by("id").ascending());
        return account.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountWithdrawalDetailsDto> listByStatus(Boolean account){
        if (account == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<AccountWithdrawalDetailsModel> accounts = this.repository.findByStatus(account);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AccountWithdrawalDetailsDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<AccountWithdrawalDetailsModel> accountWithdrawalDetailsModels = this.repository.findByQuery(query);
        return accountWithdrawalDetailsModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AccountWithdrawalDetailsDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public AccountWithdrawalDetailsModel save(AccountWithdrawalDetailsDto dto){
        Optional<AccountWithdrawalDetailsModel> account = this.repository.findByAmount(dto.getAmount());
        if (account.isPresent() && (dto.getId()==null || !account.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("El monto de límite de retiro ya existe.");
        }

        AccountWithdrawalDetailsModel accountWithdrawalDetailsModel;

        if (dto.getId() == null) {
            accountWithdrawalDetailsModel = new AccountWithdrawalDetailsModel();
            accountWithdrawalDetailsModel.setStatus(dto.isStatus());
        } else{
            Optional<AccountWithdrawalDetailsModel> amouOptional = this.repository.findById(dto.getId());
            if (amouOptional.isPresent() || amouOptional.get().isStatus()) {
                accountWithdrawalDetailsModel = amouOptional.get();
                if (accountWithdrawalDetailsModel.getAmount().equals(dto.getAmount()) && accountWithdrawalDetailsModel.getId().equals(dto.getId())) {
                    throw new UnsupportedOperationException();
                }
            } else{
                throw new EntityNotFoundException();
            }
        }

        accountWithdrawalDetailsModel.setAmount(dto.getAmount());

        return repository.save(accountWithdrawalDetailsModel);
    }

    public void delete(Long id){
        Optional<AccountWithdrawalDetailsModel> ammouOptional = this.repository.findById(id);
        if (ammouOptional.isPresent() || !ammouOptional.get().isStatus()) {
            AccountWithdrawalDetailsModel amount = ammouOptional.get();
            amount.setStatus(false);
            this.repository.save(amount);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public AccountWithdrawalDetailsDto convertToDto(AccountWithdrawalDetailsModel accountWithdrawalDetailsModel){
        AccountWithdrawalDetailsDto dto = new AccountWithdrawalDetailsDto();
        dto.setId(accountWithdrawalDetailsModel.getId());
        dto.setAmount(accountWithdrawalDetailsModel.getAmount());
        dto.setStatus(accountWithdrawalDetailsModel.isStatus());
        return dto;
    }
}
