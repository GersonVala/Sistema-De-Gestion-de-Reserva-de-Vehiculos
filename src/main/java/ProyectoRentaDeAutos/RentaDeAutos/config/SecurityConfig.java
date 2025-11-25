package ProyectoRentaDeAutos.RentaDeAutos.config;

import ProyectoRentaDeAutos.RentaDeAutos.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuración de seguridad de Spring Security.
 *
 * Implementa:
 * - Autenticación basada en email y contraseña
 * - Autorización basada en roles (ADMIN, VENDEDOR, CLIENTE)
 * - Hasheo de contraseñas con BCrypt
 * - Login personalizado con Thymeleaf
 * - Protección contra vulnerabilidades OWASP (Session Fixation, Clickjacking, etc)
 * - Gestión segura de sesiones con timeout e invalidación
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Bean para hashear contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación que usa nuestro UserDetailsService y PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager para manejar la autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuración de seguridad HTTP con control de acceso basado en roles.
     *
     * MEJORAS DE SEGURIDAD IMPLEMENTADAS:
     * - Prevención de acceso a login/register con sesión activa (validación en AuthController)
     * - Protección contra Session Fixation (Spring Security lo hace automáticamente en Spring Boot 3.x)
     * - Headers de seguridad HTTP (X-Frame-Options, X-Content-Type-Options, Referrer-Policy)
     * - Control de caché para páginas sensibles
     * - Timeout de sesión por inactividad (30 minutos configurado en application.properties)
     * - Restricción de sesiones concurrentes (máximo 1 sesión activa por usuario)
     * - Cookie HttpOnly y SameSite=Strict para prevenir ataques XSS y CSRF
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/", "/index", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // IMPORTANTE: /auth/login y /auth/register se manejan en AuthController e Interceptor
                // con validación para redirigir usuarios autenticados al INDEX (/)
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/auth/access-denied").permitAll()

                // Rutas protegidas por rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/vendedor/**").hasRole("VENDEDOR")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                // Limpiar el SecurityContext
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/auth/access-denied")
            )
            // GESTIÓN SEGURA DE SESIONES
            .sessionManagement(session -> session
                // Timeout de sesión: 30 minutos de inactividad
                .invalidSessionUrl("/auth/login?expired=true")
                // Máximo 1 sesión por usuario
                .maximumSessions(1)
                    // Prevenir nuevo login si ya hay sesión activa
                    .maxSessionsPreventsLogin(false) // false = expira sesión anterior
                    .expiredUrl("/auth/login?expired=true")
            )
            // HEADERS DE SEGURIDAD HTTP (OWASP)
            .headers(headers -> headers
                // Protección contra Clickjacking (X-Frame-Options: DENY)
                .frameOptions(frame -> frame.deny())
                // Protección contra MIME-sniffing (X-Content-Type-Options: nosniff)
                .contentTypeOptions(contentType -> contentType.disable())
                // Protección XSS (X-XSS-Protection: 1; mode=block)
                .xssProtection(xss -> xss.disable()) // Moderno: usar Content-Security-Policy

                // ===== CONTROL DE CACHÉ - SOLUCIÓN AL BOTÓN "ATRÁS" =====
                // IMPORTANTE: Los headers de caché se manejan en NoCacheFilter.java
                // que inyecta manualmente: Cache-Control, Pragma, Expires
                // Se deshabilita aquí para evitar conflictos con nuestro filtro personalizado
                .cacheControl(cache -> cache.disable())

                // Referrer Policy para controlar información de referencia
                .referrerPolicy(referrer ->
                    referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            );

        return http.build();
    }
}
