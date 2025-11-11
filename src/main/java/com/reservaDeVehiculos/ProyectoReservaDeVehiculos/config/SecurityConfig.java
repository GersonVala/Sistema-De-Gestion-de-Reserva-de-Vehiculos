package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**", "/favicon.ico").permitAll()
                // Permitir acceso público a las vistas principales (home y reservas)
                .requestMatchers("/", "/reservas", "/oficinas").permitAll()
                // Permitir acceso a la consola H2 (solo para desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                // Permitir acceso público a la API REST
                .requestMatchers("/api/**").permitAll()
                // Permitir acceso público al login y registro
                .requestMatchers("/login", "/register", "/logout").permitAll()
                // Todas las demás rutas requieren autenticación (dashboard, admin, etc.)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Deshabilitar frame options para permitir H2 Console
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

