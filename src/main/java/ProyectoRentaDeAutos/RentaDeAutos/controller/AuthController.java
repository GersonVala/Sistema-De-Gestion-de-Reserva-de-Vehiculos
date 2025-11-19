package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.request.UsuarioRegistroDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controlador para autenticación y registro de usuarios.
 * Maneja las rutas públicas de /auth/*
 */
@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra la página de login.
     * GET /auth/login
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    /**
     * Muestra la página de registro.
     * GET /auth/register
     */
    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        return "auth/register";
    }

    /**
     * Procesa el registro de un nuevo cliente.
     * POST /auth/register
     */
    @PostMapping("/register")
    public String registrarCliente(
            @Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO usuarioDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Intento de registro para email: {}", usuarioDTO.getEmail());

        // Validar errores de Bean Validation
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Validar que las contraseñas coincidan
        if (!usuarioDTO.passwordsMatch()) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "auth/register";
        }

        try {
            // Convertir DTO a Entity (conversión manual simple)
            Usuario usuario = convertirDtoAUsuario(usuarioDTO);

            // Registrar como CLIENTE por defecto
            usuarioService.registrarUsuario(usuario, "CLIENTE");

            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor inicie sesión.");
            log.info("Usuario registrado exitosamente: {}", usuario.getEmail());

            return "redirect:/auth/login";

        } catch (BusinessException e) {
            log.warn("Error en registro: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    /**
     * Convierte UsuarioRegistroDTO a Usuario (conversión manual).
     */
    private Usuario convertirDtoAUsuario(UsuarioRegistroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setContra(dto.getPassword()); // Se hasheará en el servicio
        usuario.setDni(dto.getDni());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        return usuario;
    }

    /**
     * Página de acceso denegado.
     * GET /auth/access-denied
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
