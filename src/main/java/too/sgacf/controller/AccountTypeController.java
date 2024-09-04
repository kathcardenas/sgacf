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
import too.sgacf.dto.AccountTypeDto;
import too.sgacf.service.AccountTypeService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipo de Cuenta", description = "Controlador para los tipos de cuentas")
public class AccountTypeController {

    @Autowired
    private AccountTypeService accountTypeService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/tipos-cuentas - Retrieves all account type
     * GET /api/v1/tipos-cuentas?status=value - Retrieves account type by status
     */
    @Operation(
        summary = "Listar Tipo de Cuentas",
        description = "Recupera todos los tipos de cuentas o tipos de cuentas por estado",
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
    @GetMapping("/tipos-cuentas")
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

        List<AccountTypeDto> statusDtos = (sStatus == null) ? accountTypeService.listAllAccountTypes():
        accountTypeService.listByStatus(sStatus);

         return statusDtos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(statusDtos);
    }
    
    @Operation(
        summary = "Filtrar tipo de tipos de cuentas por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/tipos-cuentas/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<AccountTypeDto> accountTypeDto = accountTypeService.listByQuery(q);
            return accountTypeDto.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(accountTypeDto);
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
    @PostMapping("/tipos-cuentas")
    public ResponseEntity<?> postMethod(@Valid @RequestBody AccountTypeDto dto) {
        try {
            this.accountTypeService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/tipos-cuentas/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody AccountTypeDto dto) {
        AccountTypeDto accountTypeDto = this.accountTypeService.findById(id);
        if (accountTypeDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        accountTypeDto.setName(dto.getName());
        this.accountTypeService.save(accountTypeDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/tipos-cuentas/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        AccountTypeDto data = this.accountTypeService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.accountTypeService.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
