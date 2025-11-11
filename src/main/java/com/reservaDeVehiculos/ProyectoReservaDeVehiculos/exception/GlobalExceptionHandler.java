package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<MensajeResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MensajeResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<MensajeResponse> handleRecursoDuplicado(RecursoDuplicadoException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MensajeResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(VehiculoNoDisponibleException.class)
    public ResponseEntity<MensajeResponse> handleVehiculoNoDisponible(VehiculoNoDisponibleException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<MensajeResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeResponse> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeResponse("Error interno del servidor: " + ex.getMessage(), false));
    }
}

