package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.ReservaResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.ForbiddenException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.AuthorizationService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.ReservaService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para operaciones de VENDEDOR.
 * Todos los endpoints requieren rol VENDEDOR.
 */
@Controller
@RequestMapping("/vendedor")
@RequiredArgsConstructor
@Slf4j
public class VendedorController {

    private final ReservaService reservaService;
    private final AuthorizationService authService;

    /**
     * Dashboard del vendedor - Vista principal
     * Muestra reservas pendientes de su sucursal
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Validar que sea vendedor
        if (!authService.isVendedor(session)) {
            log.warn("⚠️ Usuario sin rol VENDEDOR intentó acceder a dashboard vendedor");
            throw new ForbiddenException("No tienes permisos para acceder a esta sección");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        String vendedorNombre = (String) session.getAttribute(SessionService.SESSION_USER_NAME);
        
        // Obtener sucursal del vendedor
        Integer sucursalId = authService.getVendedorSucursal(vendedorId);
        if (sucursalId == null) {
            log.error("❌ Vendedor [ID: {}] no tiene sucursal asignada", vendedorId);
            model.addAttribute("error", "No tienes una sucursal asignada. Contacta al administrador.");
            return "error/403";
        }

        // Obtener reservas pendientes de la sucursal
        List<ReservaResponse> reservasPendientes = reservaService.obtenerPendientesPorSucursal(sucursalId);
        
        // Obtener reservas asignadas al vendedor (todos los estados)
        List<ReservaResponse> reservasAsignadas = reservaService.obtenerPorVendedor(vendedorId);
        
        // Calcular métricas
        long pendientes = reservasPendientes.size();
        long confirmadas = reservasAsignadas.stream()
                .filter(r -> r.getEstado() == EstadoReservaEnum.CONFIRMADA)
                .count();
        long enAlquiler = reservasAsignadas.stream()
                .filter(r -> r.getEstado() == EstadoReservaEnum.ALQUILADO)
                .count();
        long completadas = reservasAsignadas.stream()
                .filter(r -> r.getEstado() == EstadoReservaEnum.COMPLETADA)
                .count();

        model.addAttribute("vendedorNombre", vendedorNombre);
        model.addAttribute("sucursalId", sucursalId);
        model.addAttribute("reservasPendientes", reservasPendientes);
        model.addAttribute("reservasAsignadas", reservasAsignadas);
        model.addAttribute("metricaPendientes", pendientes);
        model.addAttribute("metricaConfirmadas", confirmadas);
        model.addAttribute("metricaEnAlquiler", enAlquiler);
        model.addAttribute("metricaCompletadas", completadas);

        log.info("✅ Vendedor [ID: {}] accedió a dashboard - Sucursal: {}", vendedorId, sucursalId);
        return "vendedor-dashboard";
    }

    /**
     * API: Listar reservas pendientes de la sucursal del vendedor
     */
    @GetMapping("/reservas/pendientes")
    @ResponseBody
    public ResponseEntity<List<ReservaResponse>> listarReservasPendientes(HttpSession session) {
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        Integer sucursalId = authService.getVendedorSucursal(vendedorId);
        
        if (sucursalId == null) {
            throw new ForbiddenException("No tienes una sucursal asignada");
        }

        List<ReservaResponse> reservas = reservaService.obtenerPendientesPorSucursal(sucursalId);
        log.info("✅ Vendedor [ID: {}] consultó {} reservas pendientes", vendedorId, reservas.size());
        
        return ResponseEntity.ok(reservas);
    }

    /**
     * API: Aprobar una reserva
     * Solo puede aprobar reservas PENDIENTES de su sucursal
     */
    @PostMapping("/reservas/{id}/aprobar")
    @ResponseBody
    public ResponseEntity<?> aprobarReserva(
            @PathVariable Integer id,
            HttpSession session) {
        
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        // Validar que el vendedor pueda gestionar esta reserva
        if (!authService.canVendedorManageReserva(session, id)) {
            log.warn("⚠️ Vendedor intentó aprobar reserva [ID: {}] sin permisos", id);
            throw new ForbiddenException("No tienes permisos para gestionar esta reserva");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        
        try {
            ReservaResponse reserva = reservaService.aprobarReserva(id, vendedorId);
            log.info("✅ Vendedor [ID: {}] aprobó reserva [ID: {}]", vendedorId, id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Reserva aprobada exitosamente",
                "reserva", reserva
            ));
        } catch (IllegalStateException e) {
            log.error("❌ Error al aprobar reserva [ID: {}]: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * API: Rechazar una reserva con motivo
     * Solo puede rechazar reservas PENDIENTES de su sucursal
     */
    @PostMapping("/reservas/{id}/rechazar")
    @ResponseBody
    public ResponseEntity<?> rechazarReserva(
            @PathVariable Integer id,
            @RequestParam(required = false) String motivo,
            HttpSession session) {
        
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        if (!authService.canVendedorManageReserva(session, id)) {
            log.warn("⚠️ Vendedor intentó rechazar reserva [ID: {}] sin permisos", id);
            throw new ForbiddenException("No tienes permisos para gestionar esta reserva");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        String motivoRechazo = (motivo != null && !motivo.isBlank()) 
                ? motivo 
                : "No especificado";
        
        try {
            ReservaResponse reserva = reservaService.rechazarReserva(id, vendedorId, motivoRechazo);
            log.info("✅ Vendedor [ID: {}] rechazó reserva [ID: {}] - Motivo: {}", 
                    vendedorId, id, motivoRechazo);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Reserva rechazada",
                "reserva", reserva
            ));
        } catch (IllegalStateException e) {
            log.error("❌ Error al rechazar reserva [ID: {}]: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * API: Iniciar alquiler (registro de retiro del vehículo)
     * Solo puede iniciar alquileres de reservas CONFIRMADAS asignadas a él
     */
    @PostMapping("/reservas/{id}/iniciar")
    @ResponseBody
    public ResponseEntity<?> iniciarAlquiler(
            @PathVariable Integer id,
            HttpSession session) {
        
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        if (!authService.canVendedorManageReserva(session, id)) {
            log.warn("⚠️ Vendedor intentó iniciar alquiler [ID: {}] sin permisos", id);
            throw new ForbiddenException("No tienes permisos para gestionar esta reserva");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        
        try {
            ReservaResponse reserva = reservaService.iniciarAlquiler(id, vendedorId);
            log.info("✅ Vendedor [ID: {}] inició alquiler [ID: {}]", vendedorId, id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Alquiler iniciado - Vehículo entregado al cliente",
                "reserva", reserva
            ));
        } catch (IllegalStateException e) {
            log.error("❌ Error al iniciar alquiler [ID: {}]: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * API: Completar alquiler (registro de devolución del vehículo)
     * Solo puede completar alquileres EN_ALQUILER asignados a él
     */
    @PostMapping("/reservas/{id}/completar")
    @ResponseBody
    public ResponseEntity<?> completarAlquiler(
            @PathVariable Integer id,
            HttpSession session) {
        
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        if (!authService.canVendedorManageReserva(session, id)) {
            log.warn("⚠️ Vendedor intentó completar alquiler [ID: {}] sin permisos", id);
            throw new ForbiddenException("No tienes permisos para gestionar esta reserva");
        }

        Integer vendedorId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        
        try {
            ReservaResponse reserva = reservaService.completarAlquiler(id, vendedorId);
            log.info("✅ Vendedor [ID: {}] completó alquiler [ID: {}]", vendedorId, id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Alquiler completado - Vehículo devuelto",
                "reserva", reserva
            ));
        } catch (IllegalStateException e) {
            log.error("❌ Error al completar alquiler [ID: {}]: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * API: Obtener detalle de una reserva
     * Solo puede ver reservas de su sucursal o asignadas a él
     */
    @GetMapping("/reservas/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalleReserva(
            @PathVariable Integer id,
            HttpSession session) {
        
        if (!authService.isVendedor(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        if (!authService.canVendedorManageReserva(session, id)) {
            log.warn("⚠️ Vendedor intentó ver reserva [ID: {}] sin permisos", id);
            throw new ForbiddenException("No tienes permisos para ver esta reserva");
        }

        try {
            ReservaResponse reserva = reservaService.obtenerPorId(id);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
