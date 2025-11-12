package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.controller;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearReservaRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ReservaFormRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.ReservaResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.SucursalResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.TipoVehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.VehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.ReservaService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.SucursalService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.TipoVehiculoService;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service.VehiculoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
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
    private final ReservaService reservaService;

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
        System.out.println("🔍 DEBUG - Sucursales encontradas: " + sucursales.size());
        
        // Obtener todos los tipos de vehículos
        List<TipoVehiculoResponse> tiposDeVehiculos = tipoVehiculoService.obtenerTodos();
        System.out.println("🔍 DEBUG - Tipos de vehículos encontrados: " + tiposDeVehiculos.size());
        
        // Obtener todos los vehículos disponibles
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodos();
        System.out.println("🔍 DEBUG - Vehículos encontrados: " + vehiculos.size());
        
        // Pasar datos al modelo para que Thymeleaf los use
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("tiposDeVehiculos", tiposDeVehiculos);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("tituloPagina", "Bienvenido a RentCar - Sistema de Reserva de Vehículos");
        
        return "index"; // Retorna la vista index.html
    }

    /**
     * Ruta para la página de reservas (GET)
     * Muestra el formulario de reservas con datos dinámicos de vehículos y sucursales.
     * Captura parámetros de búsqueda desde la home para pre-llenar el formulario.
     * 
     * @param model Objeto para pasar datos a la vista Thymeleaf
     * @param pickupOffice ID de la oficina de retiro (opcional)
     * @param dropoffOffice ID de la oficina de devolución (opcional)
     * @param pickupDate Fecha de retiro (opcional)
     * @param pickupTime Hora de retiro (opcional)
     * @param dropoffDate Fecha de devolución (opcional)
     * @param dropoffTime Hora de devolución (opcional)
     * @return Nombre de la plantilla Thymeleaf (reservation.html)
     */
    @GetMapping("/reservas")
    public String mostrarPaginaReservas(
            Model model, 
            HttpSession session,
            @RequestParam(required = false) String pickupOffice,
            @RequestParam(required = false) String dropoffOffice,
            @RequestParam(required = false) String pickupDate,
            @RequestParam(required = false) String pickupTime,
            @RequestParam(required = false) String dropoffDate,
            @RequestParam(required = false) String dropoffTime) {
        
        // Obtener todas las sucursales
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        
        // Obtener todos los vehículos disponibles
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodos();
        
        // Crear objeto vacío para el formulario
        ReservaFormRequest reservaRequest = new ReservaFormRequest();
        
        // Si el usuario está logueado, pre-cargar su ID
        Integer userId = (Integer) session.getAttribute("usuarioId");
        if (userId != null) {
            reservaRequest.setId_usuario(userId);
        }
        
        // Pasar datos al modelo para que Thymeleaf los use
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("reservaRequest", reservaRequest);
        model.addAttribute("tituloPagina", "Reserva tu Vehículo");
        
        // Pasar parámetros de búsqueda para pre-llenar el formulario
        model.addAttribute("pickupOffice", pickupOffice);
        model.addAttribute("dropoffOffice", dropoffOffice);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("pickupTime", pickupTime);
        model.addAttribute("dropoffDate", dropoffDate);
        model.addAttribute("dropoffTime", dropoffTime);
        
        return "reservation"; // Retorna la vista reservation.html
    }

    /**
     * Procesa el formulario de reserva (POST)
     * Crea una nueva reserva en el sistema.
     * 
     * @param formRequest Datos del formulario
     * @param session Sesión HTTP para verificar autenticación
     * @param redirectAttributes Para pasar mensajes de éxito/error
     * @return Redirección al dashboard o al login
     */
    @PostMapping("/reservas/crear")
    public String crearReserva(
            @ModelAttribute ReservaFormRequest formRequest,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar si el usuario está logueado
            Integer userId = (Integer) session.getAttribute("usuarioId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar una reserva");
                return "redirect:/login";
            }
            
            // Validaciones básicas
            if (formRequest.getFecha_inicio() == null || formRequest.getFecha_fin() == null) {
                redirectAttributes.addFlashAttribute("error", "Las fechas son obligatorias");
                return "redirect:/reservas";
            }
            
            if (formRequest.getId_sucursal() == null) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar una sucursal de retiro");
                return "redirect:/reservas";
            }
            
            if (formRequest.getId_sucursal_devolucion() == null) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar una sucursal de devolución");
                return "redirect:/reservas";
            }
            
            if (formRequest.getId_vehiculo() == null) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar un vehículo");
                return "redirect:/reservas";
            }
            
            // Calcular días de reserva
            long dias = ChronoUnit.DAYS.between(formRequest.getFecha_inicio(), formRequest.getFecha_fin());
            if (dias <= 0) {
                redirectAttributes.addFlashAttribute("error", "La fecha de devolución debe ser posterior a la fecha de retiro");
                return "redirect:/reservas";
            }
            
            // Calcular precio total (precio base por día * días)
            // TODO: Obtener precio real del vehículo cuando se agregue al modelo
            BigDecimal precioPorDia = new BigDecimal("5000.00"); // Precio base temporal
            BigDecimal precioTotal = precioPorDia.multiply(BigDecimal.valueOf(dias));
            
            // Crear request para el servicio
            CrearReservaRequest crearRequest = new CrearReservaRequest();
            crearRequest.setFecha_inicio(formRequest.getFecha_inicio());
            crearRequest.setFecha_fin(formRequest.getFecha_fin());
            crearRequest.setId_sucursal(formRequest.getId_sucursal());
            crearRequest.setId_sucursal_devolucion(formRequest.getId_sucursal_devolucion());
            crearRequest.setId_usuario(userId);
            crearRequest.setEstado(EstadoReservaEnum.PENDIENTE);
            crearRequest.setPrecio_reserva(precioTotal);
            
            // Crear la reserva
            ReservaResponse reserva = reservaService.crear(crearRequest);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Reserva creada exitosamente! Número de reserva: " + reserva.getId_reserva());
            return "redirect:/dashboard";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear la reserva: " + e.getMessage());
            return "redirect:/reservas";
        }
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
