package too.sgacf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import too.sgacf.dto.MaritalStatusDto;
import too.sgacf.service.MaritalStatusService;
import too.sgacf.utilities.ResponseBuilderUtility;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Estados Civiles", description = "Controlador para estados civiles")
public class MaritalStatusController {

    @Autowired
    private MaritalStatusService maritalStatusService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/estados-civiles - Retrieves all marital status
     * GET /api/v1/estados-civiles?status=value - Retrieves marital status by status
     */
    @Operation(
        summary = "Listar Estados Civiles",
        description = "Recupera todos los estados o estados civiles por estado",
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
    @GetMapping("/estados-civiles")
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

        List<MaritalStatusDto> statusDtos = (sStatus == null) ? maritalStatusService.listAllMaritalStatus():
        maritalStatusService.listByStatus(sStatus);

         return statusDtos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(statusDtos);
    }
    
    @Operation(
        summary = "Filtrar Estados civiles por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/estados-civiles/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<MaritalStatusDto> maritalStatusDtos = maritalStatusService.listByQuery(q);
            return maritalStatusDtos.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(maritalStatusDtos);
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
    @PostMapping("/estados-civiles")
    public ResponseEntity<?> postMethod(@Valid @RequestBody MaritalStatusDto dto) {
        try {
            this.maritalStatusService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("estados-civiles/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody MaritalStatusDto dto) {
        MaritalStatusDto maritalStatusDto = this.maritalStatusService.findById(id);
        if (maritalStatusDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        maritalStatusDto.setName(dto.getName());
        this.maritalStatusService.save(maritalStatusDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("estados-civiles/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        MaritalStatusDto data = this.maritalStatusService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.maritalStatusService.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
    
}
