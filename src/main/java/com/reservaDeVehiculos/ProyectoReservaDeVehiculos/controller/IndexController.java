package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Objeto simple para el formulario (puede ser una clase real después)
class BusquedaFormPlaceholder {} 

@Controller
public class IndexController {

    // NOTA: Las rutas "/" y "/reservation" ahora son manejadas por WebViewController
    // que proporciona datos dinámicos desde el backend

    /*
    @GetMapping("/") 
    public String index(Model model) { 
        // Adjuntamos un objeto REAL (aunque vacío) para el formulario
        model.addAttribute("busquedaForm", new BusquedaFormPlaceholder()); 
        // Adjuntamos las listas vacías
        model.addAttribute("listaOficinas", new ArrayList<>()); 
        model.addAttribute("listaCategorias", new ArrayList<>()); 
        return "index";
    }

    @GetMapping("/reservation")
    public String showReservation(Model model) {
        return "reservation";
    }
    */

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String remember,
            Model model) {
        
        // Aquí iría la lógica real de autenticación
        // Por ahora, simplemente redirigimos al usuario de vuelta al inicio
        
        // Ejemplo: guardar en sesión si es necesario
        // httpSession.setAttribute("usuarioAutenticado", email);
        
        // Puedes validar las credenciales aquí más adelante con una base de datos
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            // Simulamos login exitoso por ahora y redirigimos al inicio
            model.addAttribute("mensaje", "¡Bienvenido " + email + "!");
                return "dashboard"; // Redirige al dashboard después del login exitoso
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        // Preparar datos iniciales para el formulario de registro
        model.addAttribute("registro", new RegistroForm());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Nombre y correo son obligatorios");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }

        // Aquí conectarías con un servicio para persistir el usuario.
        // Por ahora, simulamos éxito y redirigimos al login con un mensaje.
        model.addAttribute("mensaje", "Registro exitoso. Por favor, inicia sesión.");
        return "login";
    }

    // Clase simple para model binding (temporal)
    static class RegistroForm {
        public String nombre;
        public String email;
        public String password;
        public String confirmPassword;
    }
}