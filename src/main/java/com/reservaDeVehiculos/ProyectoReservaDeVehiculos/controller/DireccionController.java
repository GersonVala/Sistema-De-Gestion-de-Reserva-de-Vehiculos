package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearDireccionRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.DireccionResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<DireccionResponse> crear(@Valid @RequestBody CrearDireccionRequest request) {
        DireccionResponse direccion = direccionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(direccion);
    }

    @GetMapping
    public ResponseEntity<List<DireccionResponse>> listar() {
        List<DireccionResponse> direcciones = direccionService.obtenerTodas();
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponse> obtenerPorId(@PathVariable Integer id) {
        DireccionResponse direccion = direccionService.obtenerPorId(id);
        return ResponseEntity.ok(direccion);
    }

    @GetMapping("/ciudad/{idCiudad}")
    public ResponseEntity<List<DireccionResponse>> listarPorCiudad(@PathVariable Integer idCiudad) {
        List<DireccionResponse> direcciones = direccionService.obtenerPorCiudad(idCiudad);
        return ResponseEntity.ok(direcciones);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        direccionService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Dirección eliminada exitosamente", true));
    }
}

