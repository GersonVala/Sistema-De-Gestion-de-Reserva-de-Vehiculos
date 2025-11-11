package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.SucursalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalesEntity, Integer> {

    // Buscar sucursales por teléfono
    @Query("SELECT s FROM SucursalesEntity s WHERE s.telefono_sucursal = :telefono")
    List<SucursalesEntity> findByTelefono(@Param("telefono") String telefono);
}

