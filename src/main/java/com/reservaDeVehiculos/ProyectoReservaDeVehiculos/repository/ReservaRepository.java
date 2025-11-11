package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.ReservasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservasEntity, Integer> {

    // Buscar reservas por usuario
    List<ReservasEntity> findByUsuario_IdUsuario(Integer id_usuario);

    // Buscar reservas por estado
    List<ReservasEntity> findByEstado(EstadoReservaEnum estado);

    // Buscar reservas por sucursal
    List<ReservasEntity> findBySucursal_IdSucursal(Integer id_sucursal);

    // Buscar reservas por rango de fechas
    List<ReservasEntity> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar reservas por fecha de fin posterior a una fecha dada
    List<ReservasEntity> findByFechaFinAfter(LocalDate fecha);

    // Buscar reservas por usuario y estado
    List<ReservasEntity> findByUsuario_IdUsuarioAndEstado(Integer id_usuario, EstadoReservaEnum estado);
}

