package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_reserva")
@Getter
@Setter
@NoArgsConstructor
public class Detalle_reservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle", nullable = false)
    private Integer id_detalle;

    @Column(name = "observaciones", length = 255, nullable = false)
    private String observaciones;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precio_unitario;

    // 🔗 FK: id_vehiculo → vehiculos(id_vehiculo)
    @ManyToOne
    @JoinColumn(name = "id_vehiculo", referencedColumnName = "id_vehiculo", nullable = false)
    private VehiculosEntity vehiculo;

    // 🔗 FK: id_reserva → reservas(id_reserva)
    @ManyToOne
    @JoinColumn(name = "id_reserva", referencedColumnName = "id_reserva", nullable = false)
    private ReservasEntity reserva;
}
