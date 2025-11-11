package com.reservaDeVehiculos.ProyectoReservaDeVehiculos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ProyectoReservaDeVehiculosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoReservaDeVehiculosApplication.class, args);
    }
}

/*
 * @SpringBootApplication
 * public class ProyectoReservaDeVehiculosApplication {
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(ProyectoReservaDeVehiculosApplication.class, args);
 * }
 * 
 * @Bean
 * public CommandLineRunner test(DataSource dataSource) {
 * return args -> {
 * try (var con = dataSource.getConnection()) {
 * System.out.println("\n✅ CONECTADO A: " + con.getCatalog() + "\n");
 * } catch (Exception e) {
 * System.out.println("\n❌ ERROR: " + e.getMessage() + "\n");
 * }
 * };
 * }
 */
