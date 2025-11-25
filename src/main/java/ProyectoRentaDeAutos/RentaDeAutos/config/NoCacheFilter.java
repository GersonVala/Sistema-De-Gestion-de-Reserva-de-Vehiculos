package ProyectoRentaDeAutos.RentaDeAutos.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro HTTP para deshabilitar completamente el caché del navegador.
 * Aplica headers HTTP para evitar que las páginas sensibles sean almacenadas en caché.
 */
@Component
@Order(1) // Ejecutar antes que Spring Security Filter Chain
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Obtener la URI de la petición
        String requestURI = httpRequest.getRequestURI();

        // Aplicar headers anti-caché a TODAS las rutas de autenticación y páginas protegidas
        // Puedes personalizar esto según tus necesidades
        if (shouldDisableCache(requestURI)) {

            // ===== HEADERS HTTP PARA DESHABILITAR CACHÉ =====

            // HTTP 1.1 - Directiva principal para control de caché
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");

            // HTTP 1.0 - Compatibilidad con navegadores antiguos
            httpResponse.setHeader("Pragma", "no-cache");

            // Indica que el contenido ya expiró (fuerza re-validación)
            httpResponse.setDateHeader("Expires", 0);
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    /**
     * Determina si se deben aplicar headers anti-caché a esta ruta.
     *
     * RUTAS AFECTADAS:
     * - /auth/* (login, register, logout)
     * - /cliente/* (dashboard de clientes)
     * - /vendedor/* (dashboard de vendedores)
     * - /admin/* (dashboard de administradores)
     *
     * @param requestURI URI de la petición HTTP
     * @return true si se deben deshabilitar caché, false en caso contrario
     */
    private boolean shouldDisableCache(String requestURI) {
        // Aplicar a todas las rutas de autenticación y dashboards
        return requestURI.startsWith("/auth/")
            || requestURI.startsWith("/cliente/")
            || requestURI.startsWith("/vendedor/")
            || requestURI.startsWith("/admin/")
            || requestURI.equals("/")
            || requestURI.equals("/index");
    }
}
