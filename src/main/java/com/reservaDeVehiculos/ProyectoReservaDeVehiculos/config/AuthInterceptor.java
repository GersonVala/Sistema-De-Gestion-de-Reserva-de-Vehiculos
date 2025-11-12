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
    private final com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.AuthorizationService authService;

    // URLs que requieren autenticación
    private static final List<String> PROTECTED_URLS = Arrays.asList(
        "/dashboard",
        "/profile",
        "/reservas",
        "/vendedor",
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
        "/vendor",
        "/error"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        HttpSession session = request.getSession(false);

        log.info("🔍 Interceptor - {} {} | Sesión existe: {}", method, requestURI, session != null);

        // Permitir recursos estáticos
        if (isStaticResource(requestURI)) {
            log.debug("✅ Recurso estático permitido: {}", requestURI);
            return true;
        }

        // Permitir URLs públicas
        if (isPublicUrl(requestURI)) {
            log.debug("✅ URL pública permitida: {}", requestURI);
            return true;
        }

        // Verificar si la URL requiere autenticación
        if (requiresAuthentication(requestURI)) {
            log.info("🔐 URL requiere autenticación: {}", requestURI);
            
            // Verificar si el usuario está logueado
            boolean isLoggedIn = sessionService.isUserLoggedIn(session);
            log.info("📋 Usuario logueado: {}", isLoggedIn);
            
            if (!isLoggedIn) {
                log.warn("❌ Acceso denegado a '{}' - Usuario no autenticado", requestURI);
                
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
                log.warn("⏰ Sesión expirada para usuario: {}", sessionService.getCurrentUserEmail(session));
                sessionService.destroyUserSession(session);
                
                response.sendRedirect("/login?error=Su sesión ha expirado. Por favor, inicie sesión nuevamente");
                return false;
            }

            // Validar permisos por rol para rutas protegidas
            if (!checkRoleAccess(requestURI, session, response)) {
                return false;
            }

            log.info("✅ Acceso autorizado a '{}' para usuario: {}", 
                     requestURI, sessionService.getCurrentUserEmail(session));
        }

        return true;
    }

    /**
     * Verificar acceso basado en roles para rutas específicas
     */
    private boolean checkRoleAccess(String requestURI, HttpSession session, HttpServletResponse response) throws Exception {
        // Rutas de vendedor - solo VENDEDOR o ADMIN
        if (requestURI.startsWith("/vendedor")) {
            if (!authService.isVendedor(session) && !authService.isAdministrador(session)) {
                log.warn("❌ Acceso denegado a '{}' - Usuario no es VENDEDOR ni ADMIN", requestURI);
                response.sendRedirect("/error/403?message=No tienes permisos para acceder a esta sección");
                return false;
            }
        }

        // Rutas de administrador - solo ADMIN
        if (requestURI.startsWith("/admin")) {
            if (!authService.isAdministrador(session)) {
                log.warn("❌ Acceso denegado a '{}' - Usuario no es ADMINISTRADOR", requestURI);
                response.sendRedirect("/error/403?message=Solo administradores pueden acceder a esta sección");
                return false;
            }
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