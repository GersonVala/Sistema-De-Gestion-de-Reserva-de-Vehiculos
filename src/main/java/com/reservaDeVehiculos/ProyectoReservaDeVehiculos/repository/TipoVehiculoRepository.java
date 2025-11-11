package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoDeVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoDeVehiculo, Integer> {

    // Buscar tipo de vehículo por nombre
    @Query("SELECT t FROM TipoDeVehiculo t WHERE t.nombre_vehiculo = :nombre")
    Optional<TipoDeVehiculo> findByNombre(@Param("nombre") String nombre);

    // Verificar si existe un tipo de vehículo con ese nombre
    @Query("SELECT COUNT(t) > 0 FROM TipoDeVehiculo t WHERE t.nombre_vehiculo = :nombre")
    boolean existsByNombre(@Param("nombre") String nombre);
}

