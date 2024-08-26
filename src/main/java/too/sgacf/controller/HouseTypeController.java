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
import too.sgacf.dto.HouseTypeDto;
import too.sgacf.service.HouseTypeService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Tipos de Vivienda", description = "Controlador para tipos de viviendas")
public class HouseTypeController {

    @Autowired
    private HouseTypeService houseTypeService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/tipos-viviendas - Retrieves all marital status
     * GET /api/v1/tipos-viviendas?status=value - Retrieves marital status by status
     */
    @Operation(
        summary = "Listar Tipos de Viviendas",
        description = "Recupera todos los tipos de viviendas, así como todos los tipos de viviendas por estado",
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
    @GetMapping("/tipos-viviendas")
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

        List<HouseTypeDto> housesDtos = (sStatus == null) ? houseTypeService.listAllHousesTypes():
        houseTypeService.listByStatus(sStatus);

         return housesDtos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(housesDtos);
    }
    
    @Operation(
        summary = "Filtrar tipos de viviendas por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/tipos-viviendas/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<HouseTypeDto> houseTypeDtos = houseTypeService.listByQuery(q);
            return houseTypeDtos.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(houseTypeDtos);
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
    @PostMapping("/tipos-viviendas")
    public ResponseEntity<?> postMethod(@Valid @RequestBody HouseTypeDto dto) {
        try {
            this.houseTypeService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/tipos-viviendas/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id,@Valid @RequestBody HouseTypeDto dto) {
        HouseTypeDto houseTypeDto = this.houseTypeService.findById(id);
        if (houseTypeDto == null || !houseTypeDto.isStatus()) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        houseTypeDto.setName(dto.getName());
        this.houseTypeService.save(houseTypeDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/tipos-viviendas/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        HouseTypeDto data = this.houseTypeService.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.houseTypeService.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
