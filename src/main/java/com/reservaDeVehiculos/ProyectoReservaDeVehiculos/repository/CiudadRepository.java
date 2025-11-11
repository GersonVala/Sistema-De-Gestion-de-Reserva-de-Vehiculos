package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.CiudadesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<CiudadesEntity, Integer> {

    // Buscar ciudad por nombre
    Optional<CiudadesEntity> findByNombreCiudad(String nombre_ciudad);

    // Buscar ciudades por estado
    List<CiudadesEntity> findByEstado(String estado);

    // Verificar si existe una ciudad con ese nombre
    boolean existsByNombreCiudad(String nombre_ciudad);
}

