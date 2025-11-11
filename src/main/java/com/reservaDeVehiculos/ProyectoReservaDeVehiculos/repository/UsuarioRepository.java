package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuariosEntity, Integer> {

    // Buscar usuario por email
    @Query("SELECT u FROM UsuariosEntity u WHERE u.email_usuario = :email")
    Optional<UsuariosEntity> findByEmail(@Param("email") String email);

    // Buscar usuario por DNI
    @Query("SELECT u FROM UsuariosEntity u WHERE u.dni_usuario = :dni")
    Optional<UsuariosEntity> findByDni(@Param("dni") String dni);

    // Verificar si existe un usuario con ese email
    @Query("SELECT COUNT(u) > 0 FROM UsuariosEntity u WHERE u.email_usuario = :email")
    boolean existsByEmail(@Param("email") String email);

    // Verificar si existe un usuario con ese DNI
    @Query("SELECT COUNT(u) > 0 FROM UsuariosEntity u WHERE u.dni_usuario = :dni")
    boolean existsByDni(@Param("dni") String dni);

    // Buscar usuarios por nombre y apellido
    @Query("SELECT u FROM UsuariosEntity u WHERE u.nombre_usuario = :nombre AND u.apellido_usuario = :apellido")
    Optional<UsuariosEntity> findByNombreAndApellido(@Param("nombre") String nombre, @Param("apellido") String apellido);
}

