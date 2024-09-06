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
import too.sgacf.dto.ProfessionDto;
import too.sgacf.service.ProfessionService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipo de Profesiones", description = "Controlador para tipo de profesiones")
public class ProfessionController {
    @Autowired
    private ProfessionService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/profesiones - Retrieves all genres
     * GET /api/v1/profesiones?estado=value - Retrieves genres by estado
     */
    @GetMapping("/profesiones")
    @Operation(
        summary = "Listar Tipo de Profesiones",
        description = "Recupera todos los tipos de profesiones o tipos de profesiones por estado",
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

        List<ProfessionDto> professions = (sStatus == null) ? service.listAllprofessions() 
                : service.listByStatus(sStatus);

        return professions.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(professions);
    }

    @Operation(
        summary = "Filtrar Tipos de profesiones por palabra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra de filtrado"
                
            )
        }
    )
    @GetMapping("/profesiones/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        try {
            List<ProfessionDto> professions = service.listByQuery(q);
            return professions.isEmpty() ? 
                responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") : 
                ResponseEntity.status(HttpStatus.OK).body(professions);
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
    @PostMapping("/profesiones")
    public ResponseEntity<?> postMethod(@Valid @RequestBody ProfessionDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/profesiones/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody ProfessionDto dto) {
        ProfessionDto profession = this.service.findById(id);
        if (profession == null) {
            throw new EntityNotFoundException();
        }
        dto.setId(id);
        profession.setName(dto.getName());
        this.service.save(dto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/tipo-telefonos/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id) {
        ProfessionDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(id);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
