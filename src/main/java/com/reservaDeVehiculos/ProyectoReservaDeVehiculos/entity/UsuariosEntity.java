package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor

public class UsuariosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id_usuario;

    @Column(name = "nombre_usuario", length = 30, nullable = false)
    private String nombre_usuario;

    @Column(name = "apellido_usuario", length = 30, nullable = false)
    private String apellido_usuario;

    @Column(name = "email_usuario", length = 100, nullable = false)
    private String email_usuario;

    @Column(name = "contraseña", length = 60, nullable = false)
    private String contrasena;

    @Column(name = "dni_usuario", length = 30, nullable = false)
    private String dni_usuario;

    @Column(name = "telefono_usuario", length = 20, nullable = false)
    private String telefono_usuario;

    @OneToOne
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private DireccionesEntity direccion;

    // 🔗 Relación MUCHOS a UNO con sucursales (para vendedores asignados a sucursal)
    @ManyToOne
    @JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal")
    private SucursalesEntity sucursal;

    // 🔗 Relación UNO a MUCHOS con usuario_roles
    @OneToMany(mappedBy = "usuario")
    private List<Usuario_rolesEntity> usuarioRoles;

}
