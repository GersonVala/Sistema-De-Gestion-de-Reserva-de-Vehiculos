package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.SucursalResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.TipoVehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.VehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SucursalService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.TipoVehiculoService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador Web para servir las vistas HTML de la aplicación.
 * Este controlador maneja las rutas públicas y pasa datos dinámicos
 * desde el backend a las plantillas Thymeleaf.
 * 
 * NO confundir con los controladores REST (que retornan JSON).
 */
@Controller
@RequiredArgsConstructor
public class WebViewController {

    private final VehiculoService vehiculoService;
    private final SucursalService sucursalService;
    private final TipoVehiculoService tipoVehiculoService;

    /**
     * Ruta principal de la aplicación (Home Page)
     * Muestra la página de inicio con sucursales y tipos de vehículos disponibles.
     * 
     * @param model Objeto para pasar datos a la vista Thymeleaf
     * @return Nombre de la plantilla Thymeleaf (index.html)
     */
    @GetMapping("/")
    public String mostrarPaginaInicio(Model model) {
        // Obtener todas las sucursales
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        
        // Obtener todos los tipos de vehículos
        List<TipoVehiculoResponse> tiposDeVehiculos = tipoVehiculoService.obtenerTodos();
        
        // Obtener todos los vehículos disponibles
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodos();
        
        // Pasar datos al modelo para que Thymeleaf los use
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("tiposDeVehiculos", tiposDeVehiculos);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("tituloPagina", "Bienvenido a RentCar - Sistema de Reserva de Vehículos");
        
        return "index"; // Retorna la vista index.html
    }

    /**
     * Ruta para la página de reservas
     * Muestra el formulario de reservas con datos dinámicos de vehículos y sucursales.
     * 
     * @param model Objeto para pasar datos a la vista Thymeleaf
     * @return Nombre de la plantilla Thymeleaf (reservation.html)
     */
    @GetMapping("/reservas")
    public String mostrarPaginaReservas(Model model) {
        // Obtener todas las sucursales
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        
        // Obtener todos los tipos de vehículos
        List<TipoVehiculoResponse> tiposDeVehiculos = tipoVehiculoService.obtenerTodos();
        
        // Obtener todos los vehículos disponibles
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodos();
        
        // Pasar datos al modelo para que Thymeleaf los use
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("tiposDeVehiculos", tiposDeVehiculos);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("tituloPagina", "Reserva tu Vehículo");
        
        return "reservation"; // Retorna la vista reservation.html
    }

    /**
     * Ruta para la página de oficinas/sucursales
     * Muestra todas las sucursales disponibles con su información.
     * 
     * @param model Objeto para pasar datos a la vista Thymeleaf
     * @return Nombre de la plantilla Thymeleaf (oficinas.html)
     */
    @GetMapping("/oficinas")
    public String mostrarPaginaOficinas(Model model) {
        // Obtener todas las sucursales con su información
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        
        // Pasar datos al modelo
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("tituloPagina", "Nuestras Oficinas");
        
        return "oficinas"; // Retorna la vista oficinas.html (puedes crearla después)
    }
}
