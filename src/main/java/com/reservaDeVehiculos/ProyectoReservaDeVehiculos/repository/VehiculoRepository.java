package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoVehiculo;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.VehiculosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<VehiculosEntity, Integer> {

    // Buscar vehículos por estado
    List<VehiculosEntity> findByEstado(EstadoVehiculo estado);

    // Buscar vehículo por patente (única)
    Optional<VehiculosEntity> findByPatente(String patente);

    // Buscar vehículos por marca
    List<VehiculosEntity> findByMarca(String marca);

    // Buscar vehículos por modelo
    List<VehiculosEntity> findByModelo(String modelo);

    // Buscar vehículos por tipo
    List<VehiculosEntity> findByTipoDeVehiculo_IdTipoVehiculo(Integer id_tipo_vehiculo);

    // Verificar si existe un vehículo con esa patente
    boolean existsByPatente(String patente);
}

