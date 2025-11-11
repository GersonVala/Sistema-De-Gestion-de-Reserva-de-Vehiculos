package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Usuario_rolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<Usuario_rolesEntity, Integer> {

    // Buscar roles de un usuario
    @Query("SELECT ur FROM Usuario_rolesEntity ur WHERE ur.usuario.id_usuario = :idUsuario")
    List<Usuario_rolesEntity> findByUsuarioId(@Param("idUsuario") Integer idUsuario);

    // Buscar usuarios con un rol específico
    @Query("SELECT ur FROM Usuario_rolesEntity ur WHERE ur.rol.id_rol = :idRol")
    List<Usuario_rolesEntity> findByRolId(@Param("idRol") Integer idRol);

    // Verificar si un usuario tiene un rol específico
    @Query("SELECT COUNT(ur) > 0 FROM Usuario_rolesEntity ur WHERE ur.usuario.id_usuario = :idUsuario AND ur.rol.id_rol = :idRol")
    boolean existsByUsuarioIdAndRolId(@Param("idUsuario") Integer idUsuario, @Param("idRol") Integer idRol);
}

