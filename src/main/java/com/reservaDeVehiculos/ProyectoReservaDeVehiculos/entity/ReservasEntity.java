package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor

public class ReservasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva", nullable = false)
    private Integer id_reserva;

    @Column(name = "fecha_inicio",columnDefinition = "DATETIME" ,nullable = false)
    private LocalDate fecha_inicio;

    @Column(name = "fecha_fin",columnDefinition = "DATETIME" ,nullable = false)
    private LocalDate fecha_fin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReservaEnum estado;

    @Column(name = "precio_reserva", nullable = false)
    private BigDecimal precio_reserva;

    // 🔗 FK: id_usuario → usuarios(id_usuario)
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private UsuariosEntity usuario;

    // 🔗 FK: id_sucursal → sucursales(id_sucursal)
    @ManyToOne
    @JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal", nullable = false)
    private SucursalesEntity sucursal;

    // 🔗 Relación UNO a MUCHOS con detalle_reserva
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    private List<Detalle_reservaEntity> detalles;

}
