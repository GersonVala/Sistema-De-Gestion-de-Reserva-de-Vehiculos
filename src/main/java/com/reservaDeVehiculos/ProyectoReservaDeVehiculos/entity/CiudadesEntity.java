package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ciudades")
@Getter
@Setter
@NoArgsConstructor

public class CiudadesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciudad", nullable = false)
    private Integer id_ciudad;

    @Column(name = "nombre_ciudad", length = 30, nullable = false)
    private String nombre_ciudad;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

}
