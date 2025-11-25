package ProyectoRentaDeAutos.RentaDeAutos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC.
 *
 * Registra interceptores personalizados para manejo de peticiones HTTP.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @NonNull
    private final AuthenticationInterceptor authenticationInterceptor;

    public WebMvcConfig(@NonNull AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    /**
     * Registra el interceptor de autenticación para prevenir acceso a login/register
     * cuando el usuario ya está autenticado.
     *
     * SEGURIDAD: Este interceptor soluciona el problema de usuarios autenticados
     * que intentan volver a las páginas de login/registro usando el botón "Atrás".
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/auth/login", "/auth/register");
    }
}
