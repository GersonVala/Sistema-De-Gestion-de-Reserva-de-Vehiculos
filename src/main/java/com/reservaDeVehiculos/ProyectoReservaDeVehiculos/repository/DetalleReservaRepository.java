package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Detalle_reservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReservaRepository extends JpaRepository<Detalle_reservaEntity, Integer> {

    // Buscar detalles por reserva
    @Query("SELECT d FROM Detalle_reservaEntity d WHERE d.reserva.id_reserva = :idReserva")
    List<Detalle_reservaEntity> findByReservaId(@Param("idReserva") Integer idReserva);
}

