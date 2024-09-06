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
import too.sgacf.dto.MemberPaymentDto;
import too.sgacf.service.MemberPaymentService;
import too.sgacf.utilities.ResponseBuilderUtility;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Paso de asociado", description = "Controlador para el pago de asociado")
public class MemberPaymentController {
    @Autowired
    private MemberPaymentService service;

    @Autowired
    private ResponseBuilderUtility responseBuilder;
/*
     * GET /api/v1/pago-asociado - Retrieves all member payments
     * GET /api/v1/pago-asociado?status=value - Retrieves member payments by status
     */
    @Operation(
        summary = "Listar pagos de asociado",
        description = "Recupera todos los pagos de asociados o pagos de asociados por estado",
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
    @GetMapping("/pago-asociado")
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

        List<MemberPaymentDto> paymentDto = (sStatus == null) ? service.listAllMemberPayment():
        service.listByStatus(sStatus);

         return paymentDto.isEmpty() ? 
            responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No existen registros.") :
            ResponseEntity.status(HttpStatus.OK).body(paymentDto);
    }
    
    @Operation(
        summary = "Filtrar pagos de asociados por palabra/letra",
        parameters = {
            @Parameter(
                name = "q",
                description = "Palabra/letra de filtrado"
                
            )
        }
    )
    @GetMapping("/pago-asociado/search")
    public ResponseEntity<?> getMethodByQuery(@RequestParam String q) {
        try {
            List<MemberPaymentDto> paymentDto = service.listByQuery(q);
            return paymentDto.isEmpty() ? responseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron registros.") 
            : ResponseEntity.status(HttpStatus.OK).body(paymentDto);
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
     /*
     * {
     * "currentDate": "2024-09-05T17:50:00"
     * "amount": 45.00,
     * "paymentStatus": true,
     * "status": true
     * }
     */
    @PostMapping("/pago-asociado")
    public ResponseEntity<?> postMethod(@Valid @RequestBody MemberPaymentDto dto) {
        try {
            this.service.save(dto);
            return responseBuilder.buildResponse(HttpStatus.CREATED, "Se creó el registro de forma exitosa");
        } catch (IllegalArgumentException e) {
            return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/pago-asociado/{id}")
    public ResponseEntity<?> putMethod(@PathVariable("id") Long id, @Valid @RequestBody MemberPaymentDto dto) {
        MemberPaymentDto paymentDto = this.service.findById(id);
        if (paymentDto == null) {
            throw new EntityNotFoundException();
        }

        dto.setId(id);
        paymentDto.setPaymentStatus(dto.isPaymentStatus());
        this.service.save(paymentDto);
        return responseBuilder.buildResponse(HttpStatus.OK, "Se actualizó el registro de forma exitosa.");
    }

    @DeleteMapping("/pago-asociado/{id}")
    public ResponseEntity<?> deleteMethod(@PathVariable("id") Long id){
        MemberPaymentDto data = this.service.findById(id);
        if (data == null || !data.isStatus()) {
            throw new EntityNotFoundException();
        }
        this.service.delete(data.getId());
        return responseBuilder.buildResponse(HttpStatus.OK, "Se eliminó el registro de forma exitosa.");
    }
}
