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
import too.sgacf.dto.GenreDto;
import too.sgacf.service.GenreService;
import too.sgacf.utilities.ResponseBuilderUtility;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Generos", description = "Controlador para géneros")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/generos - Retrieves all genres
     * GET /api/v1/generos?estado=value - Retrieves genres by estado
     */
    @GetMapping("/generos")
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

        List<GenreDto> generos = (sStatus == null) ? genreService.listAllGeneros() 
                : genreService.listByStatus(sStatus);

        return generos.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(generos);
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
    @GetMapping("/generos/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        try {
            List<GenreDto> genres = genreService.listByQuery(q);
            return genres.isEmpty() ? 
                responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") : 
                ResponseEntity.status(HttpStatus.OK).body(genres);
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
    @PostMapping("/generos")
    public ResponseEntity<?> postMethod(@Valid @RequestBody GenreDto dto) {
        try {
            this.genreService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/generos/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody GenreDto dto) {
        GenreDto genre = this.genreService.findById(id);
        if (genre == null) {
            throw new EntityNotFoundException();
        }
        dto.setId(id);
        genre.setName(dto.getName());
        this.genreService.save(dto);
        return ResponseEntity.status(HttpStatus.OK).body("Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/generos/{id}")
    public ResponseEntity<String> deleteMethod(@PathVariable("id") Long id) {
        GenreDto data = this.genreService.findById(id);
        if (data == null) {
            throw new EntityNotFoundException();
        }

        this.genreService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Se eliminó el registro de forma exitosa.");
    }
}