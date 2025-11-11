package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor

public class DireccionesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion", nullable = false)
    private Integer id_direccion;

    @Column(name = "calle", length = 40, nullable = false)
    private String calle;

    @Column(name = "numero_calle", nullable = false)
    private Integer numero_calle;

    @OneToOne
    @JoinColumn(name = "id_ciudad", referencedColumnName = "id_ciudad")
    private CiudadesEntity ciudades;

}
