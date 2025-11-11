package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipos_de_vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class TipoDeVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_vehiculo", nullable = false)
    private Integer id_tipo_vehiculo;
    @Column(name = "nombre_vehiculo", nullable = false, length = 30)
    private String nombre_vehiculo;
    @Column(name = "descripcion_vehiculo", nullable = false, length = 50)
    private String descripcion_vehiculo;

    // Relación One-to-Many: Un tipo de vehículo puede tener muchos vehículos
    @OneToMany(mappedBy = "tipoDeVehiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<VehiculosEntity> vehiculos = new ArrayList<>();
}
