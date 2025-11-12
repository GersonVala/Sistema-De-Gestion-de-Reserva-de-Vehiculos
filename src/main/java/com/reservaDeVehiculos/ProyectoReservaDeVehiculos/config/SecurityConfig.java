package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 * 
 * IMPORTANTE: Esta clase solo proporciona BCryptPasswordEncoder para encriptación.
 * La autenticación y autorización se manejan con el sistema personalizado:
 * - AuthController: Maneja login/logout/register
 * - SessionService: Gestión de sesiones HTTP
 * - AuthInterceptor: Control de acceso a URLs protegidas
 * 
 * Spring Security está configurado en modo "permisivo" para no interferir.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean de BCryptPasswordEncoder para encriptar contraseñas
     * Este es el ÚNICO componente de Spring Security que usamos activamente
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir TODO el acceso - La autenticación la maneja AuthInterceptor
                .anyRequest().permitAll()
            )
            // DESHABILITAR formLogin de Spring Security completamente
            // La autenticación la maneja AuthController + SessionService + AuthInterceptor
            .formLogin(form -> form.disable())
            // DESHABILITAR httpBasic también
            .httpBasic(basic -> basic.disable())
            // DESHABILITAR logout de Spring Security
            .logout(logout -> logout.disable())
            // Deshabilitar frame options para permitir H2 Console
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

