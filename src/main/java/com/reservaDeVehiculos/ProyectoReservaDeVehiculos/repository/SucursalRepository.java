package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.SucursalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalesEntity, Integer> {

    // Buscar sucursales por teléfono
    List<SucursalesEntity> findByTelefonoSucursal(String telefono_sucursal);
}

