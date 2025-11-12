package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.UsuariosEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador de DEBUG - Solo para desarrollo
 * Eliminar o deshabilitar en producción
 */
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Ver todos los usuarios en la BD
     * URL: http://localhost:8090/api/debug/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<Map<String, Object>> verUsuarios() {
        List<UsuariosEntity> usuarios = usuarioRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", usuarios.size());
        response.put("usuarios", usuarios.stream().map(u -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", u.getId_usuario());
            userData.put("nombre", u.getNombre_usuario());
            userData.put("apellido", u.getApellido_usuario());
            userData.put("email", u.getEmail_usuario());
            userData.put("dni", u.getDni_usuario());
            userData.put("telefono", u.getTelefono_usuario());
            userData.put("password_length", u.getContrasena() != null ? u.getContrasena().length() : 0);
            userData.put("password_starts_with", u.getContrasena() != null ? u.getContrasena().substring(0, Math.min(10, u.getContrasena().length())) : "");
            return userData;
        }).collect(Collectors.toList()));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar usuario por email
     * URL: http://localhost:8090/api/debug/usuarios/email?email=salolopez@gmail.com
     */
    @GetMapping("/usuarios/email")
    public ResponseEntity<Map<String, Object>> buscarPorEmail(@RequestParam String email) {
        String emailLimpio = email.trim().toLowerCase();
        
        Map<String, Object> response = new HashMap<>();
        response.put("email_buscado", emailLimpio);
        
        return usuarioRepository.findByEmail(emailLimpio)
            .map(u -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("encontrado", true);
                userData.put("id", u.getId_usuario());
                userData.put("nombre_completo", u.getNombre_usuario() + " " + u.getApellido_usuario());
                userData.put("email_en_bd", u.getEmail_usuario());
                userData.put("dni", u.getDni_usuario());
                userData.put("password_length", u.getContrasena().length());
                userData.put("password_empieza_con", u.getContrasena().substring(0, 10));
                userData.put("password_es_bcrypt", u.getContrasena().startsWith("$2a$") || u.getContrasena().startsWith("$2b$"));
                response.put("usuario", userData);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("encontrado", false);
                response.put("mensaje", "Usuario no encontrado con email: " + emailLimpio);
                return ResponseEntity.ok(response);
            });
    }

    /**
     * Verificar si existe un email
     * URL: http://localhost:8090/api/debug/usuarios/existe?email=salolopez@gmail.com
     */
    @GetMapping("/usuarios/existe")
    public ResponseEntity<Map<String, Object>> verificarExiste(@RequestParam String email) {
        String emailLimpio = email.trim().toLowerCase();
        boolean existe = usuarioRepository.existsByEmail(emailLimpio);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", emailLimpio);
        response.put("existe", existe);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Estadísticas de la BD
     * URL: http://localhost:8090/api/debug/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> estadisticas() {
        List<UsuariosEntity> usuarios = usuarioRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_usuarios", usuarios.size());
        stats.put("usuarios_con_password_60_chars", usuarios.stream()
            .filter(u -> u.getContrasena() != null && u.getContrasena().length() == 60)
            .count());
        stats.put("usuarios_con_bcrypt", usuarios.stream()
            .filter(u -> u.getContrasena() != null && 
                        (u.getContrasena().startsWith("$2a$") || u.getContrasena().startsWith("$2b$")))
            .count());
        stats.put("emails_registrados", usuarios.stream()
            .map(UsuariosEntity::getEmail_usuario)
            .collect(Collectors.toList()));
        
        return ResponseEntity.ok(stats);
    }
}
