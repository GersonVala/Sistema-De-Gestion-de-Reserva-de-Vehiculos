package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.ReservasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservasEntity, Integer> {

    // Buscar reservas por usuario
    @Query("SELECT r FROM ReservasEntity r WHERE r.usuario.id_usuario = :idUsuario")
    List<ReservasEntity> findByUsuarioId(@Param("idUsuario") Integer idUsuario);

    // Buscar reservas por estado
    List<ReservasEntity> findByEstado(EstadoReservaEnum estado);

    // Buscar reservas por sucursal
    @Query("SELECT r FROM ReservasEntity r WHERE r.sucursal.id_sucursal = :idSucursal")
    List<ReservasEntity> findBySucursalId(@Param("idSucursal") Integer idSucursal);

    // Buscar reservas por rango de fechas
    @Query("SELECT r FROM ReservasEntity r WHERE r.fecha_inicio BETWEEN :fechaInicio AND :fechaFin")
    List<ReservasEntity> findByFechaInicioBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    // Buscar reservas por fecha de fin posterior a una fecha dada
    @Query("SELECT r FROM ReservasEntity r WHERE r.fecha_fin > :fecha")
    List<ReservasEntity> findByFechaFinAfter(@Param("fecha") LocalDate fecha);

    // Buscar reservas por usuario y estado
    @Query("SELECT r FROM ReservasEntity r WHERE r.usuario.id_usuario = :idUsuario AND r.estado = :estado")
    List<ReservasEntity> findByUsuarioIdAndEstado(@Param("idUsuario") Integer idUsuario, @Param("estado") EstadoReservaEnum estado);
}

