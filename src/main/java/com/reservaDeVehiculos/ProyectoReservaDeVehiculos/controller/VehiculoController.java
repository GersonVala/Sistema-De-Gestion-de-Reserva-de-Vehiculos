package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.VehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoVehiculo;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody CrearVehiculoRequest request) {
        VehiculoResponse vehiculo = vehiculoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculo);
    }

    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listar() {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodos();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoResponse>> listarDisponibles() {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerDisponibles();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VehiculoResponse>> listarPorEstado(@PathVariable EstadoVehiculo estado) {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerPorEstado(estado);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> obtenerPorId(@PathVariable Integer id) {
        VehiculoResponse vehiculo = vehiculoService.obtenerPorId(id);
        return ResponseEntity.ok(vehiculo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarVehiculoRequest request) {
        VehiculoResponse vehiculo = vehiculoService.actualizar(id, request);
        return ResponseEntity.ok(vehiculo);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MensajeResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoVehiculo estado) {
        vehiculoService.cambiarEstado(id, estado);
        return ResponseEntity.ok(new MensajeResponse("Estado actualizado exitosamente", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Vehículo eliminado exitosamente", true));
    }
}

