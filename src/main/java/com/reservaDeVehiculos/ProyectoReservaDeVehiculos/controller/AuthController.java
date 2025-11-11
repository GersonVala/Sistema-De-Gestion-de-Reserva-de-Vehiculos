package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.LoginRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.RegistroUsuarioRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.LoginResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.UsuarioResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SessionService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;
    private final SessionService sessionService;

    /**
     * Mostrar página de login
     */
    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        // Si ya está logueado, redirigir al dashboard
        if (sessionService.isUserLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    /**
     * Procesar login
     */
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        // Si hay errores de validación, volver al formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Por favor, complete todos los campos correctamente.");
            return "login";
        }

        try {
            // Llamar al servicio para autenticar
            LoginResponse loginResponse = usuarioService.login(loginRequest);
            
            // Crear sesión usando SessionService
            sessionService.createUserSession(session, loginResponse);
            
            log.info("Usuario autenticado exitosamente: {}", loginResponse.getEmail_usuario());
            
            // Verificar si hay una URL de redirección guardada
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                redirectAttributes.addFlashAttribute("mensaje", 
                    "¡Bienvenido/a " + loginResponse.getNombre_completo() + "!");
                return "redirect:" + redirectUrl;
            }
            
            // Redirigir al dashboard con mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Bienvenido/a " + loginResponse.getNombre_completo() + "!");
            return "redirect:/dashboard";
            
        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            model.addAttribute("error", "Email o contraseña incorrectos. Por favor, intente nuevamente.");
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }
    }

    /**
     * Mostrar página de registro
     */
    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        // Si ya está logueado, redirigir al dashboard
        if (sessionService.isUserLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("registroRequest", new RegistroUsuarioRequest());
        return "register";
    }

    /**
     * Procesar registro
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registroRequest") RegistroUsuarioRequest registroRequest,
                                BindingResult bindingResult,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        // Validación de contraseñas coincidentes
        if (!registroRequest.getContrasena().equals(confirmPassword)) {
            bindingResult.rejectValue("contrasena", "password.mismatch", 
                "Las contraseñas no coinciden");
        }
        
        // Si hay errores de validación, volver al formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Por favor, corrija los errores del formulario.");
            return "register";
        }

        try {
            // Llamar al servicio para registrar usuario
            UsuarioResponse usuarioResponse = usuarioService.registrarUsuario(registroRequest);
            
            log.info("Usuario registrado exitosamente: {}", usuarioResponse.getEmail_usuario());
            
            // Redirigir al login con mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Registro exitoso! Ya puede iniciar sesión con sus credenciales.");
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
            
            String errorMessage = e.getMessage();
            if (errorMessage.contains("email")) {
                errorMessage = "Ya existe un usuario con ese email.";
            } else if (errorMessage.contains("dni")) {
                errorMessage = "Ya existe un usuario con ese DNI.";
            } else {
                errorMessage = "Error al registrar usuario. Por favor, intente nuevamente.";
            }
            
            model.addAttribute("error", errorMessage);
            model.addAttribute("registroRequest", registroRequest);
            return "register";
        }
    }

    /**
     * Cerrar sesión
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        sessionService.destroyUserSession(session);
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada exitosamente.");
        return "redirect:/";
    }
    
    /**
     * Redirigir logout por GET también
     */
    @GetMapping("/logout")
    public String logoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        sessionService.destroyUserSession(session);
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada exitosamente.");
        return "redirect:/";
    }
}