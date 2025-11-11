package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearSucursalRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.SucursalResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<SucursalResponse> crear(@Valid @RequestBody CrearSucursalRequest request) {
        SucursalResponse sucursal = sucursalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursal);
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listar() {
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> obtenerPorId(@PathVariable Integer id) {
        SucursalResponse sucursal = sucursalService.obtenerPorId(id);
        return ResponseEntity.ok(sucursal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Sucursal eliminada exitosamente", true));
    }
}

