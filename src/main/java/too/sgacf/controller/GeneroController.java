package too.sgacf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class GeneroController {

    @Autowired
    private GenreService genreService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    /*
     * GET /api/v1/generos - Retrieves all genres
     * GET /api/v1/generos?estado=value - Retrieves genres by estado
     */
    @GetMapping("/generos")
    public ResponseEntity<?> getMethod(@RequestParam(required = false) Boolean status) {
        List<GenreDto> generos = (status == null) ? genreService.listAllGeneros() : genreService.listByStatus(status);
        return generos.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.")
                : ResponseEntity.status(HttpStatus.OK).body(generos);
    }

    /*
     * GET /api/v1/generos/search?q=value - Retrieves all genres by query
     */
    @GetMapping("/generos/search")
    public ResponseEntity<?> getMethodQuery(@RequestParam String q) {
        List<GenreDto> genres = genreService.listByQuery(q);
        return  genres.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.")
        : ResponseEntity.status(HttpStatus.OK).body(this.genreService.listByQuery(q));
    }

    /*
     * {
     * "name": "Prueba 5",
     * "status": true
     * }
     */
    @PostMapping("/generos")
    public ResponseEntity<?> postMethod(@Valid @RequestBody GenreDto dto) {
        try {
            this.genreService.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (Exception e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Ocurrió un error inesperado.");
        }
    }

    @PutMapping("/generos/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody GenreDto dto) {
        GenreDto data = this.genreService.findById(id);
        if (data == null) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Recurso no encontrado.");
        }
        try {
            data.setName(dto.getName());
            this.genreService.save(data);
            return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
        } catch (Exception e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Ocurrió un error inesperado.");
        }
    }

    @DeleteMapping("generos/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id) {
        GenreDto data = this.genreService.findById(id);
        if (data == null) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Recurso no encontrado.");
        }
        try {
            this.genreService.delete(data.getId());
            return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
        } catch (Exception e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Ocurrió un error inesperado.");
        }
    }
}