package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoDeVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoDeVehiculo, Integer> {

    // Buscar tipo de vehículo por nombre
    Optional<TipoDeVehiculo> findByNombreVehiculo(String nombre_vehiculo);

    // Verificar si existe un tipo de vehículo con ese nombre
    boolean existsByNombreVehiculo(String nombre_vehiculo);
}

