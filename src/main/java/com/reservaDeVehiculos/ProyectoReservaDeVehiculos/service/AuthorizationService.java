package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.ReservaRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.RolRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.UsuarioRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.UsuarioRolRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio centralizado para validación de autorización y permisos por rol.
 * Maneja la lógica de control de acceso basado en roles (RBAC) para el sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    /**
     * Verifica si el usuario en sesión tiene un rol específico.
     * 
     * @param session Sesión HTTP del usuario
     * @param rolEnum Rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean hasRole(HttpSession session, RolEnum rolEnum) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            log.warn("⚠️ Usuario no autenticado intentando verificar rol: {}", rolEnum);
            return false;
        }

        Integer rolId = getRolIdByEnum(rolEnum);
        if (rolId == null) {
            log.error("❌ No se encontró el rol en la base de datos: {}", rolEnum);
            return false;
        }

        boolean hasRole = usuarioRolRepository.existsByUsuarioIdAndRolId(userId, rolId);
        log.debug("🔍 Usuario [ID: {}] tiene rol {}: {}", userId, rolEnum, hasRole);
        return hasRole;
    }

    /**
     * Verifica si el usuario es un CLIENTE.
     */
    public boolean isCliente(HttpSession session) {
        return hasRole(session, RolEnum.CLIENTE);
    }

    /**
     * Verifica si el usuario es un VENDEDOR.
     */
    public boolean isVendedor(HttpSession session) {
        return hasRole(session, RolEnum.VENDEDOR);
    }

    /**
     * Verifica si el usuario es un ADMINISTRADOR.
     */
    public boolean isAdministrador(HttpSession session) {
        return hasRole(session, RolEnum.ADMINISTRADOR);
    }

    /**
     * Verifica si el usuario tiene al menos uno de los roles especificados.
     * 
     * @param session Sesión HTTP del usuario
     * @param roles Roles a verificar
     * @return true si el usuario tiene al menos uno de los roles
     */
    public boolean hasAnyRole(HttpSession session, RolEnum... roles) {
        for (RolEnum rol : roles) {
            if (hasRole(session, rol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene todos los roles del usuario en sesión.
     * 
     * @param session Sesión HTTP del usuario
     * @return Lista de entidades Usuario_rolesEntity
     */
    public List<Usuario_rolesEntity> getUserRoles(HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            log.warn("⚠️ Usuario no autenticado intentando obtener roles");
            return List.of();
        }

        return usuarioRolRepository.findByUsuarioId(userId);
    }

    /**
     * Verifica si un cliente puede acceder/modificar una reserva específica.
     * Un cliente solo puede acceder a sus propias reservas.
     * 
     * @param session Sesión HTTP del usuario
     * @param reservaId ID de la reserva
     * @return true si el cliente puede acceder a la reserva
     */
    public boolean canClienteAccessReserva(HttpSession session, Integer reservaId) {
        if (!isCliente(session)) {
            log.warn("⚠️ Usuario sin rol CLIENTE intentando acceder a reserva [ID: {}]", reservaId);
            return false;
        }

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return false;
        }

        ReservasEntity reserva = reservaRepository.findById(reservaId).orElse(null);
        if (reserva == null) {
            log.warn("⚠️ Reserva no encontrada [ID: {}]", reservaId);
            return false;
        }

        boolean isOwner = reserva.getUsuario().getId_usuario().equals(userId);
        log.debug("🔍 Cliente [ID: {}] es dueño de reserva [ID: {}]: {}", userId, reservaId, isOwner);
        return isOwner;
    }

    /**
     * Verifica si un vendedor puede gestionar una reserva.
     * Un vendedor puede gestionar reservas de su sucursal asignada o las que ya tiene asignadas.
     * 
     * @param session Sesión HTTP del usuario
     * @param reservaId ID de la reserva
     * @return true si el vendedor puede gestionar la reserva
     */
    public boolean canVendedorManageReserva(HttpSession session, Integer reservaId) {
        if (!isVendedor(session)) {
            log.warn("⚠️ Usuario sin rol VENDEDOR intentando gestionar reserva [ID: {}]", reservaId);
            return false;
        }

        Integer vendedorId = getUserIdFromSession(session);
        if (vendedorId == null) {
            return false;
        }

        ReservasEntity reserva = reservaRepository.findById(reservaId).orElse(null);
        if (reserva == null) {
            log.warn("⚠️ Reserva no encontrada [ID: {}]", reservaId);
            return false;
        }

        // El vendedor puede gestionar si:
        // 1. Ya tiene la reserva asignada
        if (reserva.getVendedor() != null && reserva.getVendedor().getId_usuario().equals(vendedorId)) {
            log.debug("🔍 Vendedor [ID: {}] tiene asignada la reserva [ID: {}]", vendedorId, reservaId);
            return true;
        }

        // 2. La reserva está PENDIENTE y pertenece a la sucursal del vendedor
        // (Para permitir que cualquier vendedor de la sucursal pueda aprobar/rechazar)
        if (reserva.getEstado() == EstadoReservaEnum.PENDIENTE) {
            Integer vendedorSucursalId = getVendedorSucursal(vendedorId);
            if (vendedorSucursalId != null && vendedorSucursalId.equals(reserva.getSucursal().getId_sucursal())) {
                log.debug("🔍 Vendedor [ID: {}] puede gestionar reserva PENDIENTE [ID: {}] de su sucursal", vendedorId, reservaId);
                return true;
            }
        }

        log.debug("🔍 Vendedor [ID: {}] NO puede gestionar reserva [ID: {}]", vendedorId, reservaId);
        return false;
    }

    /**
     * Obtiene el ID de la sucursal asignada a un vendedor.
     * Los vendedores deben tener una sucursal asignada en su entidad usuario.
     * 
     * @param vendedorId ID del vendedor
     * @return ID de la sucursal del vendedor o null si no tiene asignada
     */
    public Integer getVendedorSucursal(Integer vendedorId) {
        UsuariosEntity vendedor = usuarioRepository.findById(vendedorId).orElse(null);
        if (vendedor == null) {
            log.warn("⚠️ Vendedor no encontrado [ID: {}]", vendedorId);
            return null;
        }

        if (vendedor.getSucursal() == null) {
            log.warn("⚠️ Vendedor [ID: {}] no tiene sucursal asignada", vendedorId);
            return null;
        }

        return vendedor.getSucursal().getId_sucursal();
    }

    /**
     * Verifica si el usuario puede realizar cualquier acción sobre una reserva.
     * Los administradores tienen acceso total, clientes solo a sus reservas, 
     * vendedores a reservas de su sucursal.
     * 
     * @param session Sesión HTTP del usuario
     * @param reservaId ID de la reserva
     * @return true si el usuario puede acceder a la reserva
     */
    public boolean canAccessReserva(HttpSession session, Integer reservaId) {
        // Administradores tienen acceso total
        if (isAdministrador(session)) {
            log.debug("🔍 Administrador tiene acceso total a reserva [ID: {}]", reservaId);
            return true;
        }

        // Clientes solo a sus propias reservas
        if (isCliente(session)) {
            return canClienteAccessReserva(session, reservaId);
        }

        // Vendedores a reservas de su sucursal o asignadas
        if (isVendedor(session)) {
            return canVendedorManageReserva(session, reservaId);
        }

        log.warn("⚠️ Usuario sin rol válido intentando acceder a reserva [ID: {}]", reservaId);
        return false;
    }

    /**
     * Verifica si el usuario en sesión está autenticado.
     * 
     * @param session Sesión HTTP del usuario
     * @return true si el usuario está autenticado
     */
    public boolean isAuthenticated(HttpSession session) {
        return getUserIdFromSession(session) != null;
    }

    /**
     * Obtiene el ID del usuario desde la sesión.
     * Verifica ambos atributos: SESSION_USER_ID y "usuarioId" para compatibilidad.
     * 
     * @param session Sesión HTTP
     * @return ID del usuario o null si no está autenticado
     */
    private Integer getUserIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }

        // Intentar obtener desde SESSION_USER_ID
        Integer userId = (Integer) session.getAttribute(SessionService.SESSION_USER_ID);
        
        // Si no existe, intentar desde "usuarioId" (para compatibilidad)
        if (userId == null) {
            userId = (Integer) session.getAttribute("usuarioId");
        }

        return userId;
    }

    /**
     * Obtiene el ID del rol desde la base de datos según el enum.
     * 
     * @param rolEnum Enum del rol
     * @return ID del rol o null si no existe
     */
    private Integer getRolIdByEnum(RolEnum rolEnum) {
        return rolRepository.findByNombreRol(rolEnum.name())
                .map(RolesEntity::getId_rol)
                .orElse(null);
    }
}
