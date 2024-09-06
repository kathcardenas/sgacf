package too.sgacf.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import too.sgacf.dto.MemberPaymentDto;
import too.sgacf.model.MemberPaymentModel;
import too.sgacf.repository.IMemberPaymentRepository;

@Service
public class MemberPaymentService {

    @Autowired
    private IMemberPaymentRepository repository;

    public List<MemberPaymentDto> listAllMemberPayment(){
        List<MemberPaymentModel> memberPayments = repository.findAll(Sort.by("id").ascending());
        return memberPayments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MemberPaymentDto> listByStatus(Boolean account){
        if (account == null) {
            throw new IllegalArgumentException("Debe ingresar un estado.");
        }
        List<MemberPaymentModel> memberPayments = this.repository.findByStatus(account);
        return memberPayments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MemberPaymentDto> listByPaymentStatus(Boolean account){
        if (account == null) {
            throw new IllegalArgumentException("Debe ingresar un estado de pago.");
        }
        List<MemberPaymentModel> accounts = this.repository.findByStatus(account);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<MemberPaymentDto> listByQuery(String query){
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un texto para realizar la búsqueda.");
        }

        List<MemberPaymentModel> memberPaymentModels = this.repository.findByQuery(query);
        return memberPaymentModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public MemberPaymentDto findById(Long id){
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    //guardar o editar, se le envía registro
    public MemberPaymentModel save(MemberPaymentDto dto){
        //se crea pago nuevo
        LocalDateTime now = LocalDateTime.now();
        MemberPaymentModel paymentModel;
        // si, el registro que se envía tiene un id nulo, es un nuevo registro
        if (dto.getId() == null) {
            //inicializamos pago
            paymentModel = new MemberPaymentModel();
            if (!dto.isPaymentStatus()) {
                throw new IllegalArgumentException("El pago debe ser realizado.");                
            }
            paymentModel.setCurrentDate(now);
            paymentModel.setAmount(dto.getAmount());
        } else{ // el registro que se envía tiene un id diferente de nulo, entonces se edita el registro
            //Buscamos el registro en la base de datos con el id
            Optional<MemberPaymentModel> paymentOptional = this.repository.findById(dto.getId());
            //si el registro tiene un estado activo o el pago está realizado
            if (paymentOptional.isPresent() || paymentOptional.get().isPaymentStatus()) {
                // el pago se inicializa con los datos del registro obtenido
                paymentModel = paymentOptional.get();
                if (paymentModel.getAmount() != dto.getAmount()) {
                    throw new IllegalArgumentException("No puede editar el monto del pago.");   
                }
            } else{
                throw new EntityNotFoundException();
            }
        }
        paymentModel.setStatus(dto.isStatus());
        paymentModel.setPaymentStatus(dto.isPaymentStatus());
        return repository.save(paymentModel);
    }

    public void delete(Long id){
        Optional<MemberPaymentModel> memberPaymentOptional = this.repository.findById(id);
        if (memberPaymentOptional.isPresent() || !memberPaymentOptional.get().isStatus()) {
            MemberPaymentModel memberPayment = memberPaymentOptional.get();
            memberPayment.setStatus(false);
            this.repository.save(memberPayment);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    public MemberPaymentDto convertToDto(MemberPaymentModel memberPaymentModel){
        MemberPaymentDto dto = new MemberPaymentDto();
        dto.setId(memberPaymentModel.getId());
        dto.setCurrentDate(memberPaymentModel.getCurrentDate());
        dto.setAmount(memberPaymentModel.getAmount());
        dto.setStatus(memberPaymentModel.isStatus());
        dto.setPaymentStatus(memberPaymentModel.isPaymentStatus());
        return dto;
    }
}
