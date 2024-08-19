package too.sgacf.Error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
        NoHandlerFoundException ex,HttpServletRequest httpServletRequest
    ){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(404, "Recurso no disponible. ");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponseDto> handleInvalidArguments(MethodArgumentNotValidException exception) {
        StringBuilder errorMessage = new StringBuilder("Ocurrió un error inesperado. ");
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getDefaultMessage()).append(" ");
        });
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto(errorMessage.toString().trim());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(apiErrorResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ApiErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException exception) {
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto("Registro no encontrado. ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }
    
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto("Ocurrió un error inesperado. " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    public ResponseEntity<ApiErrorResponseDto> handleUnsupportedOperationException(UnsupportedOperationException exception) {
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto("El registro no sufrió cambios. ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto("Ocurrio un error inesperado: "+e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGenericException(Exception e) {
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto("Ocurrió un error inesperado.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }
}
