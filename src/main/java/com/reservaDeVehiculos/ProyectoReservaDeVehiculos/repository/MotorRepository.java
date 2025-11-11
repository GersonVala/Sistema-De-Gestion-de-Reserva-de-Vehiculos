package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Motor;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoCombustible;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoMotor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotorRepository extends JpaRepository<Motor, Integer> {

    // Buscar motores por tipo de combustible
    List<Motor> findByTipoCombustible(TipoCombustible tipoCombustible);

    // Buscar motores por tipo de motor
    List<Motor> findByTipoMotor(TipoMotor tipoMotor);

    // Buscar motores por cilindrada
    List<Motor> findByCilindrada(double cilindrada);

    // Buscar motores por caballos de fuerza
    List<Motor> findByCaballosDeFuerza(Integer caballos_de_fuerza);
}

