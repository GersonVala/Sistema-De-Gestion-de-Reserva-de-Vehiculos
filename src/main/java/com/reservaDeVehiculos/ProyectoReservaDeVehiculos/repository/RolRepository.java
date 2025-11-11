package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolesEntity, Integer> {

    // Buscar rol por nombre
    @Query("SELECT r FROM RolesEntity r WHERE r.nombre_rol = :nombre")
    Optional<RolesEntity> findByNombreRol(@Param("nombre") String nombre);

    // Buscar rol por descripción
    @Query("SELECT r FROM RolesEntity r WHERE r.descripcion_rol = :descripcion")
    Optional<RolesEntity> findByDescripcionRol(@Param("descripcion") String descripcion);
}
