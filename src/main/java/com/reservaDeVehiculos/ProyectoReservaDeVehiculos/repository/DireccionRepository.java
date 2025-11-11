package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.DireccionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<DireccionesEntity, Integer> {

    // Buscar direcciones por ciudad
    @Query("SELECT d FROM DireccionesEntity d WHERE d.ciudades.id_ciudad = :idCiudad")
    List<DireccionesEntity> findByCiudadId(@Param("idCiudad") Integer idCiudad);

    // Buscar direcciones por calle
    List<DireccionesEntity> findByCalle(String calle);

    // Buscar dirección específica por calle y número
    @Query("SELECT d FROM DireccionesEntity d WHERE d.calle = :calle AND d.numero_calle = :numeroCalle")
    List<DireccionesEntity> findByCalleAndNumero(@Param("calle") String calle, @Param("numeroCalle") Integer numeroCalle);
}


