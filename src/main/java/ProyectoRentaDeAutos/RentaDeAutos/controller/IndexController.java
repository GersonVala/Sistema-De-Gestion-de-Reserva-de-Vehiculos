package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.response.SucursalResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.VehiculoResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.service.SucursalService;
import ProyectoRentaDeAutos.RentaDeAutos.service.VehiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la página de inicio de la aplicación.
 *
 * Maneja las rutas públicas para la página principal donde los usuarios
 * pueden ver información general del sistema, buscar vehículos disponibles
 * y acceder a opciones para iniciar sesión o registrarse.
 *
 * Este controlador es accesible sin autenticación.
 */
@Controller
@Slf4j
public class IndexController {

    private final VehiculoService vehiculoService;
    private final SucursalService sucursalService;

    public IndexController(VehiculoService vehiculoService, SucursalService sucursalService) {
        this.vehiculoService = vehiculoService;
        this.sucursalService = sucursalService;
    }

    /**
     * Muestra la página de inicio principal con búsqueda pública de vehículos.
     * GET /
     *
     * Permite a usuarios NO autenticados ver vehículos disponibles.
     * No pueden reservar sin iniciar sesión.
     */
    @GetMapping("/")
    public String mostrarIndex(
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

                log.info("Búsqueda pública: {} vehículos encontrados", vehiculos.size());

            } catch (BusinessException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "index";
    }

    /**
     * Muestra la página de inicio principal (ruta alternativa).
     * GET /index
     *
     * @return vista index
     */
    @GetMapping("/index")
    public String mostrarIndexAlternativo(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {
        return mostrarIndex(sucursalId, fechaInicio, fechaFin, model);
    }

    // ==================== MÉTODOS DE CONVERSIÓN MANUAL ====================

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
     * Convierte Sucursal a SucursalResponseDTO (conversión manual).
     */
    private SucursalResponseDTO convertirSucursalADto(Sucursal sucursal) {
        return SucursalResponseDTO.builder()
            .idSucursal(sucursal.getIdSucursal())
            .nombre(sucursal.getNombre())
            .direccion(sucursal.getDireccion())
            .imagenUrl(sucursal.getImagenUrl())
            .estado(sucursal.getEstado())
            .cantidadVehiculos(0)
            .cantidadEmpleados(0)
            .build();
    }
}
