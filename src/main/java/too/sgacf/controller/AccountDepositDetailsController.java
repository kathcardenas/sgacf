package too.sgacf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import too.sgacf.dto.AccountDepositDetailsDto;
import too.sgacf.service.AccountDepositDetailsService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Detalle depósito cuenta", description = "Controlador para límite de depósito de cuenta")
public class AccountDepositDetailsController {

    @Autowired
    private AccountDepositDetailsService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/limite-deposito - Retrieves all account deposit detail
     * GET /api/v1/limite-deposito?status=value - Retrieves account deposit detail by status
     */
    @Operation(
        summary = "Listar Límites depósito de cuentas",
        description = "Recupera todos los límites de depósito de cuentas o límite de retiro de cuentas por estado",
        parameters = {
            @Parameter(
                name = "status",
                description = "Variable de estado activo o desactivado",
                required = false,
                in = ParameterIn.DEFAULT,
                schema = @Schema(
                    type = "boolean"
                )
            )
        }
    )
    @GetMapping("/limite-deposito")
    public ResponseEntity<?> getMethod(@RequestParam(required = false) String status) {
        if (status!=null) {
            if (status.isEmpty()) {
                return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Debe ingresar un estado.");                
            }
            if (!status.equalsIgnoreCase("true") && !status.equalsIgnoreCase("false")) {
                return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "El estado debe ser 'true' o 'false'.");
            }
        }
        Boolean sStatus = status != null ? Boolean.parseBoolean(status) : null;

        List<AccountDepositDetailsDto> depositDto = (sStatus == null) ? service.listAllAccountDepositsDetails():
        service.listByStatus(sStatus);

         return depositDto.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(depositDto);
    }
    
    @Operation(
        summary = "Filtrar límite de depósito de cuentas por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/limite-deposito/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<AccountDepositDetailsDto> depositDto = service.listByQuery(q);
            return depositDto.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(depositDto);
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
     /*
     * {
     * "amount": 45.00,
     * "status": true
     * }
     */
    @PostMapping("/limite-deposito")
    public ResponseEntity<?> postMethod(@Valid @RequestBody AccountDepositDetailsDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/limite-deposito/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody AccountDepositDetailsDto dto) {
        AccountDepositDetailsDto depositDto = this.service.findById(id);
        if (depositDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        depositDto.setAmount(dto.getAmount());
        this.service.save(depositDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/limite-deposito/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        AccountDepositDetailsDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
