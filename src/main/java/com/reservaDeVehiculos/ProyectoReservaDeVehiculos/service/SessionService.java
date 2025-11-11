package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.LoginResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.UsuarioResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SessionService {

    // Claves para atributos de sesión
    public static final String SESSION_USER = "usuario";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_NAME = "userName";
    public static final String SESSION_USER_EMAIL = "userEmail";
    public static final String SESSION_USER_ROLES = "userRoles";
    public static final String SESSION_LOGIN_TIME = "loginTime";
    public static final String SESSION_LAST_ACTIVITY = "lastActivity";

    /**
     * Crear sesión para usuario autenticado
     */
    public void createUserSession(HttpSession session, LoginResponse loginResponse) {
        try {
            // Invalidar sesión anterior si existe
            session.invalidate();
            
            // Crear nueva sesión
            session.setAttribute(SESSION_USER, loginResponse);
            session.setAttribute(SESSION_USER_ID, loginResponse.getId_usuario());
            session.setAttribute(SESSION_USER_NAME, loginResponse.getNombre_completo());
            session.setAttribute(SESSION_USER_EMAIL, loginResponse.getEmail_usuario());
            session.setAttribute(SESSION_LOGIN_TIME, LocalDateTime.now());
            session.setAttribute(SESSION_LAST_ACTIVITY, LocalDateTime.now());
            
            // Configurar timeout de sesión (30 minutos)
            session.setMaxInactiveInterval(30 * 60);
            
            log.info("Sesión creada para usuario: {} [ID: {}]", 
                    loginResponse.getEmail_usuario(), 
                    loginResponse.getId_usuario());
                    
        } catch (Exception e) {
            log.error("Error al crear sesión para usuario {}: {}", 
                    loginResponse.getEmail_usuario(), e.getMessage());
            throw new RuntimeException("Error al crear sesión de usuario");
        }
    }

    /**
     * Actualizar información del usuario en sesión
     */
    public void updateUserSession(HttpSession session, UsuarioResponse usuarioResponse) {
        try {
            if (isUserLoggedIn(session)) {
                LoginResponse currentUser = getCurrentUser(session);
                
                // Actualizar información
                LoginResponse updatedUser = new LoginResponse(
                    usuarioResponse.getId_usuario(),
                    usuarioResponse.getNombre_usuario() + " " + usuarioResponse.getApellido_usuario(),
                    usuarioResponse.getEmail_usuario(),
                    currentUser.getToken(), // Mantener token
                    "Usuario actualizado"
                );
                
                session.setAttribute(SESSION_USER, updatedUser);
                session.setAttribute(SESSION_USER_NAME, updatedUser.getNombre_completo());
                session.setAttribute(SESSION_USER_EMAIL, updatedUser.getEmail_usuario());
                session.setAttribute(SESSION_LAST_ACTIVITY, LocalDateTime.now());
                
                log.info("Sesión actualizada para usuario: {} [ID: {}]", 
                        updatedUser.getEmail_usuario(), 
                        updatedUser.getId_usuario());
            }
        } catch (Exception e) {
            log.error("Error al actualizar sesión: {}", e.getMessage());
        }
    }

    /**
     * Actualizar última actividad
     */
    public void updateLastActivity(HttpSession session) {
        if (session != null && isUserLoggedIn(session)) {
            session.setAttribute(SESSION_LAST_ACTIVITY, LocalDateTime.now());
        }
    }

    /**
     * Verificar si el usuario está logueado
     */
    public boolean isUserLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    /**
     * Obtener usuario actual de la sesión
     */
    public LoginResponse getCurrentUser(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return null;
        }
        return (LoginResponse) session.getAttribute(SESSION_USER);
    }

    /**
     * Obtener ID del usuario actual
     */
    public Integer getCurrentUserId(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return null;
        }
        return (Integer) session.getAttribute(SESSION_USER_ID);
    }

    /**
     * Obtener nombre del usuario actual
     */
    public String getCurrentUserName(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return null;
        }
        return (String) session.getAttribute(SESSION_USER_NAME);
    }

    /**
     * Obtener email del usuario actual
     */
    public String getCurrentUserEmail(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return null;
        }
        return (String) session.getAttribute(SESSION_USER_EMAIL);
    }

    /**
     * Verificar si el usuario tiene un rol específico
     */
    public boolean userHasRole(HttpSession session, String role) {
        // TODO: Implementar cuando tengamos roles en la sesión
        return false;
    }

    /**
     * Obtener tiempo de sesión activa
     */
    public String getSessionDuration(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "0 minutos";
        }

        LocalDateTime loginTime = (LocalDateTime) session.getAttribute(SESSION_LOGIN_TIME);
        if (loginTime == null) {
            return "Desconocido";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(loginTime, now).toMinutes();
        
        if (minutes < 60) {
            return minutes + " minutos";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " horas " + remainingMinutes + " minutos";
        }
    }

    /**
     * Obtener información de la sesión para debugging
     */
    public String getSessionInfo(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "No hay sesión activa";
        }

        StringBuilder info = new StringBuilder();
        info.append("Usuario: ").append(getCurrentUserEmail(session)).append("\n");
        info.append("ID: ").append(getCurrentUserId(session)).append("\n");
        info.append("Sesión activa por: ").append(getSessionDuration(session)).append("\n");
        
        LocalDateTime lastActivity = (LocalDateTime) session.getAttribute(SESSION_LAST_ACTIVITY);
        if (lastActivity != null) {
            info.append("Última actividad: ")
                .append(lastActivity.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .append("\n");
        }
        
        info.append("Session ID: ").append(session.getId());
        
        return info.toString();
    }

    /**
     * Cerrar sesión del usuario
     */
    public void destroyUserSession(HttpSession session) {
        if (session != null) {
            String userEmail = getCurrentUserEmail(session);
            Integer userId = getCurrentUserId(session);
            
            try {
                session.invalidate();
                log.info("Sesión cerrada para usuario: {} [ID: {}]", userEmail, userId);
            } catch (IllegalStateException e) {
                log.warn("Intento de cerrar sesión ya invalidada para usuario: {}", userEmail);
            }
        }
    }

    /**
     * Limpiar sesiones expiradas (para uso interno)
     */
    public boolean isSessionExpired(HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return true;
        }

        LocalDateTime lastActivity = (LocalDateTime) session.getAttribute(SESSION_LAST_ACTIVITY);
        if (lastActivity == null) {
            return true;
        }

        // Considerar expirada si no hay actividad en más de 30 minutos
        LocalDateTime now = LocalDateTime.now();
        long minutesInactive = java.time.Duration.between(lastActivity, now).toMinutes();
        
        return minutesInactive > 30;
    }
}