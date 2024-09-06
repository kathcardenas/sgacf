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
import too.sgacf.dto.FieldDto;
import too.sgacf.service.FieldService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Rubros", description = "Controlador para rubros")
public class FieldController {
    @Autowired
    private FieldService fieldService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/rubros - Retrieves all fields
     * GET /api/v1/rubros?estado=value - Retrieves fields by estado
     */
    @GetMapping("/rubros")
    @Operation(
        summary = "Listar Rubros",
        description = "Recupera todos los rubros o rubros por estado",
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

        List<FieldDto> rubros = (sStatus == null) ? fieldService.listAllFields() 
                : fieldService.listByStatus(sStatus);

        return rubros.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(rubros);
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
    @GetMapping("/rubros/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        try {
            List<FieldDto> fields = fieldService.listByQuery(q);
            return fields.isEmpty() ? 
                responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") : 
                ResponseEntity.status(HttpStatus.OK).body(fields);
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
    @PostMapping("/rubros")
    public ResponseEntity<?> postMethod(@Valid @RequestBody FieldDto dto) {
        try {
            this.fieldService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/rubros/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody FieldDto dto) {
        FieldDto field = this.fieldService.findById(id);
        if (field == null) {
            throw new EntityNotFoundException();
        }
        dto.setId(id);
        field.setName(dto.getName());
        this.fieldService.save(dto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/rubros/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id) {
        FieldDto data = this.fieldService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.fieldService.delete(id);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
