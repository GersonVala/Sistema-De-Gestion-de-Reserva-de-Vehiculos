package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.RegistroUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.LoginRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.UsuarioResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.LoginResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoDuplicadoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.CredencialesInvalidasException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final DireccionRepository direccionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest request) {
        // Validar que no exista el email
        if (usuarioRepository.existsByEmailUsuario(request.getEmail_usuario())) {
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        // Validar que no exista el DNI
        if (usuarioRepository.existsByDniUsuario(request.getDni_usuario())) {
            throw new RecursoDuplicadoException("El DNI ya está registrado");
        }

        // Validar dirección si se proporcionó
        DireccionesEntity direccion = null;
        if (request.getId_direccion() != null) {
            direccion = direccionRepository.findById(request.getId_direccion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada"));
        }

        // Crear usuario
        UsuariosEntity usuario = new UsuariosEntity();
        usuario.setNombre_usuario(request.getNombre_usuario());
        usuario.setApellido_usuario(request.getApellido_usuario());
        usuario.setEmail_usuario(request.getEmail_usuario());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setDni_usuario(request.getDni_usuario());
        usuario.setTelefono_usuario(request.getTelefono_usuario());
        usuario.setDireccion(direccion);

        UsuariosEntity usuarioGuardado = usuarioRepository.save(usuario);

        // Asignar rol CLIENTE por defecto
        RolesEntity rolCliente = rolRepository.findByEstado(RolEnum.CLIENTE)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol CLIENTE no encontrado"));

        Usuario_rolesEntity usuarioRol = new Usuario_rolesEntity();
        usuarioRol.setUsuario(usuarioGuardado);
        usuarioRol.setRol(rolCliente);
        usuarioRolRepository.save(usuarioRol);

        return convertirAResponse(usuarioGuardado);
    }

    public LoginResponse login(LoginRequest request) {
        UsuariosEntity usuario = usuarioRepository.findByEmailUsuario(request.getEmail_usuario())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        String nombreCompleto = usuario.getNombre_usuario() + " " + usuario.getApellido_usuario();

        return new LoginResponse(
                usuario.getId_usuario(),
                nombreCompleto,
                usuario.getEmail_usuario(),
                "TOKEN_JWT_AQUI", // Implementar JWT después
                "Login exitoso"
        );
    }

    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Integer id) {
        UsuariosEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return convertirAResponse(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Integer id, ActualizarUsuarioRequest request) {
        UsuariosEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (request.getNombre_usuario() != null) {
            usuario.setNombre_usuario(request.getNombre_usuario());
        }
        if (request.getApellido_usuario() != null) {
            usuario.setApellido_usuario(request.getApellido_usuario());
        }
        if (request.getEmail_usuario() != null) {
            if (!request.getEmail_usuario().equals(usuario.getEmail_usuario())
                && usuarioRepository.existsByEmailUsuario(request.getEmail_usuario())) {
                throw new RecursoDuplicadoException("El email ya está registrado");
            }
            usuario.setEmail_usuario(request.getEmail_usuario());
        }
        if (request.getTelefono_usuario() != null) {
            usuario.setTelefono_usuario(request.getTelefono_usuario());
        }
        if (request.getId_direccion() != null) {
            DireccionesEntity direccion = direccionRepository.findById(request.getId_direccion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada"));
            usuario.setDireccion(direccion);
        }

        UsuariosEntity usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponse(usuarioActualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponse convertirAResponse(UsuariosEntity usuario) {
        List<String> roles = usuarioRolRepository.findByUsuario_IdUsuario(usuario.getId_usuario())
                .stream()
                .map(ur -> ur.getRol().getEstado().name())
                .collect(Collectors.toList());

        return new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre_usuario(),
                usuario.getApellido_usuario(),
                usuario.getEmail_usuario(),
                usuario.getDni_usuario(),
                usuario.getTelefono_usuario(),
                null, // DireccionResponse - implementar después
                roles
        );
    }
}

