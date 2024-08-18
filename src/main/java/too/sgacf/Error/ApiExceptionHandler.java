package too.sgacf.Error;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(
        NoHandlerFoundException ex,HttpServletRequest httpServletRequest
    ){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(404, "Recurso no disponible");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(apiErrorResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponseDto> handleInvalidArguments(MethodArgumentNotValidException exception) {
        StringBuilder errorMessage = new StringBuilder("Ocurrió un error inesperado: ");
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getDefaultMessage()).append(" ");
        });
        ApiErrorResponseDto apiErrorResponse = new ApiErrorResponseDto(errorMessage.toString().trim());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(apiErrorResponse);
    }
}
