package too.sgacf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import too.sgacf.model.GeneroModel;
import too.sgacf.service.GeneroService;
import too.sgacf.utilities.ResponseBuilderUtility;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    @Autowired
    private ResponseBuilderUtility responseBuilder;

    @GetMapping("/generos")
    public ResponseEntity<?> getMethod() {
        return ResponseEntity.status(HttpStatus.OK).body(this.generoService.list());
    }
    
    @PostMapping("/generos")
    public ResponseEntity<?> postMethod(@RequestBody GeneroModel model) {        
        try {
            this.generoService.save(new GeneroModel(
                model.getNombre()
            ));
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (Exception e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "ocurrió un error inesperado");

        }
    }

    @PutMapping("/generos/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @RequestBody GeneroModel model){
        GeneroModel data = this.generoService.findById(id);
        if (data == null) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Recurso no encontrado.");   
        }
        try {
            data.setNombre(model.getNombre());
            this.generoService.save(data);
            return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa");
        } catch (Exception e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Ocurrió un error inesperado.");
        }
    }
    
}
