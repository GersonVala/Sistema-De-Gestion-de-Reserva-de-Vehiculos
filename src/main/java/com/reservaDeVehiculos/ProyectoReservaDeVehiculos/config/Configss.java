package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class Configss implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // Interceptar todas las rutas
                .excludePathPatterns(
                        "/static/**",
                        "/css/**", 
                        "/js/**", 
                        "/img/**",
                        "/vendor/**",
                        "/webjars/**",
                        "/error/**",
                        "/favicon.ico"
                );
    }
}
