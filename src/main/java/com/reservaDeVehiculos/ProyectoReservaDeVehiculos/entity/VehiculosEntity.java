package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@NoArgsConstructor
public class VehiculosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo", nullable = false)
    private Integer id_vehiculo;

    @Column(name = "marca", nullable = false, length = 30)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 30)
    private String modelo;

    @Column(name = "patente", nullable = false, length = 30, unique = true)
    private String patente;

    @Column(name = "color", nullable = false, length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoVehiculo estado;

    @Column(name = "cant_puertas")
    private Integer cant_puertas;

    // Relación Many-to-One: Muchos vehículos pueden tener el mismo motor
    @ManyToOne
    @JoinColumn(name = "id_motor", referencedColumnName = "id_motor", nullable = false)
    private Motor motor;

    // Relación Many-to-One: Muchos vehículos son del mismo tipo
    @ManyToOne
    @JoinColumn(name = "id_tipo_vehiculo", referencedColumnName = "id_tipo_vehiculo", nullable = false)
    private TipoDeVehiculo tipoDeVehiculo;

    // Relación Many-to-One: Muchos vehículos pertenecen a una sucursal
    @ManyToOne
    @JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal", nullable = false)
    private SucursalesEntity sucursal;
}
