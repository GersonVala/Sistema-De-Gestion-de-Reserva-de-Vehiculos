package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Detalle_reservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReservaRepository extends JpaRepository<Detalle_reservaEntity, Integer> {

    // Buscar detalles por reserva
    List<Detalle_reservaEntity> findByReserva_IdReserva(Integer id_reserva);
}

