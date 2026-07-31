
package ir.HomeServiceApplication.exception;

import ir.HomeServiceApplication.DTO.ErrorResponseDto;
import ir.HomeServiceApplication.DTO.InsufficientBalanceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateEmail(DuplicateEmailException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.CONFLICT.value(), ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotApprovedException.class)
    public ResponseEntity<ErrorResponseDto> handleNotApproved(NotApprovedException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOperation(InvalidOperationException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalance(InsufficientBalanceException ex) {
        if (ex.getChargeUrl() != null) {
            InsufficientBalanceResponseDto dto = new InsufficientBalanceResponseDto(
                    "Insufficient balance. Please charge your wallet.",
                    ex.getCurrentBalance(),
                    ex.getRequiredAmount(),
                    ex.getRequiredAmount() - ex.getCurrentBalance(),
                    ex.getChargeUrl()
            );
            return new ResponseEntity<>(dto, HttpStatus.PAYMENT_REQUIRED);
        }
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
