package ProyectoRentaDeAutos.RentaDeAutos.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para prevenir que usuarios autenticados accedan a páginas de login/registro.
    * Si un usuario ya está autenticado, será redirigido al index ("/") si intenta acceder a:
    * - /auth/login
    * - /auth/register
    * 
 * SEGURIDAD: Previene confusión y potenciales vulnerabilidades de sesión.
 */
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        // Solo interceptar rutas de autenticación (login y registro)
        if (requestURI.equals("/auth/login") || requestURI.equals("/auth/register")) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Verificar si hay un usuario REAL autenticado (NO anónimo)
            // IMPORTANTE: Usamos AnonymousAuthenticationToken para detectar correctamente usuarios anónimos
            if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

                log.info("Usuario autenticado '{}' intentó acceder a {}. Redirigiendo al index (/)",
                         authentication.getName(), requestURI);

                response.sendRedirect(request.getContextPath() + "/");
                return false; // Bloquear procesamiento del request original
            }
        }

        // Permitir continuar con el request
        return true;
    }
}
