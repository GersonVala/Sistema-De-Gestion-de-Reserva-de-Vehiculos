package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.request.PerfilUpdateDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestión de perfil de usuario.
 * Accesible para todos los roles autenticados.
 */
@Controller
@RequestMapping("/perfil")
@Slf4j
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Mostrar formulario de edición de perfil.
     * GET /perfil/editar
     */
    @GetMapping("/editar")
    public String mostrarFormularioEditar(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario autenticado
            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerPorEmail(email);

            // Crear DTO con datos actuales (sin contraseña)
            PerfilUpdateDTO perfilDTO = new PerfilUpdateDTO(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                null, // nuevaContra
                null  // confirmarContra
            );

            model.addAttribute("perfilDTO", perfilDTO);
            model.addAttribute("usuario", usuario);
            model.addAttribute("emailReadOnly", usuario.getEmail()); // Para mostrar en el form
            model.addAttribute("dashboardUrl", getDashboardUrl(usuario.getRol().getNombre()));

            return "perfil/editar";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar perfil: " + e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * Helper method para obtener la URL del dashboard según el rol.
     */
    private String getDashboardUrl(String rolNombre) {
        return switch (rolNombre) {
            case "ADMIN" -> "/admin/dashboard";
            case "VENDEDOR" -> "/vendedor/dashboard";
            case "CLIENTE" -> "/cliente/dashboard";
            default -> "/";
        };
    }

    /**
     * Procesar actualización de perfil.
     * POST /perfil/editar
     */
    @PostMapping("/editar")
    public String actualizarPerfil(
            @Valid @ModelAttribute("perfilDTO") PerfilUpdateDTO perfilDTO,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener el usuario autenticado
            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerPorEmail(email);

            // Si hay errores de validación, volver al formulario
            if (result.hasErrors()) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("emailReadOnly", usuario.getEmail());
                model.addAttribute("dashboardUrl", getDashboardUrl(usuario.getRol().getNombre()));
                return "perfil/editar";
            }

            // Validar que las contraseñas coincidan si se proporcionó una nueva
            if (perfilDTO.getNuevaContra() != null && !perfilDTO.getNuevaContra().isEmpty()) {
                if (!perfilDTO.getNuevaContra().equals(perfilDTO.getConfirmarContra())) {
                    model.addAttribute("error", "Las contraseñas no coinciden");
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("emailReadOnly", usuario.getEmail());
                    model.addAttribute("dashboardUrl", getDashboardUrl(usuario.getRol().getNombre()));
                    return "perfil/editar";
                }
            }

            // Actualizar perfil
            usuarioService.actualizarPerfil(
                usuario.getIdUsuario(),
                perfilDTO.getNombre(),
                perfilDTO.getApellido(),
                perfilDTO.getDni(),
                perfilDTO.getTelefono(),
                perfilDTO.getDireccion(),
                perfilDTO.getNuevaContra()
            );

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");

            // Redirigir según el rol del usuario
            String rolNombre = usuario.getRol().getNombre();
            switch (rolNombre) {
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "VENDEDOR":
                    return "redirect:/vendedor/dashboard";
                case "CLIENTE":
                    return "redirect:/cliente/dashboard";
                default:
                    return "redirect:/";
            }

        } catch (BusinessException e) {
            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerPorEmail(email);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("emailReadOnly", usuario.getEmail());
            model.addAttribute("dashboardUrl", getDashboardUrl(usuario.getRol().getNombre()));
            return "perfil/editar";
        } catch (Exception e) {
            log.error("Error al actualizar perfil", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }
}
