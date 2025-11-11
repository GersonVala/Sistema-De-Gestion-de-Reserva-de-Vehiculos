package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.CiudadesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<CiudadesEntity, Integer> {

    // Buscar ciudad por nombre (usando el nombre exacto del campo)
    @Query("SELECT c FROM CiudadesEntity c WHERE c.nombre_ciudad = :nombreCiudad")
    Optional<CiudadesEntity> findByNombreCiudad(@Param("nombreCiudad") String nombreCiudad);

    // Buscar ciudades por estado
    List<CiudadesEntity> findByEstado(String estado);

    // Verificar si existe una ciudad con ese nombre (usando el nombre exacto del campo)
    @Query("SELECT COUNT(c) > 0 FROM CiudadesEntity c WHERE c.nombre_ciudad = :nombreCiudad")
    boolean existsByNombreCiudad(@Param("nombreCiudad") String nombreCiudad);
}

