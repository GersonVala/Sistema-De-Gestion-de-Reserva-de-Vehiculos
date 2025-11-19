package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.request.ReservaRequestDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.ReservaResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.SucursalResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.VehiculoResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Reserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.service.ReservaService;
import ProyectoRentaDeAutos.RentaDeAutos.service.SucursalService;
import ProyectoRentaDeAutos.RentaDeAutos.service.UsuarioService;
import ProyectoRentaDeAutos.RentaDeAutos.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para funcionalidades de CLIENTE.
 * - Buscar vehículos disponibles
 * - Crear y cancelar reservas
 * - Ver historial de reservas
 */
@Controller
@RequestMapping("/cliente")
@Slf4j
public class ClienteController {

    private final VehiculoService vehiculoService;
    private final ReservaService reservaService;
    private final SucursalService sucursalService;
    private final UsuarioService usuarioService;

    public ClienteController(VehiculoService vehiculoService,
                            ReservaService reservaService,
                            SucursalService sucursalService,
                            UsuarioService usuarioService) {
        this.vehiculoService = vehiculoService;
        this.reservaService = reservaService;
        this.sucursalService = sucursalService;
        this.usuarioService = usuarioService;
    }

    /**
     * Dashboard del cliente.
     * GET /cliente/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email);

        model.addAttribute("usuario", usuario);
        return "cliente/dashboard";
    }

    /**
     * Página de búsqueda de vehículos.
     * GET /cliente/vehiculos
     */
    @GetMapping("/vehiculos")
    public String buscarVehiculos(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // Cargar sucursales para el filtro (convertir a DTO)
        List<Sucursal> sucursales = sucursalService.obtenerActivas();
        List<SucursalResponseDTO> sucursalesDTO = sucursales.stream()
            .map(this::convertirSucursalADto)
            .collect(Collectors.toList());
        model.addAttribute("sucursales", sucursalesDTO);

        // Si hay parámetros de búsqueda, buscar vehículos
        if (sucursalId != null && fechaInicio != null && fechaFin != null) {
            try {
                List<Vehiculo> vehiculos = vehiculoService.buscarDisponiblesEnFechas(
                    sucursalId, fechaInicio, fechaFin
                );

                // Convertir a DTOs
                List<VehiculoResponseDTO> vehiculosDTO = vehiculos.stream()
                    .map(this::convertirVehiculoADto)
                    .collect(Collectors.toList());

                model.addAttribute("vehiculos", vehiculosDTO);
                model.addAttribute("sucursalId", sucursalId);
                model.addAttribute("fechaInicio", fechaInicio);
                model.addAttribute("fechaFin", fechaFin);

                log.info("Búsqueda: {} vehículos encontrados", vehiculos.size());

            } catch (BusinessException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "cliente/vehiculos";
    }

    /**
     * Crear una nueva reserva.
     * POST /cliente/reservas
     */
    @PostMapping("/reservas")
    public String crearReserva(
            @Valid @ModelAttribute ReservaRequestDTO reservaDTO,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Intento de crear reserva para vehículo ID: {}", reservaDTO.getVehiculoId());

        // Validar errores de Bean Validation
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos de reserva inválidos");
            return "redirect:/cliente/vehiculos";
        }

        // Validar que las fechas sean coherentes
        if (!reservaDTO.fechasValidas()) {
            redirectAttributes.addFlashAttribute("error", "La fecha de fin debe ser posterior a la de inicio");
            return "redirect:/cliente/vehiculos";
        }

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerPorEmail(email);

            // Convertir DTO a Entity (conversión manual)
            Reserva reserva = convertirDtoAReserva(reservaDTO, usuario);

            // Crear reserva
            Reserva reservaCreada = reservaService.crearReserva(reserva);

            redirectAttributes.addFlashAttribute("success",
                "Reserva creada exitosamente. ID: " + reservaCreada.getIdReserva());

            log.info("Reserva {} creada por usuario {}", reservaCreada.getIdReserva(), email);

            return "redirect:/cliente/mis-reservas";

        } catch (BusinessException | ResourceNotFoundException e) {
            log.error("Error al crear reserva: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cliente/vehiculos";
        }
    }

    /**
     * Ver mis reservas.
     * GET /cliente/mis-reservas
     */
    @GetMapping("/mis-reservas")
    public String misReservas(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email);

        List<Reserva> reservas = reservaService.obtenerPorUsuario(usuario.getIdUsuario());

        // Convertir a DTOs
        List<ReservaResponseDTO> reservasDTO = reservas.stream()
            .map(this::convertirReservaADto)
            .collect(Collectors.toList());

        model.addAttribute("reservas", reservasDTO);

        return "cliente/mis-reservas";
    }

    /**
     * Cancelar una reserva.
     * POST /cliente/reservas/{id}/cancelar
     */
    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReserva(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerPorEmail(email);

            // Verificar que la reserva pertenece al usuario
            Reserva reserva = reservaService.obtenerPorId(id);
            if (!reserva.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new BusinessException("No tiene permisos para cancelar esta reserva");
            }

            reservaService.cancelarReserva(id);

            redirectAttributes.addFlashAttribute("success", "Reserva cancelada exitosamente");
            log.info("Reserva {} cancelada por usuario {}", id, email);

        } catch (BusinessException | ResourceNotFoundException e) {
            log.error("Error al cancelar reserva: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cliente/mis-reservas";
    }

    // ==================== MÉTODOS DE CONVERSIÓN MANUAL ====================

    /**
     * Convierte ReservaRequestDTO a Reserva (conversión manual).
     */
    private Reserva convertirDtoAReserva(ReservaRequestDTO dto, Usuario usuario) {
        Reserva reserva = new Reserva();
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setMetodoPago(dto.getMetodoPago());

        // Asignar relaciones (el servicio las validará)
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(dto.getVehiculoId());
        reserva.setVehiculo(vehiculo);

        Sucursal sucursalRetiro = new Sucursal();
        sucursalRetiro.setIdSucursal(dto.getSucursalRetiroId());
        reserva.setSucursalRetiro(sucursalRetiro);

        Sucursal sucursalDevolucion = new Sucursal();
        sucursalDevolucion.setIdSucursal(dto.getSucursalDevolucionId());
        reserva.setSucursalDevolucion(sucursalDevolucion);

        reserva.setUsuario(usuario);

        return reserva;
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
     * Convierte Sucursal a SucursalResponseDTO (conversión manual).
     */
    private SucursalResponseDTO convertirSucursalADto(Sucursal sucursal) {
        return SucursalResponseDTO.builder()
            .idSucursal(sucursal.getIdSucursal())
            .nombre(sucursal.getNombre())
            .direccion(sucursal.getDireccion())
            .imagenUrl(sucursal.getImagenUrl())
            .estado(sucursal.getEstado())
            .cantidadVehiculos(0) // No calculamos aquí, se puede obtener del servicio si es necesario
            .cantidadEmpleados(0) // No calculamos aquí, se puede obtener del servicio si es necesario
            .build();
    }
}
