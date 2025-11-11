package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuariosEntity, Integer> {

    // Buscar usuario por email
    Optional<UsuariosEntity> findByEmailUsuario(String email_usuario);

    // Buscar usuario por DNI
    Optional<UsuariosEntity> findByDniUsuario(String dni_usuario);

    // Verificar si existe un usuario con ese email
    boolean existsByEmailUsuario(String email_usuario);

    // Verificar si existe un usuario con ese DNI
    boolean existsByDniUsuario(String dni_usuario);

    // Buscar usuarios por nombre
    Optional<UsuariosEntity> findByNombreUsuarioAndApellidoUsuario(String nombre_usuario, String apellido_usuario);
}

