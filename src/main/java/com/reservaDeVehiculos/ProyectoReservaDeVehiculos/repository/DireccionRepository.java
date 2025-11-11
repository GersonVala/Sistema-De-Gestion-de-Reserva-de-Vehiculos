package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.DireccionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<DireccionesEntity, Integer> {

    // Buscar direcciones por ciudad
    List<DireccionesEntity> findByCiudades_IdCiudad(Integer id_ciudad);

    // Buscar direcciones por calle
    List<DireccionesEntity> findByCalle(String calle);

    // Buscar dirección específica por calle y número
    List<DireccionesEntity> findByCalleAndNumeroCalle(String calle, Integer numero_calle);
}


