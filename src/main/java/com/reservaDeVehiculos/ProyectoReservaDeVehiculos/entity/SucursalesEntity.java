package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@NoArgsConstructor
public class SucursalesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal", nullable = false)
    private Integer id_sucursal;

    @Column(name = "telefono_sucursal", length = 30, nullable = false)
    private String telefono_sucursal;

    // Relación con Direcciones (Muchas sucursales pueden tener diferentes direcciones)
    @ManyToOne
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion", nullable = false)
    private DireccionesEntity direccion;

    // Relación con Vendedor (Usuario con rol vendedor)
    @ManyToOne
    @JoinColumn(name = "id_vendedor", referencedColumnName = "id_usuario", nullable = false)
    private UsuariosEntity vendedor;

    // Relación One-to-Many: Una sucursal puede tener muchos vehículos
    @OneToMany(mappedBy = "sucursal", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<VehiculosEntity> vehiculos;

    // Relación One-to-Many: Una sucursal puede tener muchas reservas
    @OneToMany(mappedBy = "sucursal", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ReservasEntity> reservas;
}
