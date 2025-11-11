package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearMotorRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MotorResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.MotorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motores")
@RequiredArgsConstructor
public class MotorController {

    private final MotorService motorService;

    @PostMapping
    public ResponseEntity<MotorResponse> crear(@Valid @RequestBody CrearMotorRequest request) {
        MotorResponse motor = motorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(motor);
    }

    @GetMapping
    public ResponseEntity<List<MotorResponse>> listar() {
        List<MotorResponse> motores = motorService.obtenerTodos();
        return ResponseEntity.ok(motores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotorResponse> obtenerPorId(@PathVariable Integer id) {
        MotorResponse motor = motorService.obtenerPorId(id);
        return ResponseEntity.ok(motor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        motorService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Motor eliminado exitosamente", true));
    }
}

