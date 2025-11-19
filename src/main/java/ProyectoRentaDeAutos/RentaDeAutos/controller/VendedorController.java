package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.response.ReservaResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.VehiculoResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Empleado;
import ProyectoRentaDeAutos.RentaDeAutos.models.Reserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.service.EmpleadoService;
import ProyectoRentaDeAutos.RentaDeAutos.service.ReservaService;
import ProyectoRentaDeAutos.RentaDeAutos.service.VehiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para funcionalidades de VENDEDOR.
 * - Ver reservas pendientes de su sucursal
 * - Aceptar o rechazar reservas
 * - Ver historial de reservas de su sucursal
 */
@Controller
@RequestMapping("/vendedor")
@Slf4j
public class VendedorController {

    private final ReservaService reservaService;
    private final EmpleadoService empleadoService;
    private final VehiculoService vehiculoService;

    public VendedorController(ReservaService reservaService,
                             EmpleadoService empleadoService,
                             VehiculoService vehiculoService) {
        this.reservaService = reservaService;
        this.empleadoService = empleadoService;
        this.vehiculoService = vehiculoService;
    }

    /**
     * Dashboard del vendedor.
     * GET /vendedor/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            log.info("Accediendo al dashboard de vendedor con email: {}", email);

            Empleado empleado = empleadoService.obtenerPorEmail(email);

            // Verificar que el empleado esté activo
            if (!empleado.getEstado()) {
                log.error("Empleado desactivado intentando acceder: {}", email);
                redirectAttributes.addFlashAttribute("error",
                    "Su cuenta de empleado está desactivada. Contacte al administrador.");
                return "redirect:/auth/access-denied";
            }

            Long idSucursal = empleado.getSucursal().getIdSucursal();

            // Información de la sucursal y vendedor
            model.addAttribute("sucursalNombre", empleado.getSucursal().getNombre());
            model.addAttribute("sucursalDireccion", empleado.getSucursal().getDireccion());
            model.addAttribute("vendedorNombre", empleado.getUsuario().getNombre() + " " + empleado.getUsuario().getApellido());

            // Obtener todas las reservas de la sucursal
            List<Reserva> todasReservas = reservaService.obtenerPorSucursal(idSucursal);

            // Estadísticas
            long reservasPendientes = todasReservas.stream()
                .filter(r -> r.getEstado().name().equals("PENDIENTE"))
                .count();
            long reservasAceptadas = todasReservas.stream()
                .filter(r -> r.getEstado().name().equals("ACEPTADA"))
                .count();
            long reservasCanceladas = todasReservas.stream()
                .filter(r -> r.getEstado().name().equals("CANCELADA"))
                .count();

            model.addAttribute("reservasPendientes", reservasPendientes);
            model.addAttribute("reservasAceptadas", reservasAceptadas);
            model.addAttribute("reservasCanceladas", reservasCanceladas);
            model.addAttribute("totalReservas", todasReservas.size());

            // Lista de reservas pendientes para mostrar en el dashboard
            List<Reserva> reservasPendientesList = todasReservas.stream()
                .filter(r -> r.getEstado().name().equals("PENDIENTE"))
                .collect(Collectors.toList());

            // Convertir a DTOs con información adicional para la vista
            List<ReservaResponseDTO> reservasPendientesDTO = reservasPendientesList.stream()
                .map(this::convertirReservaADtoExtendido)
                .collect(Collectors.toList());

            model.addAttribute("reservasPendientesList", reservasPendientesDTO);

            return "vendedor/dashboard";

        } catch (ResourceNotFoundException e) {
            log.error("Empleado no encontrado para el usuario autenticado: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "No se encontró información de empleado para este usuario. Contacte al administrador para que le asigne una sucursal.");
            return "redirect:/auth/access-denied";
        } catch (Exception e) {
            log.error("Error inesperado en dashboard de vendedor: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                "Ocurrió un error al cargar el dashboard. Por favor intente nuevamente.");
            return "redirect:/auth/access-denied";
        }
    }

    /**
     * Ver todas las reservas de la sucursal del vendedor.
     * GET /vendedor/reservas
     */
    @GetMapping("/reservas")
    public String verReservas(
            @RequestParam(required = false) String estado,
            Authentication authentication,
            Model model) {

        try {
            String email = authentication.getName();
            Empleado empleado = empleadoService.obtenerPorEmail(email);
            Long idSucursal = empleado.getSucursal().getIdSucursal();

            List<Reserva> reservas;

            if (estado != null && !estado.isEmpty()) {
                // Filtrar por estado
                if ("PENDIENTE".equals(estado)) {
                    reservas = reservaService.obtenerPendientesPorSucursal(idSucursal);
                } else {
                    // Para otros estados, obtenemos todas y filtramos
                    reservas = reservaService.obtenerPorSucursal(idSucursal).stream()
                        .filter(r -> r.getEstado().name().equals(estado))
                        .toList();
                }
            } else {
                reservas = reservaService.obtenerPorSucursal(idSucursal);
            }

            // Convertir a DTOs
            List<ReservaResponseDTO> reservasDTO = reservas.stream()
                .map(this::convertirReservaADto)
                .collect(Collectors.toList());

            model.addAttribute("reservas", reservasDTO);
            model.addAttribute("sucursal", empleado.getSucursal());
            model.addAttribute("estadoFiltro", estado);

            return "vendedor/reservas";

        } catch (ResourceNotFoundException e) {
            log.error("Error al cargar reservas: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar las reservas");
            return "error/500";
        }
    }

    /**
     * Aceptar una reserva.
     * POST /vendedor/reservas/{id}/aceptar
     */
    @PostMapping("/reservas/{id}/aceptar")
    public String aceptarReserva(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            Empleado empleado = empleadoService.obtenerPorEmail(email);

            // Verificar que la reserva pertenece a la sucursal del vendedor
            Reserva reserva = reservaService.obtenerPorId(id);
            if (!reserva.getSucursalRetiro().getIdSucursal().equals(empleado.getSucursal().getIdSucursal())) {
                throw new BusinessException("No tiene permisos para gestionar esta reserva");
            }

            reservaService.aceptarReserva(id);

            redirectAttributes.addFlashAttribute("success", "Reserva aceptada exitosamente");
            log.info("Reserva {} aceptada por vendedor {}", id, email);

        } catch (BusinessException | ResourceNotFoundException e) {
            log.error("Error al aceptar reserva: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/vendedor/dashboard";
    }

    /**
     * Rechazar/Cancelar una reserva.
     * POST /vendedor/reservas/{id}/rechazar
     */
    @PostMapping("/reservas/{id}/rechazar")
    public String rechazarReserva(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            Empleado empleado = empleadoService.obtenerPorEmail(email);

            // Verificar que la reserva pertenece a la sucursal del vendedor
            Reserva reserva = reservaService.obtenerPorId(id);
            if (!reserva.getSucursalRetiro().getIdSucursal().equals(empleado.getSucursal().getIdSucursal())) {
                throw new BusinessException("No tiene permisos para gestionar esta reserva");
            }

            reservaService.cancelarReserva(id);

            redirectAttributes.addFlashAttribute("success", "Reserva rechazada exitosamente");
            log.info("Reserva {} rechazada por vendedor {}", id, email);

        } catch (BusinessException | ResourceNotFoundException e) {
            log.error("Error al rechazar reserva: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/vendedor/dashboard";
    }

    /**
     * Ver vehículos de la sucursal del vendedor (solo lectura).
     * GET /vendedor/vehiculos
     */
    @GetMapping("/vehiculos")
    public String verVehiculos(
            @RequestParam(required = false) String estado,
            Authentication authentication,
            Model model) {

        try {
            String email = authentication.getName();
            Empleado empleado = empleadoService.obtenerPorEmail(email);
            Long idSucursal = empleado.getSucursal().getIdSucursal();

            // Obtener todos los vehículos de la sucursal
            List<Vehiculo> vehiculos = vehiculoService.obtenerPorSucursal(idSucursal);

            // Filtrar por estado si se proporciona
            if (estado != null && !estado.isEmpty()) {
                vehiculos = vehiculos.stream()
                    .filter(v -> v.getEstado().name().equals(estado))
                    .collect(Collectors.toList());
            }

            // Convertir a DTOs
            List<VehiculoResponseDTO> vehiculosDTO = vehiculos.stream()
                .map(this::convertirVehiculoADto)
                .collect(Collectors.toList());

            model.addAttribute("vehiculos", vehiculosDTO);
            model.addAttribute("sucursal", empleado.getSucursal());
            model.addAttribute("estadoFiltro", estado);

            return "vendedor/vehiculos";

        } catch (ResourceNotFoundException e) {
            log.error("Error al cargar vehículos: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar los vehículos");
            return "error/500";
        }
    }

    // ==================== MÉTODOS DE CONVERSIÓN MANUAL ====================

    /**
     * Convierte Reserva a ReservaResponseDTO (conversión manual).
     */
    private ReservaResponseDTO convertirReservaADto(Reserva reserva) {
        return ReservaResponseDTO.builder()
            .idReserva(reserva.getIdReserva())
            .fechaInicio(reserva.getFechaInicio())
            .fechaFin(reserva.getFechaFin())
            .precio(reserva.getPrecio())
            .metodoPago(reserva.getMetodoPago())
            .estado(reserva.getEstado())
            .usuarioNombre(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido())
            .usuarioEmail(reserva.getUsuario().getEmail())
            .vehiculoMarca(reserva.getVehiculo().getMarca())
            .vehiculoModelo(reserva.getVehiculo().getModelo())
            .vehiculoPatente(reserva.getVehiculo().getPatente())
            .sucursalRetiroNombre(reserva.getSucursalRetiro().getNombre())
            .sucursalRetiroDireccion(reserva.getSucursalRetiro().getDireccion())
            .sucursalDevolucionNombre(reserva.getSucursalDevolucion().getNombre())
            .sucursalDevolucionDireccion(reserva.getSucursalDevolucion().getDireccion())
            .build();
    }

    /**
     * Convierte Reserva a ReservaResponseDTO con información extendida para el dashboard.
     */
    private ReservaResponseDTO convertirReservaADtoExtendido(Reserva reserva) {
        return ReservaResponseDTO.builder()
            .idReserva(reserva.getIdReserva())
            .fechaInicio(reserva.getFechaInicio())
            .fechaFin(reserva.getFechaFin())
            .precio(reserva.getPrecio())
            .metodoPago(reserva.getMetodoPago())
            .estado(reserva.getEstado())
            .usuarioNombre(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido())
            .usuarioEmail(reserva.getUsuario().getEmail())
            .vehiculoMarca(reserva.getVehiculo().getMarca())
            .vehiculoModelo(reserva.getVehiculo().getModelo())
            .vehiculoPatente(reserva.getVehiculo().getPatente())
            .sucursalRetiroNombre(reserva.getSucursalRetiro().getNombre())
            .sucursalRetiroDireccion(reserva.getSucursalRetiro().getDireccion())
            .sucursalDevolucionNombre(reserva.getSucursalDevolucion().getNombre())
            .sucursalDevolucionDireccion(reserva.getSucursalDevolucion().getDireccion())
            .build();
    }

    /**
     * Convierte Vehiculo a VehiculoResponseDTO (conversión manual).
     */
    private VehiculoResponseDTO convertirVehiculoADto(Vehiculo vehiculo) {
        return VehiculoResponseDTO.builder()
            .idVehiculo(vehiculo.getIdVehiculo())
            .patente(vehiculo.getPatente())
            .modelo(vehiculo.getModelo())
            .marca(vehiculo.getMarca())
            .color(vehiculo.getColor())
            .estado(vehiculo.getEstado())
            .cantPuertas(vehiculo.getCantPuertas())
            .descripcion(vehiculo.getDescripcion())
            .imagenUrl(vehiculo.getImagenUrl())
            .precioDiario(vehiculo.getPrecioDiario())
            .tipoMotor(vehiculo.getMotor().getTipoMotor().name())
            .tipoCombustible(vehiculo.getMotor().getTipoCombustible().name())
            .cilindrada(vehiculo.getMotor().getCilindrada())
            .caballosDeFuerza(vehiculo.getMotor().getCaballosDeFuerza())
            .tipoVehiculo(vehiculo.getTipoVehiculo().getTipo())
            .caracteristicasTipo(vehiculo.getTipoVehiculo().getCaracteristicas())
            .sucursalNombre(vehiculo.getSucursal().getNombre())
            .sucursalDireccion(vehiculo.getSucursal().getDireccion())
            .build();
    }
}
