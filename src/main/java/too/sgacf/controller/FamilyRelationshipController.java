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
import too.sgacf.dto.FamilyRelationshipDto;
import too.sgacf.service.FamilyRelationshipService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Parentezco Beneficiarios", description = "Controlador para parentezco beneficiarios")
public class FamilyRelationshipController {
    @Autowired
    private FamilyRelationshipService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/parentezcos - Retrieves all relationships
     * GET /api/v1/parentezcos?estado=value - Retrieves relationships by estado
     */
    @GetMapping("/parentezcos")
    @Operation(
        summary = "Listar Géneros",
        description = "Recupera todos los géneros o géneros por estado",
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
    public ResponseEntity<?> getMethod(@RequestParam(required = false) String status) {
        if (status != null) {
            if (status.isEmpty()) {
                return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Debe ingresar un estado.");
            }
            if (!status.equalsIgnoreCase("true") && !status.equalsIgnoreCase("false")) {
                return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "El estado debe ser 'true' o 'false'.");
            }
        }

        Boolean sStatus = status != null ? Boolean.parseBoolean(status) : null;

        List<FamilyRelationshipDto> relationships = (sStatus == null) ? service.listAllrelationships() 
                : service.listByStatus(sStatus);

        return relationships.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(relationships);
    }

    @Operation(
        summary = "Filtrar Géneros por palabra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra de filtrado"
                
            )
        }
    )
    @GetMapping("/parentezcos/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        try {
            List<FamilyRelationshipDto> relationships = service.listByQuery(q);
            return relationships.isEmpty() ? 
                responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") : 
                ResponseEntity.status(HttpStatus.OK).body(relationships);
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
    @PostMapping("/parentezcos")
    public ResponseEntity<?> postMethod(@Valid @RequestBody FamilyRelationshipDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/parentezcos/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody FamilyRelationshipDto dto) {
        FamilyRelationshipDto genre = this.service.findById(id);
        if (genre == null) {
            throw new EntityNotFoundException();
        }
        dto.setId(id);
        genre.setName(dto.getName());
        this.service.save(dto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/parentezcos/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id) {
        FamilyRelationshipDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(id);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
