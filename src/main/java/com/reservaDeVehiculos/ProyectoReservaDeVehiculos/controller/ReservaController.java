package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarReservaRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearReservaRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MensajeResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.ReservaResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody CrearReservaRequest request) {
        ReservaResponse reserva = reservaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        List<ReservaResponse> reservas = reservaService.obtenerTodas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Integer id) {
        ReservaResponse reserva = reservaService.obtenerPorId(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaResponse>> listarPorUsuario(@PathVariable Integer idUsuario) {
        List<ReservaResponse> reservas = reservaService.obtenerPorUsuario(idUsuario);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponse>> listarPorEstado(@PathVariable EstadoReservaEnum estado) {
        List<ReservaResponse> reservas = reservaService.obtenerPorEstado(estado);
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarReservaRequest request) {
        ReservaResponse reserva = reservaService.actualizar(id, request);
        return ResponseEntity.ok(reserva);
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<MensajeResponse> confirmar(@PathVariable Integer id) {
        reservaService.confirmar(id);
        return ResponseEntity.ok(new MensajeResponse("Reserva confirmada exitosamente", true));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<MensajeResponse> cancelar(@PathVariable Integer id) {
        reservaService.cancelar(id);
        return ResponseEntity.ok(new MensajeResponse("Reserva cancelada exitosamente", true));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<MensajeResponse> completar(@PathVariable Integer id) {
        reservaService.completar(id);
        return ResponseEntity.ok(new MensajeResponse("Reserva completada exitosamente", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Integer id) {
        reservaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Reserva eliminada exitosamente", true));
    }
}

