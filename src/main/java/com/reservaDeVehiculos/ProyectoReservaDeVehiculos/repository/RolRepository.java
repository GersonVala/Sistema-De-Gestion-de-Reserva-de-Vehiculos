package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolesEntity, Integer> {

    // Buscar rol por nombre
    Optional<RolesEntity> findByNombreRol(String nombre_rol);

    // Buscar rol por descripción
    Optional<RolesEntity> findByDescripcionRol(String descripcion_rol);
}
