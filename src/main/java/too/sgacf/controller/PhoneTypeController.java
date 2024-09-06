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
import too.sgacf.dto.PhoneTypeDto;
import too.sgacf.service.PhoneTypeService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipo de Teléfonos", description = "Controlador para tipo de teléfonos")
public class PhoneTypeController {
    @Autowired
    private PhoneTypeService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/tipo-telefonos - Retrieves all genres
     * GET /api/v1/tipo-telefonos?estado=value - Retrieves genres by estado
     */
    @GetMapping("/tipo-telefonos")
    @Operation(
        summary = "Listar Tipo de teléfonos",
        description = "Recupera todos los tipos de teléfonos o tipos de teléfonos por estado",
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

        List<PhoneTypeDto> generos = (sStatus == null) ? service.listAllphones() 
                : service.listByStatus(sStatus);

        return generos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(generos);
    }

    @Operation(
        summary = "Filtrar Tipos de teléfonos por palabra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra de filtrado"
                
            )
        }
    )
    @GetMapping("/tipo-telefonos/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        try {
            List<PhoneTypeDto> phonesType = service.listByQuery(q);
            return phonesType.isEmpty() ? 
                responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") : 
                ResponseEntity.status(HttpStatus.OK).body(phonesType);
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
    @PostMapping("/tipo-telefonos")
    public ResponseEntity<?> postMethod(@Valid @RequestBody PhoneTypeDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/tipo-telefonos/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody PhoneTypeDto dto) {
        PhoneTypeDto phone = this.service.findById(id);
        if (phone == null) {
            throw new EntityNotFoundException();
        }
        dto.setId(id);
        phone.setName(dto.getName());
        this.service.save(dto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/tipo-telefonos/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id) {
        PhoneTypeDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(id);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
