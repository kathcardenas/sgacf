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
import too.sgacf.dto.ReferenceTypeDto;
import too.sgacf.service.ReferenceTypeService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipo de Referencias", description = "Controlador para los tipos de referencias")
public class ReferenceTypeController {
    @Autowired
    private ReferenceTypeService referenceTypeService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/referencias - Retrieves all references type
     * GET /api/v1/referencias?status=value - Retrieves references type status by status
     */
    @Operation(
        summary = "Listar Tipo de Referencias",
        description = "Recupera todos los tipos de referencias o tipos de referencias por estado",
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
    @GetMapping("/referencias")
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

        List<ReferenceTypeDto> statusDtos = (sStatus == null) ? referenceTypeService.listAllReferencesStatus():
        referenceTypeService.listByStatus(sStatus);

         return statusDtos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(statusDtos);
    }
    
    @Operation(
        summary = "Filtrar tipo de referencias por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/referencias/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<ReferenceTypeDto> referenceStatusDtos = referenceTypeService.listByQuery(q);
            return referenceStatusDtos.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(referenceStatusDtos);
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
    @PostMapping("/referencias")
    public ResponseEntity<?> postMethod(@Valid @RequestBody ReferenceTypeDto dto) {
        try {
            this.referenceTypeService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/referencias/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody ReferenceTypeDto dto) {
        ReferenceTypeDto referenceStatusDto = this.referenceTypeService.findById(id);
        if (referenceStatusDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        referenceStatusDto.setName(dto.getName());
        this.referenceTypeService.save(referenceStatusDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/referencias/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        ReferenceTypeDto data = this.referenceTypeService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.referenceTypeService.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
    
}
