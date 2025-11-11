package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearTipoVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.TipoVehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.TipoVehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-vehiculo")
@RequiredArgsConstructor
public class TipoVehiculoController {

    private final TipoVehiculoService tipoVehiculoService;

    @PostMapping
    public ResponseEntity<TipoVehiculoResponse> crear(@Valid @RequestBody CrearTipoVehiculoRequest request) {
        TipoVehiculoResponse tipoVehiculo = tipoVehiculoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoVehiculo);
    }

    @GetMapping
    public ResponseEntity<List<TipoVehiculoResponse>> listar() {
        List<TipoVehiculoResponse> tipos = tipoVehiculoService.obtenerTodos();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculoResponse> obtenerPorId(@PathVariable Integer id) {
        TipoVehiculoResponse tipoVehiculo = tipoVehiculoService.obtenerPorId(id);
        return ResponseEntity.ok(tipoVehiculo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        tipoVehiculoService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Tipo de vehículo eliminado exitosamente", true));
    }
}

