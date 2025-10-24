package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.ProyectoReservaDeVehiculos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class ProyectoReservaDeVehiculosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoReservaDeVehiculosApplication.class, args);
	}

	@Bean
	public CommandLineRunner test(DataSource dataSource) {
		return args -> {
			try (var con = dataSource.getConnection()) {
				System.out.println("\n✅ CONECTADO A: " + con.getCatalog() + "\n");
			} catch (Exception e) {
				System.out.println("\n❌ ERROR: " + e.getMessage() + "\n");
			}
		};
	}
}