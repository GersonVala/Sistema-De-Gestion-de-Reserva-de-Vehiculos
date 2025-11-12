package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CreateVendedorRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.UsuarioResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.ForbiddenException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.AuthorizationService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para operaciones de ADMINISTRADOR.
 * Todos los endpoints requieren rol ADMINISTRADOR.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UsuarioService usuarioService;
    private final AuthorizationService authService;
    private final com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SucursalService sucursalService;

    /**
     * Dashboard del administrador - Vista principal
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!authService.isAdministrador(session)) {
            log.warn("⚠️ Usuario sin rol ADMINISTRADOR intentó acceder a dashboard admin");
            throw new ForbiddenException("No tienes permisos para acceder a esta sección");
        }

        // Aquí puedes agregar métricas generales del sistema
        model.addAttribute("titulo", "Panel de Administración");
        
        log.info("✅ Administrador accedió al dashboard");
        return "admin-dashboard";
    }

    /**
     * Vista para crear vendedores
     */
    @GetMapping("/vendedores/crear")
    public String mostrarFormularioCrearVendedor(HttpSession session, Model model) {
        if (!authService.isAdministrador(session)) {
            throw new ForbiddenException("No tienes permisos para acceder a esta sección");
        }

        // Obtener lista de sucursales para el dropdown
        model.addAttribute("sucursales", sucursalService.obtenerTodas());
        
        // Obtener lista de vendedores existentes (usuarios con rol VENDEDOR)
        model.addAttribute("vendedores", usuarioService.obtenerTodos().stream()
                .filter(u -> u.getRoles().contains("VENDEDOR"))
                .toList());
        
        model.addAttribute("titulo", "Crear Nuevo Vendedor");
        return "admin-crear-vendedor";
    }

    /**
     * Crear un nuevo vendedor (formulario HTML)
     * Solo ADMINISTRADOR puede crear vendedores
     */
    @PostMapping("/vendedores/crear")
    public String crearVendedor(
            @Valid @ModelAttribute CreateVendedorRequest request,
            HttpSession session,
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        if (!authService.isAdministrador(session)) {
            log.warn("⚠️ Usuario sin rol ADMINISTRADOR intentó crear vendedor");
            throw new ForbiddenException("Solo administradores pueden crear vendedores");
        }

        try {
            UsuarioResponse vendedor = usuarioService.crearVendedor(request);
            log.info("✅ Administrador creó vendedor: {} - Sucursal ID: {}", 
                    vendedor.getEmail_usuario(), vendedor.getIdSucursal());
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Vendedor creado exitosamente: " + vendedor.getEmail_usuario());
            return "redirect:/admin/vendedores/crear";
            
        } catch (Exception e) {
            log.error("❌ Error al crear vendedor: {}", e.getMessage());
            
            // Volver al formulario con error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sucursales", sucursalService.obtenerTodas());
            model.addAttribute("vendedores", usuarioService.obtenerTodos().stream()
                    .filter(u -> u.getRoles().contains("VENDEDOR"))
                    .toList());
            return "admin-crear-vendedor";
        }
    }

    /**
     * API: Listar todos los usuarios (para administración)
     */
    @GetMapping("/usuarios")
    @ResponseBody
    public ResponseEntity<?> listarUsuarios(HttpSession session) {
        if (!authService.isAdministrador(session)) {
            throw new ForbiddenException("No tienes permisos para esta operación");
        }

        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
}
