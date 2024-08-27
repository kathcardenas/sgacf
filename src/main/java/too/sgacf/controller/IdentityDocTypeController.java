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
import too.sgacf.dto.IdentityDocTypeDto;
import too.sgacf.service.IdentityDocTypeService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipos de documentos de identidad", description = "Controlador para tipos de documentos de identidad")
public class IdentityDocTypeController {
    @Autowired
    private IdentityDocTypeService identityDocTypeService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/tipos-id - Retrieves all marital status
     * GET /api/v1/tipos-id?status=value - Retrieves marital status by status
     */
    @Operation(
        summary = "Listar Tipos de Documentos de Identidad",
        description = "Recupera todos los tipos de id o todos los tipos de id por estado",
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
    @GetMapping("/tipos-id")
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

        List<IdentityDocTypeDto> identityDocTypeDtos = (sStatus == null) ? identityDocTypeService.listAllIDType():
        identityDocTypeService.listByStatus(sStatus);

         return identityDocTypeDtos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(identityDocTypeDtos);
    }
    
    @Operation(
        summary = "Filtrar tipos de documentos de identidad por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/tipos-id/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<IdentityDocTypeDto> identityDocTypeDtos = identityDocTypeService.listByQuery(q);
            return identityDocTypeDtos.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(identityDocTypeDtos);
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
    @PostMapping("/tipos-id")
    public ResponseEntity<?> postMethod(@Valid @RequestBody IdentityDocTypeDto dto) {
        try {
            this.identityDocTypeService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/tipos-id/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody IdentityDocTypeDto dto) {
        IdentityDocTypeDto identityDocTypeDto = this.identityDocTypeService.findById(id);
        if (identityDocTypeDto == null || !identityDocTypeDto.isStatus()) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        identityDocTypeDto.setName(dto.getName());
        this.identityDocTypeService.save(identityDocTypeDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/tipos-id/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        IdentityDocTypeDto data = this.identityDocTypeService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.identityDocTypeService.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
