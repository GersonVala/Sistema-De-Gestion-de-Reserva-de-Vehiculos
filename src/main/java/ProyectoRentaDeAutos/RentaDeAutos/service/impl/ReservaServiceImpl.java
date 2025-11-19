package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Reserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoReserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.repository.ReservaRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.SucursalRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.UsuarioRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.VehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.ReservaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                             VehiculoRepository vehiculoRepository,
                             UsuarioRepository usuarioRepository,
                             SucursalRepository sucursalRepository) {
        this.reservaRepository = reservaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public Reserva crearReserva(Reserva reserva) {
        log.info("Creando reserva para usuario: {}", reserva.getUsuario().getIdUsuario());

        // Validar fechas
        validarFechas(reserva.getFechaInicio(), reserva.getFechaFin());

        // Validar y obtener entidades relacionadas
        Usuario usuario = usuarioRepository.findById(reserva.getUsuario().getIdUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", reserva.getUsuario().getIdUsuario()));

        Vehiculo vehiculo = vehiculoRepository.findById(reserva.getVehiculo().getIdVehiculo())
            .orElseThrow(() -> new ResourceNotFoundException("Vehiculo", "id", reserva.getVehiculo().getIdVehiculo()));

        Sucursal sucursalRetiro = sucursalRepository.findById(reserva.getSucursalRetiro().getIdSucursal())
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", reserva.getSucursalRetiro().getIdSucursal()));

        Sucursal sucursalDevolucion = sucursalRepository.findById(reserva.getSucursalDevolucion().getIdSucursal())
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", reserva.getSucursalDevolucion().getIdSucursal()));

        // Validar que el vehículo esté disponible
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new BusinessException("El vehículo no está disponible para reserva");
        }

        // Validar que no haya reservas activas que se solapen
        validarDisponibilidad(vehiculo.getIdVehiculo(), reserva.getFechaInicio(), reserva.getFechaFin());

        // Calcular precio total (días * precio diario)
        long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        if (dias <= 0) {
            throw new BusinessException("La reserva debe ser de al menos 1 día");
        }
        reserva.setPrecio(vehiculo.getPrecioDiario().multiply(java.math.BigDecimal.valueOf(dias)));

        // Establecer relaciones
        reserva.setUsuario(usuario);
        reserva.setVehiculo(vehiculo);
        reserva.setSucursalRetiro(sucursalRetiro);
        reserva.setSucursalDevolucion(sucursalDevolucion);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        // Guardar reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // Cambiar estado del vehículo a RESERVADO (los triggers de BD también lo harán)
        vehiculo.setEstado(EstadoVehiculo.RESERVADO);
        vehiculoRepository.save(vehiculo);

        log.info("Reserva creada exitosamente con ID: {}", reservaGuardada.getIdReserva());
        return reservaGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorUsuario(Long idUsuario) {
        return reservaRepository.findByUsuarioIdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorSucursal(Long idSucursal) {
        return reservaRepository.findAllBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPendientesPorSucursal(Long idSucursal) {
        return reservaRepository.findPendientesBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodasPorEstado(EstadoReserva estado) {
        return reservaRepository.findAllByEstadoOrderByFechaInicio(estado);
    }

    @Override
    public Reserva aceptarReserva(Long idReserva) {
        Reserva reserva = obtenerPorId(idReserva);

        // Validar que esté en estado PENDIENTE
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden aceptar reservas en estado PENDIENTE");
        }

        // Cambiar estado de reserva
        reserva.setEstado(EstadoReserva.ACEPTADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        // Cambiar estado del vehículo a ENTREGADO (los triggers de BD también lo harán)
        Vehiculo vehiculo = reserva.getVehiculo();
        vehiculo.setEstado(EstadoVehiculo.ENTREGADO);
        vehiculoRepository.save(vehiculo);

        log.info("Reserva {} aceptada", idReserva);
        return reservaActualizada;
    }

    @Override
    public Reserva cancelarReserva(Long idReserva) {
        Reserva reserva = obtenerPorId(idReserva);

        // No se pueden cancelar reservas ya canceladas
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya está cancelada");
        }

        // Cambiar estado de reserva
        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        // Liberar vehículo (cambiar a DISPONIBLE) - los triggers de BD también lo harán
        Vehiculo vehiculo = reserva.getVehiculo();
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculoRepository.save(vehiculo);

        log.info("Reserva {} cancelada", idReserva);
        return reservaActualizada;
    }

    @Override
    @Transactional(readOnly = true)
    public void validarDisponibilidad(Long idVehiculo, LocalDate fechaInicio, LocalDate fechaFin) {
        boolean tieneReservaActiva = reservaRepository.existeReservaActivaEnFechas(idVehiculo, fechaInicio, fechaFin);

        if (tieneReservaActiva) {
            throw new BusinessException("El vehículo ya tiene una reserva activa en las fechas seleccionadas");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorVehiculo(Long idVehiculo) {
        return reservaRepository.findByVehiculoIdVehiculo(idVehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorVehiculoYEstados(Long idVehiculo, List<EstadoReserva> estados) {
        return reservaRepository.findByVehiculoAndEstadoIn(idVehiculo, estados);
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Validar que fechaFin sea posterior a fechaInicio
        if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Validar que no sean fechas pasadas
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new BusinessException("No se pueden crear reservas con fechas pasadas");
        }
    }
}
