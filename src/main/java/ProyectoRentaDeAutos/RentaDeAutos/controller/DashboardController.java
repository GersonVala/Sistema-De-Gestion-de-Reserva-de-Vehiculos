package ProyectoRentaDeAutos.RentaDeAutos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para redirigir al dashboard apropiado según el rol del usuario.
 */
@Controller
@Slf4j
public class DashboardController {

    /**
     * Redirige al dashboard correspondiente según el rol del usuario autenticado.
     * GET /dashboard
     */
    @GetMapping("/dashboard")
    public String redirigirDashboard(Authentication authentication) {
        log.debug("Usuario autenticado: {}", authentication.getName());

        // Obtener el rol del usuario
        String rol = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("");

        log.debug("Rol detectado: {}", rol);

        // Redirigir según el rol
        return switch (rol) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_VENDEDOR" -> "redirect:/vendedor/dashboard";
            case "ROLE_CLIENTE" -> "redirect:/cliente/dashboard";
            default -> {
                log.warn("Rol desconocido: {}", rol);
                yield "redirect:/auth/access-denied";
            }
        };
    }
}
