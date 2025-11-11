package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearReservaRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarReservaRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.ReservaResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.VehiculoNoDisponibleException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final VehiculoService vehiculoService;

    @Transactional
    public ReservaResponse crear(CrearReservaRequest request) {
        // Validar usuario
        UsuariosEntity usuario = usuarioRepository.findById(request.getId_usuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Validar sucursal
        SucursalesEntity sucursal = sucursalRepository.findById(request.getId_sucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));

        // Validar fechas
        if (request.getFecha_fin().isBefore(request.getFecha_inicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Crear reserva
        ReservasEntity reserva = new ReservasEntity();
        reserva.setFecha_inicio(request.getFecha_inicio());
        reserva.setFecha_fin(request.getFecha_fin());
        reserva.setEstado(request.getEstado());
        reserva.setPrecio_reserva(request.getPrecio_reserva());
        reserva.setUsuario(usuario);
        reserva.setSucursal(sucursal);

        ReservasEntity reservaGuardada = reservaRepository.save(reserva);
        return convertirAResponse(reservaGuardada);
    }

    public List<ReservaResponse> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public ReservaResponse obtenerPorId(Integer id) {
        ReservasEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
        return convertirAResponse(reserva);
    }

    public List<ReservaResponse> obtenerPorUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuarioId(idUsuario).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaResponse> obtenerPorEstado(EstadoReservaEnum estado) {
        return reservaRepository.findByEstado(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaResponse actualizar(Integer id, ActualizarReservaRequest request) {
        ReservasEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));

        if (request.getFecha_inicio() != null) {
            reserva.setFecha_inicio(request.getFecha_inicio());
        }
        if (request.getFecha_fin() != null) {
            reserva.setFecha_fin(request.getFecha_fin());
        }
        if (request.getEstado() != null) {
            reserva.setEstado(request.getEstado());
        }
        if (request.getPrecio_reserva() != null) {
            reserva.setPrecio_reserva(request.getPrecio_reserva());
        }
        if (request.getId_sucursal() != null) {
            SucursalesEntity sucursal = sucursalRepository.findById(request.getId_sucursal())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));
            reserva.setSucursal(sucursal);
        }

        ReservasEntity reservaActualizada = reservaRepository.save(reserva);
        return convertirAResponse(reservaActualizada);
    }

    @Transactional
    public void cancelar(Integer id) {
        ReservasEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));

        reserva.setEstado(EstadoReservaEnum.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void confirmar(Integer id) {
        ReservasEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));

        reserva.setEstado(EstadoReservaEnum.CONFIRMADA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void completar(Integer id) {
        ReservasEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));

        reserva.setEstado(EstadoReservaEnum.COMPLETADA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }

    private ReservaResponse convertirAResponse(ReservasEntity reserva) {
        String nombreCompleto = reserva.getUsuario().getNombre_usuario() + " " +
                                reserva.getUsuario().getApellido_usuario();

        return new ReservaResponse(
                reserva.getId_reserva(),
                reserva.getFecha_inicio(),
                reserva.getFecha_fin(),
                reserva.getEstado(),
                reserva.getPrecio_reserva(),
                nombreCompleto,
                reserva.getUsuario().getEmail_usuario(),
                reserva.getSucursal().getTelefono_sucursal()
        );
    }
}

