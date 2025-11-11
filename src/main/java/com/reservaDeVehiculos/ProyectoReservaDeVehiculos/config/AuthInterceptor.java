package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    // URLs que requieren autenticación
    private static final List<String> PROTECTED_URLS = Arrays.asList(
        "/dashboard",
        "/profile",
        "/reservas",
        "/admin"
    );

    // URLs públicas (no requieren autenticación)
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/",
        "/login",
        "/register",
        "/logout",
        "/api/usuarios/login",
        "/api/usuarios/registro",
        "/static",
        "/css",
        "/js",
        "/img",
        "/vendor"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        HttpSession session = request.getSession(false);

        log.debug("Interceptando petición: {} {}", method, requestURI);

        // Permitir recursos estáticos
        if (isStaticResource(requestURI)) {
            return true;
        }

        // Permitir URLs públicas
        if (isPublicUrl(requestURI)) {
            return true;
        }

        // Verificar si la URL requiere autenticación
        if (requiresAuthentication(requestURI)) {
            
            // Verificar si el usuario está logueado
            if (!sessionService.isUserLoggedIn(session)) {
                log.warn("Acceso denegado a URL protegida '{}' - Usuario no autenticado", requestURI);
                
                // Guardar URL de destino para redirección post-login
                if (session != null) {
                    session.setAttribute("redirectAfterLogin", requestURI);
                }
                
                // Redirigir al login
                response.sendRedirect("/login?error=Se requiere iniciar sesión para acceder a esta página");
                return false;
            }

            // Usuario autenticado: actualizar última actividad
            sessionService.updateLastActivity(session);
            
            // Verificar si la sesión ha expirado
            if (sessionService.isSessionExpired(session)) {
                log.warn("Sesión expirada para usuario: {}", sessionService.getCurrentUserEmail(session));
                sessionService.destroyUserSession(session);
                
                response.sendRedirect("/login?error=Su sesión ha expirado. Por favor, inicie sesión nuevamente");
                return false;
            }

            log.debug("Acceso autorizado a '{}' para usuario: {}", 
                     requestURI, sessionService.getCurrentUserEmail(session));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Actualizar última actividad después de completar la petición
        HttpSession session = request.getSession(false);
        if (sessionService.isUserLoggedIn(session)) {
            sessionService.updateLastActivity(session);
        }
    }

    /**
     * Verificar si la URL requiere autenticación
     */
    private boolean requiresAuthentication(String requestURI) {
        return PROTECTED_URLS.stream().anyMatch(url -> 
            requestURI.startsWith(url) || requestURI.equals(url)
        );
    }

    /**
     * Verificar si es una URL pública
     */
    private boolean isPublicUrl(String requestURI) {
        return PUBLIC_URLS.stream().anyMatch(url -> 
            requestURI.startsWith(url) || requestURI.equals(url)
        );
    }

    /**
     * Verificar si es un recurso estático
     */
    private boolean isStaticResource(String requestURI) {
        return requestURI.matches(".+\\.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$") ||
               requestURI.startsWith("/static/") ||
               requestURI.startsWith("/css/") ||
               requestURI.startsWith("/js/") ||
               requestURI.startsWith("/img/") ||
               requestURI.startsWith("/vendor/") ||
               requestURI.startsWith("/webjars/");
    }
}