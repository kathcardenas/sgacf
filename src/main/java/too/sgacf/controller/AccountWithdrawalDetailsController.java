package too.sgacf.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import too.sgacf.dto.AccountWithdrawalDetailsDto;
import too.sgacf.service.AccountWithdrawalDetailsService;
import too.sgacf.utilities.ResponseBuilderUtility;

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


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Detalle retiro cuenta", description = "Controlador para los detalle de cuentas")
public class AccountWithdrawalDetailsController {
    @Autowired
    private AccountWithdrawalDetailsService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/limite-retiro - Retrieves all accounts withdrawal details
     * GET /api/v1/limite-retiro?status=value - Retrieves accounts withdrawal details by status
     */
    @Operation(
        summary = "Listar Límites retiro de cuentas",
        description = "Recupera todos los límites de retiso de cuentas o límite de retiro de cuentas por estado",
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
    @GetMapping("/limite-retiro")
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

        List<AccountWithdrawalDetailsDto> withdrawalDto = (sStatus == null) ? service.listAllAccountsWithdrawalDetails():
        service.listByStatus(sStatus);

         return withdrawalDto.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(withdrawalDto);
    }
    
    @Operation(
        summary = "Filtrar límite de retiro de cuentas por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/limite-retiro/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<AccountWithdrawalDetailsDto> withdrawalDto = service.listByQuery(q);
            return withdrawalDto.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(withdrawalDto);
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
     /*
     * {
     * "name": "Prueba",
     * "status": true
     * }
     */
    @PostMapping("/limite-retiro")
    public ResponseEntity<?> postMethod(@Valid @RequestBody AccountWithdrawalDetailsDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/limite-retiro/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody AccountWithdrawalDetailsDto dto) {
        AccountWithdrawalDetailsDto withdrawalDto = this.service.findById(id);
        if (withdrawalDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        withdrawalDto.setAmount(dto.getAmount());
        this.service.save(withdrawalDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/limite-retiro/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        AccountWithdrawalDetailsDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
