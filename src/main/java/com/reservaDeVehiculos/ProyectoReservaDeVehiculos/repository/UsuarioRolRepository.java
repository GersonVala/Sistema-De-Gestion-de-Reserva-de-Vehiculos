package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Usuario_rolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<Usuario_rolesEntity, Integer> {

    // Buscar roles de un usuario
    List<Usuario_rolesEntity> findByUsuario_IdUsuario(Integer id_usuario);

    // Buscar usuarios con un rol específico
    List<Usuario_rolesEntity> findByRol_IdRol(Integer id_rol);

    // Verificar si un usuario tiene un rol específico
    boolean existsByUsuario_IdUsuarioAndRol_IdRol(Integer id_usuario, Integer id_rol);
}

