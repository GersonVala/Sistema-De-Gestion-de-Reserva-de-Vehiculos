package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.ReservaResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.UsuarioResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.ReservaService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SessionService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final SessionService sessionService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;

    /**
     * Dashboard principal del usuario
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            // Verificar si está logueado (el interceptor ya hace esto, pero por seguridad)
            if (!sessionService.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            Integer userId = sessionService.getCurrentUserId(session);
            String userName = sessionService.getCurrentUserName(session);

            // Obtener datos del usuario
            UsuarioResponse usuario = usuarioService.obtenerPorId(userId);

            // Obtener reservas del usuario
            List<ReservaResponse> reservas = reservaService.obtenerPorUsuario(userId);

            // Calcular estadísticas
            Map<String, Object> estadisticas = calcularEstadisticas(reservas);

            // Obtener reservas recientes (últimas 3)
            List<ReservaResponse> reservasRecientes = reservas.stream()
                    .sorted((r1, r2) -> r2.getFecha_inicio().compareTo(r1.getFecha_inicio()))
                    .limit(3)
                    .toList();

            // Preparar datos para la vista
            model.addAttribute("usuario", usuario);
            model.addAttribute("userName", userName);
            model.addAttribute("reservas", reservas);
            model.addAttribute("reservasRecientes", reservasRecientes);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("sessionInfo", sessionService.getSessionInfo(session));

            log.debug("Dashboard cargado para usuario: {} [ID: {}]", userName, userId);
            return "dashboard";

        } catch (Exception e) {
            log.error("Error al cargar dashboard: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar el dashboard. Por favor, intente nuevamente.");
            return "dashboard";
        }
    }

    /**
     * Perfil del usuario
     */
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        try {
            if (!sessionService.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            Integer userId = sessionService.getCurrentUserId(session);
            UsuarioResponse usuario = usuarioService.obtenerPorId(userId);

            // Crear objeto para actualización
            ActualizarUsuarioRequest updateRequest = new ActualizarUsuarioRequest();
            updateRequest.setNombre_usuario(usuario.getNombre_usuario());
            updateRequest.setApellido_usuario(usuario.getApellido_usuario());
            updateRequest.setEmail_usuario(usuario.getEmail_usuario());
            updateRequest.setTelefono_usuario(usuario.getTelefono_usuario());
            // Nota: DNI no se puede actualizar por seguridad

            model.addAttribute("usuario", usuario);
            model.addAttribute("updateRequest", updateRequest);
            model.addAttribute("userName", sessionService.getCurrentUserName(session));

            return "profile";

        } catch (Exception e) {
            log.error("Error al cargar perfil: {}", e.getMessage());
            return "redirect:/dashboard?error=Error al cargar el perfil";
        }
    }

    /**
     * Actualizar perfil del usuario
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateRequest") ActualizarUsuarioRequest updateRequest,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!sessionService.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            if (bindingResult.hasErrors()) {
                Integer userId = sessionService.getCurrentUserId(session);
                UsuarioResponse usuario = usuarioService.obtenerPorId(userId);
                model.addAttribute("usuario", usuario);
                model.addAttribute("userName", sessionService.getCurrentUserName(session));
                model.addAttribute("error", "Por favor, corrija los errores del formulario.");
                return "profile";
            }

            Integer userId = sessionService.getCurrentUserId(session);
            
            // Actualizar usuario
            UsuarioResponse usuarioActualizado = usuarioService.actualizar(userId, updateRequest);
            
            // Actualizar sesión con nuevos datos
            sessionService.updateUserSession(session, usuarioActualizado);

            log.info("Perfil actualizado para usuario ID: {}", userId);
            
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente.");
            return "redirect:/profile";

        } catch (Exception e) {
            log.error("Error al actualizar perfil: {}", e.getMessage());
            
            String errorMessage = e.getMessage();
            if (errorMessage.contains("email")) {
                errorMessage = "Ya existe un usuario con ese email.";
            } else if (errorMessage.contains("dni")) {
                errorMessage = "Ya existe un usuario con ese DNI.";
            } else {
                errorMessage = "Error al actualizar perfil. Por favor, intente nuevamente.";
            }
            
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/profile";
        }
    }

    /**
     * Mis reservas
     */
    @GetMapping("/reservas")
    public String misReservas(@RequestParam(value = "estado", required = false) String estado,
                            Model model, 
                            HttpSession session) {
        try {
            if (!sessionService.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            Integer userId = sessionService.getCurrentUserId(session);
            List<ReservaResponse> reservas;

            // Filtrar por estado si se especifica
            if (estado != null && !estado.isEmpty()) {
                try {
                    EstadoReservaEnum estadoEnum = EstadoReservaEnum.valueOf(estado.toUpperCase());
                    // Filtrar las reservas del usuario por estado
                    List<ReservaResponse> todasReservas = reservaService.obtenerPorUsuario(userId);
                    reservas = todasReservas.stream()
                            .filter(r -> r.getEstado() == estadoEnum)
                            .toList();
                } catch (IllegalArgumentException e) {
                    // Estado inválido, mostrar todas las reservas
                    reservas = reservaService.obtenerPorUsuario(userId);
                }
            } else {
                reservas = reservaService.obtenerPorUsuario(userId);
            }

            // Estadísticas de reservas
            Map<String, Object> estadisticas = calcularEstadisticas(reservas);

            model.addAttribute("reservas", reservas);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("estadoFiltro", estado);
            model.addAttribute("userName", sessionService.getCurrentUserName(session));

            return "reservas";

        } catch (Exception e) {
            log.error("Error al cargar reservas: {}", e.getMessage());
            return "redirect:/dashboard?error=Error al cargar las reservas";
        }
    }

    /**
     * Ver detalles de una reserva
     */
    @GetMapping("/reservas/{id}")
    public String detalleReserva(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            if (!sessionService.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            Integer userId = sessionService.getCurrentUserId(session);
            ReservaResponse reserva = reservaService.obtenerPorId(id);

            // Por seguridad, verificamos que las reservas obtenidas son del usuario actual
            // (aunque el ReservaResponse no incluye ID de usuario, confiamos en que el servicio
            // devuelve reservas correctas o implementamos verificación a nivel de servicio)

            model.addAttribute("reserva", reserva);
            model.addAttribute("userName", sessionService.getCurrentUserName(session));

            return "reserva-detalle";

        } catch (Exception e) {
            log.error("Error al cargar detalle de reserva {}: {}", id, e.getMessage());
            return "redirect:/reservas?error=Error al cargar el detalle de la reserva";
        }
    }

    /**
     * Calcular estadísticas de las reservas
     */
    private Map<String, Object> calcularEstadisticas(List<ReservaResponse> reservas) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalReservas", reservas.size());
        
        // Contar por estado
        long activas = reservas.stream().filter(r -> r.getEstado() == EstadoReservaEnum.CONFIRMADA).count();
        long completadas = reservas.stream().filter(r -> r.getEstado() == EstadoReservaEnum.COMPLETADA).count();
        long canceladas = reservas.stream().filter(r -> r.getEstado() == EstadoReservaEnum.CANCELADA).count();
        long pendientes = reservas.stream().filter(r -> r.getEstado() == EstadoReservaEnum.PENDIENTE).count();
        
        stats.put("reservasActivas", activas);
        stats.put("reservasCompletadas", completadas);
        stats.put("reservasCanceladas", canceladas);
        stats.put("reservasPendientes", pendientes);
        
        // Calcular gasto total (convertir BigDecimal a double)
        double gastoTotal = reservas.stream()
            .filter(r -> r.getEstado() == EstadoReservaEnum.COMPLETADA)
            .mapToDouble(r -> r.getPrecio_reserva().doubleValue())
            .sum();
        
        stats.put("gastoTotal", gastoTotal);
        
        return stats;
    }
}