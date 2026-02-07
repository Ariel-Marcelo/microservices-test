package com.demo.trclientes.infrastructure;

import com.demo.trclientes.domain.dtos.generated.ApiResponseError;
import com.demo.trclientes.domain.exceptions.LowBalanceException;
import com.demo.trclientes.domain.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleNotFound(ResourceNotFoundException ex) {
        log.warn("WARN (404 Not Found): Recurso no encontrado. Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(LowBalanceException.class)
    public ResponseEntity<ApiResponseError> handleSaldoNoDisponible(LowBalanceException ex) {
        log.warn("WARN (400 Bad Request): Error de negocio (Saldo no disponible/Low Balance). Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("WARN (400 Bad Request): Fallo de validación de argumentos de entrada. Errores: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message("Error de validación: " + errors.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseError> handleGeneral(Exception ex) {
        log.error("FATAL ERROR (500 Internal Server Error): Error no controlado. Mensaje: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseError().status(false).message("Error interno del servidor: " + ex.getMessage()));
    }
}