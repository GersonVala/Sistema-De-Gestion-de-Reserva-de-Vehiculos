package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "motores")
@Getter
@Setter
@NoArgsConstructor
public class Motor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motor", nullable = false)
    private Integer id_motor;
    @Column(name = "cilindrada", columnDefinition = "Decimal(3,2)")
    private double cilindrada;
    @Column(name = "caballos_de_fuerza", nullable = false)
    private Integer caballos_de_fuerza;


    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", nullable = false, length = 30)
    private TipoCombustible tipoCombustible;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_motor", nullable = false, length = 30)
    private TipoMotor tipoMotor;

    // Relación One-to-Many: Un motor puede pertenecer a muchos vehículos
    @OneToMany(mappedBy = "motor", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<VehiculosEntity> vehiculos = new ArrayList<>();
}
