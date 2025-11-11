package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor

public class RolesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol", nullable = false)
    private Integer id_rol;

    @Enumerated(EnumType.STRING)
    private RolEnum estado;

    @Column(name = "descripcion_rol", length = 30, nullable = false)
    private String descripcion_rol;

    // 🔗 Relación UNO a MUCHOS con usuario_roles
    @OneToMany(mappedBy = "rol")
    private List<Usuario_rolesEntity> usuarioRoles;

}
