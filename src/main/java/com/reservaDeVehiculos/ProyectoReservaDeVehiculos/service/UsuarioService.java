package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.RegistroUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.LoginRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CreateVendedorRequest;
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
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest request) {
        // Limpiar email y DNI de espacios en blanco
        String emailLimpio = request.getEmail_usuario().trim().toLowerCase();
        String dniLimpio = request.getDni_usuario().trim();
        
        // DEBUG: Log del email que se está intentando registrar
        System.out.println("=== DEBUG REGISTRO ===");
        System.out.println("Email original: [" + request.getEmail_usuario() + "]");
        System.out.println("Email limpio: [" + emailLimpio + "]");
        System.out.println("DNI a registrar: [" + dniLimpio + "]");
        
        // Validar que no exista el email
        boolean emailExiste = usuarioRepository.existsByEmail(emailLimpio);
        System.out.println("¿Email existe en BD? " + emailExiste);
        
        if (emailExiste) {
            // Buscar el usuario con ese email para debug
            usuarioRepository.findByEmail(emailLimpio).ifPresent(u -> {
                System.out.println("Usuario encontrado: " + u.getNombre_usuario() + " " + u.getApellido_usuario());
            });
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        // Validar que no exista el DNI
        boolean dniExiste = usuarioRepository.existsByDni(dniLimpio);
        System.out.println("¿DNI existe en BD? " + dniExiste);
        
        if (dniExiste) {
            throw new RecursoDuplicadoException("El DNI ya está registrado");
        }
        
        System.out.println("Validaciones pasadas, creando usuario...");
        
        // VERIFICAR ROLES PRIMERO
        System.out.println("Verificando rol CLIENTE...");
        RolesEntity rolCliente = rolRepository.findByNombreRol(RolEnum.CLIENTE.name())
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR CRÍTICO: Rol CLIENTE no encontrado en BD");
                    System.out.println("Los roles disponibles son:");
                    rolRepository.findAll().forEach(r -> 
                        System.out.println("  - " + r.getNombre_rol()));
                    return new RecursoNoEncontradoException("Rol CLIENTE no encontrado");
                });
        System.out.println("✅ Rol CLIENTE encontrado: ID=" + rolCliente.getId_rol());
        System.out.println("======================");

        // Validar dirección si se proporcionó
        DireccionesEntity direccion = null;
        if (request.getId_direccion() != null) {
            direccion = direccionRepository.findById(request.getId_direccion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada"));
        }

        // Crear usuario
        String passwordEncriptada = passwordEncoder.encode(request.getContrasena());
        System.out.println("Contraseña original (primeros 5 chars): [" + request.getContrasena().substring(0, Math.min(5, request.getContrasena().length())) + "...]");
        System.out.println("Contraseña encriptada (primeros 10 chars): [" + passwordEncriptada.substring(0, 10) + "...]");
        System.out.println("Longitud contraseña encriptada: " + passwordEncriptada.length());
        
        UsuariosEntity usuario = new UsuariosEntity();
        usuario.setNombre_usuario(request.getNombre_usuario().trim());
        usuario.setApellido_usuario(request.getApellido_usuario().trim());
        usuario.setEmail_usuario(emailLimpio);
        usuario.setContrasena(passwordEncriptada);
        usuario.setDni_usuario(dniLimpio);
        usuario.setTelefono_usuario(request.getTelefono_usuario().trim());
        usuario.setDireccion(direccion);

        System.out.println("Guardando usuario en BD...");
        UsuariosEntity usuarioGuardado = usuarioRepository.save(usuario);
        System.out.println("✅ Usuario guardado con ID: " + usuarioGuardado.getId_usuario());
        System.out.println("   Email guardado: [" + usuarioGuardado.getEmail_usuario() + "]");
        System.out.println("   Contraseña en BD (primeros 10 chars): [" + usuarioGuardado.getContrasena().substring(0, 10) + "...]");

        // Asignar rol CLIENTE (ya verificado arriba)
        System.out.println("Asignando rol al usuario...");
        Usuario_rolesEntity usuarioRol = new Usuario_rolesEntity();
        usuarioRol.setUsuario(usuarioGuardado);
        usuarioRol.setRol(rolCliente);
        usuarioRolRepository.save(usuarioRol);
        System.out.println("✅ Rol " + rolCliente.getNombre_rol() + " asignado correctamente");
        System.out.println("======================");

        return convertirAResponse(usuarioGuardado);
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email recibido: [" + request.getEmail_usuario() + "]");
        
        String emailLimpio = request.getEmail_usuario().trim().toLowerCase();
        System.out.println("Email limpio: [" + emailLimpio + "]");
        System.out.println("Contraseña recibida (primeros 5 chars): [" + request.getContrasena().substring(0, Math.min(5, request.getContrasena().length())) + "...]");
        
        UsuariosEntity usuario = usuarioRepository.findByEmail(emailLimpio)
                .orElseThrow(() -> {
                    System.out.println("❌ Usuario NO encontrado en BD con email: " + emailLimpio);
                    return new CredencialesInvalidasException("Credenciales inválidas");
                });
        
        System.out.println("✅ Usuario encontrado: " + usuario.getNombre_usuario() + " " + usuario.getApellido_usuario());
        System.out.println("Contraseña hash en BD (primeros 10 chars): [" + usuario.getContrasena().substring(0, 10) + "...]");
        
        boolean passwordMatch = passwordEncoder.matches(request.getContrasena(), usuario.getContrasena());
        System.out.println("¿Contraseña coincide? " + passwordMatch);
        
        if (!passwordMatch) {
            System.out.println("❌ Contraseña incorrecta");
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }
        
        System.out.println("✅ LOGIN EXITOSO");
        
        // Obtener roles del usuario
        List<String> roles = usuarioRolRepository.findByUsuarioId(usuario.getId_usuario())
                .stream()
                .map(ur -> ur.getRol().getNombre_rol())
                .collect(Collectors.toList());
        
        System.out.println("Roles del usuario: " + roles);
        System.out.println("==================");

        String nombreCompleto = usuario.getNombre_usuario() + " " + usuario.getApellido_usuario();

        return new LoginResponse(
                usuario.getId_usuario(),
                nombreCompleto,
                usuario.getEmail_usuario(),
                "TOKEN_JWT_AQUI", // Implementar JWT después
                "Login exitoso",
                roles
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
                && usuarioRepository.existsByEmail(request.getEmail_usuario())) {
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
        List<String> roles = usuarioRolRepository.findByUsuarioId(usuario.getId_usuario())
                .stream()
                .map(ur -> ur.getRol().getNombre_rol())
                .collect(Collectors.toList());

        // Información de sucursal (solo para vendedores)
        Integer idSucursal = null;
        String nombreSucursal = null;
        if (usuario.getSucursal() != null) {
            idSucursal = usuario.getSucursal().getId_sucursal();
            // Como SucursalesEntity no tiene campo nombre, usamos ID y teléfono para identificar
            nombreSucursal = "Sucursal #" + idSucursal + " - " + usuario.getSucursal().getTelefono_sucursal();
        }

        UsuarioResponse response = new UsuarioResponse();
        response.setId_usuario(usuario.getId_usuario());
        response.setNombre_usuario(usuario.getNombre_usuario());
        response.setApellido_usuario(usuario.getApellido_usuario());
        response.setEmail_usuario(usuario.getEmail_usuario());
        response.setDni_usuario(usuario.getDni_usuario());
        response.setTelefono_usuario(usuario.getTelefono_usuario());
        response.setDireccion(null); // DireccionResponse - implementar después
        response.setRoles(roles);
        response.setIdSucursal(idSucursal);
        response.setNombreSucursal(nombreSucursal);

        return response;
    }

    /**
     * Crear un vendedor (solo para administradores).
     * Asigna automáticamente el rol VENDEDOR y la sucursal especificada.
     */
    @Transactional
    public UsuarioResponse crearVendedor(CreateVendedorRequest request) {
        String emailLimpio = request.getEmail_usuario().trim().toLowerCase();
        String dniLimpio = request.getDni_usuario().trim();

        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(emailLimpio)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email: " + emailLimpio);
        }

        // Validar que no exista el DNI
        if (usuarioRepository.existsByDni(dniLimpio)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el DNI: " + dniLimpio);
        }

        // Verificar que existe el rol VENDEDOR
        RolesEntity rolVendedor = rolRepository.findByNombreRol(RolEnum.VENDEDOR.name())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Rol VENDEDOR no encontrado en el sistema"));

        // Verificar que existe la sucursal
        SucursalesEntity sucursal = sucursalRepository.findById(request.getId_sucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Sucursal no encontrada con ID: " + request.getId_sucursal()));

        // Crear el usuario vendedor
        UsuariosEntity vendedor = new UsuariosEntity();
        vendedor.setNombre_usuario(request.getNombre_usuario());
        vendedor.setApellido_usuario(request.getApellido_usuario());
        vendedor.setEmail_usuario(emailLimpio);
        vendedor.setContrasena(passwordEncoder.encode(request.getContrasena()));
        vendedor.setDni_usuario(dniLimpio);
        vendedor.setTelefono_usuario(request.getTelefono_usuario());
        vendedor.setSucursal(sucursal); // Asignar sucursal

        UsuariosEntity vendedorGuardado = usuarioRepository.save(vendedor);

        // Asignar rol VENDEDOR
        Usuario_rolesEntity usuarioRol = new Usuario_rolesEntity();
        usuarioRol.setUsuario(vendedorGuardado);
        usuarioRol.setRol(rolVendedor);
        usuarioRolRepository.save(usuarioRol);

        System.out.println("✅ Vendedor creado: " + vendedorGuardado.getEmail_usuario() + 
                          " - Sucursal ID: " + sucursal.getId_sucursal());

        return convertirAResponse(vendedorGuardado);
    }
}

