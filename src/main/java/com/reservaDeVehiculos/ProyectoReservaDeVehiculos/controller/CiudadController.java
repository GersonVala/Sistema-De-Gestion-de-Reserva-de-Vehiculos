package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearCiudadRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.CiudadResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.CiudadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    public ResponseEntity<CiudadResponse> crear(@Valid @RequestBody CrearCiudadRequest request) {
        CiudadResponse ciudad = ciudadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudad);
    }

    @GetMapping
    public ResponseEntity<List<CiudadResponse>> listar() {
        List<CiudadResponse> ciudades = ciudadService.obtenerTodas();
        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadResponse> obtenerPorId(@PathVariable Integer id) {
        CiudadResponse ciudad = ciudadService.obtenerPorId(id);
        return ResponseEntity.ok(ciudad);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CiudadResponse>> listarPorEstado(@PathVariable String estado) {
        List<CiudadResponse> ciudades = ciudadService.obtenerPorEstado(estado);
        return ResponseEntity.ok(ciudades);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        ciudadService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Ciudad eliminada exitosamente", true));
    }
}

