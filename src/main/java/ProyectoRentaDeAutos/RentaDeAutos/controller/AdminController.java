package ProyectoRentaDeAutos.RentaDeAutos.controller;

import ProyectoRentaDeAutos.RentaDeAutos.dto.request.SucursalRequestDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.request.VehiculoRequestDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.ReservaResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.SucursalResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.dto.response.VehiculoResponseDTO;
import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.*;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoReserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para funcionalidades de ADMIN.
 * - CRUD de Sucursales
 * - CRUD de Vehículos
 * - CRUD de Empleados (Vendedores)
 * - Ver todas las reservas
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final SucursalService sucursalService;
    private final VehiculoService vehiculoService;
    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;
    private final MotorService motorService;
    private final TipoVehiculoService tipoVehiculoService;
    private final ProyectoRentaDeAutos.RentaDeAutos.repository.MotorRepository motorRepository;
    private final ProyectoRentaDeAutos.RentaDeAutos.repository.TipoVehiculoRepository tipoVehiculoRepository;
    private final ProyectoRentaDeAutos.RentaDeAutos.repository.RolRepository rolRepository;

    public AdminController(SucursalService sucursalService,
                          VehiculoService vehiculoService,
                          EmpleadoService empleadoService,
                          UsuarioService usuarioService,
                          ReservaService reservaService,
                          MotorService motorService,
                          TipoVehiculoService tipoVehiculoService,
                          ProyectoRentaDeAutos.RentaDeAutos.repository.MotorRepository motorRepository,
                          ProyectoRentaDeAutos.RentaDeAutos.repository.TipoVehiculoRepository tipoVehiculoRepository,
                          ProyectoRentaDeAutos.RentaDeAutos.repository.RolRepository rolRepository) {
        this.sucursalService = sucursalService;
        this.vehiculoService = vehiculoService;
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
        this.motorService = motorService;
        this.tipoVehiculoService = tipoVehiculoService;
        this.motorRepository = motorRepository;
        this.tipoVehiculoRepository = tipoVehiculoRepository;
        this.rolRepository = rolRepository;
    }

    /**
     * Dashboard del administrador.
     * GET /admin/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        long totalSucursales = sucursalService.obtenerTodas().size();
        long totalVehiculos = vehiculoService.obtenerTodos().size();
        long totalEmpleados = empleadoService.obtenerTodos().size();
        long reservasPendientes = reservaService.obtenerTodasPorEstado(EstadoReserva.PENDIENTE).size();

        model.addAttribute("totalSucursales", totalSucursales);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalEmpleados", totalEmpleados);
        model.addAttribute("reservasPendientes", reservasPendientes);

        return "admin/dashboard";
    }

    // ========== CRUD SUCURSALES ==========

    /**
     * Listar todas las sucursales.
     * GET /admin/sucursales
     */
    @GetMapping("/sucursales")
    public String listarSucursales(Model model) {
        List<Sucursal> sucursales = sucursalService.obtenerTodas();

        // Convertir a DTOs
        List<SucursalResponseDTO> sucursalesDTO = sucursales.stream()
            .map(this::convertirSucursalADto)
            .collect(Collectors.toList());

        model.addAttribute("sucursales", sucursalesDTO);
        return "admin/sucursales/lista";
    }

    /**
     * Formulario para crear sucursal.
     * GET /admin/sucursales/nueva
     */
    @GetMapping("/sucursales/nueva")
    public String formularioNuevaSucursal(Model model) {
        model.addAttribute("sucursalDTO", new SucursalRequestDTO());
        return "admin/sucursales/formulario";
    }

    /**
     * Crear nueva sucursal.
     * POST /admin/sucursales
     */
    @PostMapping("/sucursales")
    public String crearSucursal(
            @Valid @ModelAttribute("sucursalDTO") SucursalRequestDTO sucursalDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "admin/sucursales/formulario";
        }

        try {
            // Convertir DTO a Entity
            Sucursal sucursal = convertirDtoASucursal(sucursalDTO);
            sucursalService.crearSucursal(sucursal);

            redirectAttributes.addFlashAttribute("success", "Sucursal creada exitosamente");
            return "redirect:/admin/dashboard";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/sucursales/formulario";
        }
    }

    /**
     * Formulario para editar sucursal.
     * GET /admin/sucursales/{id}/editar
     */
    @GetMapping("/sucursales/{id}/editar")
    public String formularioEditarSucursal(@PathVariable Long id, Model model) {
        try {
            Sucursal sucursal = sucursalService.obtenerPorId(id);

            // Convertir a DTO para edición
            SucursalRequestDTO sucursalDTO = new SucursalRequestDTO(
                sucursal.getNombre(),
                sucursal.getDireccion(),
                sucursal.getImagenUrl()
            );

            model.addAttribute("sucursalDTO", sucursalDTO);
            model.addAttribute("idSucursal", id);
            return "admin/sucursales/formulario";
        } catch (ResourceNotFoundException e) {
            return "redirect:/admin/sucursales";
        }
    }

    /**
     * Actualizar sucursal.
     * POST /admin/sucursales/{id}
     */
    @PostMapping("/sucursales/{id}")
    public String actualizarSucursal(
            @PathVariable Long id,
            @Valid @ModelAttribute("sucursalDTO") SucursalRequestDTO sucursalDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("idSucursal", id);
            return "admin/sucursales/formulario";
        }

        try {
            // Convertir DTO a Entity
            Sucursal sucursal = convertirDtoASucursal(sucursalDTO);
            sucursalService.actualizarSucursal(id, sucursal);

            redirectAttributes.addFlashAttribute("success", "Sucursal actualizada exitosamente");
            return "redirect:/admin/dashboard";
        } catch (BusinessException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idSucursal", id);
            return "admin/sucursales/formulario";
        }
    }

    /**
     * Eliminar sucursal.
     * POST /admin/sucursales/{id}/eliminar
     */
    @PostMapping("/sucursales/{id}/eliminar")
    public String eliminarSucursal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sucursalService.eliminarSucursal(id);
            redirectAttributes.addFlashAttribute("success", "Sucursal eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/sucursales";
    }

    /**
     * Desactivar una sucursal.
     * POST /admin/sucursales/{id}/desactivar
     */
    @PostMapping("/sucursales/{id}/desactivar")
    public String desactivarSucursal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sucursalService.desactivarSucursal(id);
            redirectAttributes.addFlashAttribute("success", "Sucursal desactivada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
        }
        return "redirect:/admin/sucursales";
    }

    /**
     * Activar una sucursal.
     * POST /admin/sucursales/{id}/activar
     */
    @PostMapping("/sucursales/{id}/activar")
    public String activarSucursal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sucursalService.activarSucursal(id);
            redirectAttributes.addFlashAttribute("success", "Sucursal activada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar: " + e.getMessage());
        }
        return "redirect:/admin/sucursales";
    }

    // ========== CRUD VEHÍCULOS ==========

    /**
     * Listar todos los vehículos con filtro opcional por sucursal.
     * GET /admin/vehiculos
     * GET /admin/vehiculos?sucursalId={id}
     */
    @GetMapping("/vehiculos")
    public String listarVehiculos(
            @RequestParam(required = false) Long sucursalId,
            Model model) {

        List<Vehiculo> vehiculos;

        if (sucursalId != null) {
            // Filtrar por sucursal específica
            Sucursal sucursal = sucursalService.obtenerPorId(sucursalId);
            vehiculos = vehiculoService.obtenerTodos().stream()
                .filter(v -> v.getSucursal().getIdSucursal().equals(sucursalId))
                .collect(Collectors.toList());
            model.addAttribute("sucursalSeleccionada", sucursal);
        } else {
            // Obtener todos
            vehiculos = vehiculoService.obtenerTodos();
        }

        // Obtener todas las sucursales para el filtro
        List<Sucursal> sucursales = sucursalService.obtenerActivas();

        // Enviar entidades directamente (el template espera navegación de objetos)
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("sucursalIdFiltro", sucursalId);

        return "admin/vehiculos/lista";
    }

    /**
     * Formulario para crear vehículo.
     * GET /admin/vehiculos/nuevo
     */
    @GetMapping("/vehiculos/nuevo")
    public String formularioNuevoVehiculo(Model model) {
        model.addAttribute("vehiculoDTO", new VehiculoRequestDTO());
        model.addAttribute("sucursales", sucursalService.obtenerActivas());
        model.addAttribute("motores", motorRepository.findAll());
        model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
        return "admin/vehiculos/formulario";
    }

    /**
     * Crear nuevo vehículo.
     * POST /admin/vehiculos
     */
    @PostMapping("/vehiculos")
    public String crearVehiculo(
            @Valid @ModelAttribute("vehiculoDTO") VehiculoRequestDTO vehiculoDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("sucursales", sucursalService.obtenerActivas());
            model.addAttribute("motores", motorRepository.findAll());
            model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
            return "admin/vehiculos/formulario";
        }

        try {
            // Convertir DTO a Entity
            Vehiculo vehiculo = convertirDtoAVehiculo(vehiculoDTO);
            vehiculoService.crearVehiculo(vehiculo);

            redirectAttributes.addFlashAttribute("success", "Vehículo creado exitosamente");
            return "redirect:/admin/vehiculos";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sucursales", sucursalService.obtenerActivas());
            model.addAttribute("motores", motorRepository.findAll());
            model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
            return "admin/vehiculos/formulario";
        }
    }

    /**
     * Formulario para editar vehículo.
     * GET /admin/vehiculos/{id}/editar
     */
    @GetMapping("/vehiculos/{id}/editar")
    public String formularioEditarVehiculo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Vehiculo vehiculo = vehiculoService.obtenerPorId(id);

            // Convertir a DTO para edición
            VehiculoRequestDTO vehiculoDTO = new VehiculoRequestDTO(
                vehiculo.getPatente(),
                vehiculo.getModelo(),
                vehiculo.getMarca(),
                vehiculo.getColor(),
                vehiculo.getCantPuertas(),
                vehiculo.getDescripcion(),
                vehiculo.getImagenUrl(),
                vehiculo.getPrecioDiario(),
                vehiculo.getMotor().getIdMotor(),
                vehiculo.getTipoVehiculo().getIdTipoVehiculo(),
                vehiculo.getSucursal().getIdSucursal(),
                vehiculo.getEstado()
            );

            model.addAttribute("vehiculoDTO", vehiculoDTO);
            model.addAttribute("idVehiculo", id);
            model.addAttribute("sucursales", sucursalService.obtenerActivas());
            model.addAttribute("motores", motorRepository.findAll());
            model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
            model.addAttribute("estadosVehiculo", EstadoVehiculo.values());

            return "admin/vehiculos/formulario";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado");
            return "redirect:/admin/vehiculos";
        }
    }

    /**
     * Actualizar vehículo.
     * POST /admin/vehiculos/{id}
     */
    @PostMapping("/vehiculos/{id}")
    public String actualizarVehiculo(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehiculoDTO") VehiculoRequestDTO vehiculoDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("idVehiculo", id);
            model.addAttribute("sucursales", sucursalService.obtenerActivas());
            model.addAttribute("motores", motorRepository.findAll());
            model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
            model.addAttribute("estadosVehiculo", EstadoVehiculo.values());
            return "admin/vehiculos/formulario";
        }

        try {
            // Convertir DTO a Entity
            Vehiculo vehiculoActualizado = convertirDtoAVehiculo(vehiculoDTO);
            vehiculoService.actualizarVehiculo(id, vehiculoActualizado);

            redirectAttributes.addFlashAttribute("success", "Vehículo actualizado exitosamente");
            return "redirect:/admin/vehiculos";
        } catch (BusinessException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idVehiculo", id);
            model.addAttribute("sucursales", sucursalService.obtenerActivas());
            model.addAttribute("motores", motorRepository.findAll());
            model.addAttribute("tiposVehiculo", tipoVehiculoRepository.findAll());
            model.addAttribute("estadosVehiculo", EstadoVehiculo.values());
            return "admin/vehiculos/formulario";
        }
    }

    /**
     * Eliminar vehículo.
     * POST /admin/vehiculos/{id}/eliminar
     */
    @PostMapping("/vehiculos/{id}/eliminar")
    public String eliminarVehiculo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.eliminarVehiculo(id);
            redirectAttributes.addFlashAttribute("success", "Vehículo eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/vehiculos";
    }

    /**
     * Ver detalle completo de un vehículo con información de alquiler.
     * GET /admin/vehiculos/{id}/detalle
     */
    @GetMapping("/vehiculos/{id}/detalle")
    public String verDetalleVehiculo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Vehiculo vehiculo = vehiculoService.obtenerPorId(id);

            // Enviar entidad directamente (el template espera navegación de objetos)
            model.addAttribute("vehiculo", vehiculo);

            // Si el vehículo está RESERVADO o ENTREGADO, buscar la reserva activa
            if (vehiculo.getEstado() == EstadoVehiculo.RESERVADO || vehiculo.getEstado() == EstadoVehiculo.ENTREGADO) {
                // Buscar reservas activas (PENDIENTE o ACEPTADA) para este vehículo
                List<EstadoReserva> estadosActivos = List.of(EstadoReserva.PENDIENTE, EstadoReserva.ACEPTADA);
                List<Reserva> reservasActivas = reservaService.obtenerPorVehiculoYEstados(id, estadosActivos);

                if (!reservasActivas.isEmpty()) {
                    // Tomar la reserva más reciente
                    Reserva reservaActual = reservasActivas.get(0);
                    model.addAttribute("reservaActual", reservaActual);
                }
            }

            // Obtener historial de reservas para este vehículo (todas)
            List<Reserva> todasReservas = reservaService.obtenerPorVehiculo(id);
            model.addAttribute("todasReservas", todasReservas);

            return "admin/vehiculos/detalle";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado");
            return "redirect:/admin/vehiculos";
        }
    }

    // ========== CRUD MOTORES ==========

    /**
     * Listar todos los motores.
     * GET /admin/motores
     */
    @GetMapping("/motores")
    public String listarMotores(Model model) {
        List<Motor> motores = motorRepository.findAll();
        model.addAttribute("motores", motores);
        return "admin/motores/lista";
    }

    /**
     * Formulario para crear motor.
     * GET /admin/motores/nuevo
     */
    @GetMapping("/motores/nuevo")
    public String formularioNuevoMotor(Model model) {
        model.addAttribute("motor", new Motor());
        return "admin/motores/formulario";
    }

    /**
     * Crear nuevo motor.
     * POST /admin/motores
     */
    @PostMapping("/motores")
    public String crearMotor(
            @Valid @ModelAttribute("motor") Motor motor,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "admin/motores/formulario";
        }

        try {
            motorService.crearMotor(motor);
            redirectAttributes.addFlashAttribute("success", "Motor creado exitosamente");
            return "redirect:/admin/motores";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/motores/formulario";
        }
    }

    /**
     * Formulario para editar motor.
     * GET /admin/motores/{id}/editar
     */
    @GetMapping("/motores/{id}/editar")
    public String formularioEditarMotor(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Motor motor = motorService.obtenerPorId(id);
            model.addAttribute("motor", motor);
            model.addAttribute("idMotor", id);
            return "admin/motores/formulario";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Motor no encontrado");
            return "redirect:/admin/motores";
        }
    }

    /**
     * Actualizar motor.
     * POST /admin/motores/{id}
     */
    @PostMapping("/motores/{id}")
    public String actualizarMotor(
            @PathVariable Long id,
            @Valid @ModelAttribute("motor") Motor motor,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("idMotor", id);
            return "admin/motores/formulario";
        }

        try {
            motorService.actualizarMotor(id, motor);
            redirectAttributes.addFlashAttribute("success", "Motor actualizado exitosamente");
            return "redirect:/admin/motores";
        } catch (BusinessException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idMotor", id);
            return "admin/motores/formulario";
        }
    }

    /**
     * Eliminar motor.
     * POST /admin/motores/{id}/eliminar
     */
    @PostMapping("/motores/{id}/eliminar")
    public String eliminarMotor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            motorService.eliminarMotor(id);
            redirectAttributes.addFlashAttribute("success", "Motor eliminado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/motores";
    }

    // ========== CRUD TIPOS DE VEHÍCULO ==========

    /**
     * Listar todos los tipos de vehículos.
     * GET /admin/tipos-vehiculo
     */
    @GetMapping("/tipos-vehiculo")
    public String listarTiposVehiculo(Model model) {
        List<TipoVehiculo> tiposVehiculo = tipoVehiculoRepository.findAll();
        model.addAttribute("tiposVehiculo", tiposVehiculo);
        return "admin/tipos-vehiculo/lista";
    }

    /**
     * Formulario para crear tipo de vehículo.
     * GET /admin/tipos-vehiculo/nuevo
     */
    @GetMapping("/tipos-vehiculo/nuevo")
    public String formularioNuevoTipoVehiculo(Model model) {
        model.addAttribute("tipoVehiculo", new TipoVehiculo());
        return "admin/tipos-vehiculo/formulario";
    }

    /**
     * Crear nuevo tipo de vehículo.
     * POST /admin/tipos-vehiculo
     */
    @PostMapping("/tipos-vehiculo")
    public String crearTipoVehiculo(
            @Valid @ModelAttribute("tipoVehiculo") TipoVehiculo tipoVehiculo,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "admin/tipos-vehiculo/formulario";
        }

        try {
            tipoVehiculoService.crearTipoVehiculo(tipoVehiculo);
            redirectAttributes.addFlashAttribute("success", "Tipo de vehículo creado exitosamente");
            return "redirect:/admin/tipos-vehiculo";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/tipos-vehiculo/formulario";
        }
    }

    /**
     * Formulario para editar tipo de vehículo.
     * GET /admin/tipos-vehiculo/{id}/editar
     */
    @GetMapping("/tipos-vehiculo/{id}/editar")
    public String formularioEditarTipoVehiculo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TipoVehiculo tipoVehiculo = tipoVehiculoService.obtenerPorId(id);
            model.addAttribute("tipoVehiculo", tipoVehiculo);
            model.addAttribute("idTipoVehiculo", id);
            return "admin/tipos-vehiculo/formulario";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Tipo de vehículo no encontrado");
            return "redirect:/admin/tipos-vehiculo";
        }
    }

    /**
     * Actualizar tipo de vehículo.
     * POST /admin/tipos-vehiculo/{id}
     */
    @PostMapping("/tipos-vehiculo/{id}")
    public String actualizarTipoVehiculo(
            @PathVariable Long id,
            @Valid @ModelAttribute("tipoVehiculo") TipoVehiculo tipoVehiculo,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("idTipoVehiculo", id);
            return "admin/tipos-vehiculo/formulario";
        }

        try {
            tipoVehiculoService.actualizarTipoVehiculo(id, tipoVehiculo);
            redirectAttributes.addFlashAttribute("success", "Tipo de vehículo actualizado exitosamente");
            return "redirect:/admin/tipos-vehiculo";
        } catch (BusinessException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idTipoVehiculo", id);
            return "admin/tipos-vehiculo/formulario";
        }
    }

    /**
     * Eliminar tipo de vehículo.
     * POST /admin/tipos-vehiculo/{id}/eliminar
     */
    @PostMapping("/tipos-vehiculo/{id}/eliminar")
    public String eliminarTipoVehiculo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tipoVehiculoService.eliminarTipoVehiculo(id);
            redirectAttributes.addFlashAttribute("success", "Tipo de vehículo eliminado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/tipos-vehiculo";
    }

    // ========== CRUD EMPLEADOS ==========

    /**
     * Listar todos los empleados.
     * GET /admin/empleados
     */
    @GetMapping("/empleados")
    public String listarEmpleados(Model model) {
        List<Empleado> empleados = empleadoService.obtenerTodos();
        model.addAttribute("empleados", empleados);
        return "admin/empleados/lista";
    }

    /**
     * Formulario para crear empleado.
     * GET /admin/empleados/nuevo
     */
    @GetMapping("/empleados/nuevo")
    public String formularioNuevoEmpleado(Model model) {
        // Listar usuarios con rol VENDEDOR que no sean empleados
        List<Usuario> vendedores = usuarioService.obtenerPorRol("VENDEDOR");
        List<Sucursal> sucursales = sucursalService.obtenerActivas();

        model.addAttribute("vendedores", vendedores);
        model.addAttribute("sucursales", sucursales);
        return "admin/empleados/formulario";
    }

    /**
     * Crear nuevo empleado.
     * POST /admin/empleados
     */
    @PostMapping("/empleados")
    public String crearEmpleado(
            @RequestParam Long usuarioId,
            @RequestParam Long sucursalId,
            RedirectAttributes redirectAttributes) {

        try {
            empleadoService.crearEmpleado(usuarioId, sucursalId);
            redirectAttributes.addFlashAttribute("success", "Empleado asignado exitosamente");
            return "redirect:/admin/dashboard";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/empleados/nuevo";
        }
    }

    /**
     * Eliminar empleado.
     * POST /admin/empleados/{id}/eliminar
     */
    @PostMapping("/empleados/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.eliminarEmpleado(id);
            redirectAttributes.addFlashAttribute("success", "Empleado eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/empleados";
    }

    /**
     * Formulario para reasignar empleado a otra sucursal.
     * GET /admin/empleados/{id}/reasignar
     */
    @GetMapping("/empleados/{id}/reasignar")
    public String formularioReasignarEmpleado(@PathVariable Long id, Model model) {
        try {
            Empleado empleado = empleadoService.obtenerPorId(id);
            List<Sucursal> sucursales = sucursalService.obtenerActivas();

            model.addAttribute("empleado", empleado);
            model.addAttribute("sucursales", sucursales);
            return "admin/empleados/reasignar";
        } catch (Exception e) {
            return "redirect:/admin/empleados";
        }
    }

    /**
     * Reasignar empleado a otra sucursal.
     * POST /admin/empleados/{id}/reasignar
     */
    @PostMapping("/empleados/{id}/reasignar")
    public String reasignarEmpleado(
            @PathVariable Long id,
            @RequestParam Long sucursalId,
            RedirectAttributes redirectAttributes) {

        try {
            Empleado empleado = empleadoService.cambiarSucursal(id, sucursalId);

            redirectAttributes.addFlashAttribute("success",
                "Empleado reasignado exitosamente a " + empleado.getSucursal().getNombre());
            return "redirect:/admin/empleados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reasignar: " + e.getMessage());
            return "redirect:/admin/empleados/" + id + "/reasignar";
        }
    }

    /**
     * Activar empleado.
     * POST /admin/empleados/{id}/activar
     */
    @PostMapping("/empleados/{id}/activar")
    public String activarEmpleado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.activarEmpleado(id);
            redirectAttributes.addFlashAttribute("success", "Empleado activado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar: " + e.getMessage());
        }
        return "redirect:/admin/empleados";
    }

    /**
     * Desactivar empleado.
     * POST /admin/empleados/{id}/desactivar
     */
    @PostMapping("/empleados/{id}/desactivar")
    public String desactivarEmpleado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.desactivarEmpleado(id);
            redirectAttributes.addFlashAttribute("success", "Empleado desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
        }
        return "redirect:/admin/empleados";
    }

    // ========== VER RESERVAS ==========

    /**
     * Ver todas las reservas del sistema.
     * GET /admin/reservas
     */
    @GetMapping("/reservas")
    public String verReservas(
            @RequestParam(required = false) String estado,
            Model model) {

        List<Reserva> reservas;

        if (estado != null && !estado.isEmpty()) {
            reservas = reservaService.obtenerTodasPorEstado(EstadoReserva.valueOf(estado));
        } else {
            // Obtener todas (implementar método en servicio si no existe)
            reservas = reservaService.obtenerTodasPorEstado(EstadoReserva.PENDIENTE);
            reservas.addAll(reservaService.obtenerTodasPorEstado(EstadoReserva.ACEPTADA));
            reservas.addAll(reservaService.obtenerTodasPorEstado(EstadoReserva.CANCELADA));
        }

        // Convertir a DTOs
        List<ReservaResponseDTO> reservasDTO = reservas.stream()
            .map(this::convertirReservaADto)
            .collect(Collectors.toList());

        model.addAttribute("reservas", reservasDTO);
        model.addAttribute("estadoFiltro", estado);

        return "admin/reservas/lista";
    }

    /**
     * Aceptar una reserva.
     * POST /admin/reservas/{id}/aceptar
     */
    @PostMapping("/reservas/{id}/aceptar")
    public String aceptarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.aceptarReserva(id);
            redirectAttributes.addFlashAttribute("success", "Reserva aceptada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aceptar: " + e.getMessage());
        }
        return "redirect:/admin/reservas";
    }

    /**
     * Rechazar/Cancelar una reserva.
     * POST /admin/reservas/{id}/rechazar
     */
    @PostMapping("/reservas/{id}/rechazar")
    public String rechazarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.cancelarReserva(id);
            redirectAttributes.addFlashAttribute("success", "Reserva rechazada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar: " + e.getMessage());
        }
        return "redirect:/admin/reservas";
    }

    // ========== GESTIÓN DE USUARIOS ==========

    /**
     * Listar todos los usuarios del sistema.
     * GET /admin/usuarios
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/lista";
    }

    /**
     * Formulario para editar el rol de un usuario.
     * GET /admin/usuarios/{id}/editar-rol
     */
    @GetMapping("/usuarios/{id}/editar-rol")
    public String formularioEditarRol(@PathVariable Long id, Model model) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            List<Rol> roles = rolRepository.findAll();
            List<Sucursal> sucursales = sucursalService.obtenerActivas();

            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", roles);
            model.addAttribute("sucursales", sucursales);

            // Si el usuario ya es VENDEDOR, obtener su sucursal actual
            if ("VENDEDOR".equals(usuario.getRol().getNombre())) {
                try {
                    Empleado empleado = empleadoService.obtenerPorUsuario(id);
                    model.addAttribute("sucursalActual", empleado.getSucursal());
                } catch (ResourceNotFoundException e) {
                    // Usuario es VENDEDOR pero no tiene empleado asignado (caso de error)
                    model.addAttribute("advertencia",
                        "Este usuario tiene rol VENDEDOR pero no está asignado a ninguna sucursal. Seleccione una sucursal.");
                }
            }

            return "admin/usuarios/editar-rol";
        } catch (Exception e) {
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Actualizar el rol de un usuario.
     * POST /admin/usuarios/{id}/cambiar-rol
     */
    @PostMapping("/usuarios/{id}/cambiar-rol")
    public String cambiarRolUsuario(
            @PathVariable Long id,
            @RequestParam Long rolId,
            @RequestParam(required = false) Long sucursalId,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            Rol nuevoRol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

            String rolAnterior = usuario.getRol().getNombre();
            String rolNuevo = nuevoRol.getNombre();

            // Si está cambiando a VENDEDOR, verificar que se proporcionó sucursal
            if ("VENDEDOR".equals(rolNuevo) && sucursalId == null) {
                // Necesita seleccionar una sucursal
                redirectAttributes.addFlashAttribute("error",
                    "Para asignar el rol VENDEDOR es necesario seleccionar una sucursal");
                return "redirect:/admin/usuarios/" + id + "/editar-rol";
            }

            // Actualizar SOLO el rol sin modificar la contraseña
            // Usar el método cambiarRol() que NO re-hashea la contraseña
            usuarioService.cambiarRol(id, rolId);

            // Si cambió a VENDEDOR, crear entrada en empleados o reactivar existente
            if ("VENDEDOR".equals(rolNuevo)) {
                try {
                    // Intentar obtener empleado existente (incluyendo desactivados)
                    Empleado empleadoExistente = empleadoService.obtenerPorUsuario(id);

                    // Si existe pero está desactivado, reactivarlo
                    if (!empleadoExistente.getEstado()) {
                        empleadoService.activarEmpleado(empleadoExistente.getIdEmpleado());

                        // Si se especificó una sucursal diferente, cambiarla
                        if (sucursalId != null && !empleadoExistente.getSucursal().getIdSucursal().equals(sucursalId)) {
                            empleadoService.cambiarSucursal(empleadoExistente.getIdEmpleado(), sucursalId);
                            redirectAttributes.addFlashAttribute("success",
                                "Rol actualizado a VENDEDOR, empleado reactivado y reasignado a nueva sucursal exitosamente");
                        } else {
                            redirectAttributes.addFlashAttribute("success",
                                "Rol actualizado a VENDEDOR y empleado reactivado exitosamente");
                        }
                    } else {
                        // Ya existe y está activo, actualizar sucursal si cambió
                        if (sucursalId != null && !empleadoExistente.getSucursal().getIdSucursal().equals(sucursalId)) {
                            empleadoService.cambiarSucursal(empleadoExistente.getIdEmpleado(), sucursalId);
                            redirectAttributes.addFlashAttribute("success",
                                "Rol actualizado y empleado reasignado a nueva sucursal exitosamente");
                        } else {
                            redirectAttributes.addFlashAttribute("success",
                                "Rol actualizado exitosamente. El empleado ya existía.");
                        }
                    }
                } catch (ResourceNotFoundException e) {
                    // No existe empleado, crear uno nuevo
                    empleadoService.crearEmpleado(id, sucursalId);
                    redirectAttributes.addFlashAttribute("success",
                        "Rol actualizado a VENDEDOR y asignado a sucursal exitosamente");
                }
            }
            // Si cambió DESDE VENDEDOR a otro rol, desactivar el empleado
            else if ("VENDEDOR".equals(rolAnterior)) {
                try {
                    Empleado empleado = empleadoService.obtenerPorUsuario(id);
                    empleadoService.desactivarEmpleado(empleado.getIdEmpleado());
                    redirectAttributes.addFlashAttribute("success",
                        "Rol actualizado y empleado desactivado exitosamente");
                } catch (ResourceNotFoundException e) {
                    // No había empleado, solo actualizar mensaje
                    redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente");
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente");
            }

            return "redirect:/admin/usuarios";
        } catch (BusinessException e) {
            log.error("Error de negocio al actualizar rol: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar-rol";
        } catch (Exception e) {
            log.error("Error al actualizar rol: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar rol: " + e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar-rol";
        }
    }

    /**
     * Desactivar un usuario.
     * POST /admin/usuarios/{id}/desactivar
     */
    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    /**
     * Activar un usuario.
     * POST /admin/usuarios/{id}/activar
     */
    @PostMapping("/usuarios/{id}/activar")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario activado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // ==================== MÉTODOS DE CONVERSIÓN MANUAL ====================

    /**
     * Convierte SucursalRequestDTO a Sucursal (conversión manual).
     */
    private Sucursal convertirDtoASucursal(SucursalRequestDTO dto) {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setImagenUrl(dto.getImagenUrl());
        return sucursal;
    }

    /**
     * Convierte Sucursal a SucursalResponseDTO (conversión manual).
     */
    private SucursalResponseDTO convertirSucursalADto(Sucursal sucursal) {
        // Contar vehículos de esta sucursal
        long cantVehiculos = vehiculoService.obtenerTodos().stream()
            .filter(v -> v.getSucursal().getIdSucursal().equals(sucursal.getIdSucursal()))
            .count();

        // Contar empleados de esta sucursal
        long cantEmpleados = empleadoService.obtenerTodos().stream()
            .filter(e -> e.getSucursal().getIdSucursal().equals(sucursal.getIdSucursal()))
            .count();

        return SucursalResponseDTO.builder()
            .idSucursal(sucursal.getIdSucursal())
            .nombre(sucursal.getNombre())
            .direccion(sucursal.getDireccion())
            .imagenUrl(sucursal.getImagenUrl())
            .estado(sucursal.getEstado())
            .cantidadVehiculos((int) cantVehiculos)
            .cantidadEmpleados((int) cantEmpleados)
            .build();
    }

    /**
     * Convierte VehiculoRequestDTO a Vehiculo (conversión manual).
     */
    private Vehiculo convertirDtoAVehiculo(VehiculoRequestDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(dto.getPatente());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setColor(dto.getColor());
        vehiculo.setCantPuertas(dto.getCantPuertas());
        vehiculo.setDescripcion(dto.getDescripcion());
        vehiculo.setImagenUrl(dto.getImagenUrl());
        vehiculo.setPrecioDiario(dto.getPrecioDiario());
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE); // Nuevo vehículo siempre DISPONIBLE

        // Asignar relaciones (el servicio las validará)
        Motor motor = new Motor();
        motor.setIdMotor(dto.getMotorId());
        vehiculo.setMotor(motor);

        TipoVehiculo tipoVehiculo = new TipoVehiculo();
        tipoVehiculo.setIdTipoVehiculo(dto.getTipoVehiculoId());
        vehiculo.setTipoVehiculo(tipoVehiculo);

        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(dto.getSucursalId());
        vehiculo.setSucursal(sucursal);

        return vehiculo;
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
}
