package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Motor;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.models.TipoVehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.repository.MotorRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.SucursalRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.TipoVehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.VehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.VehiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final SucursalRepository sucursalRepository;
    private final MotorRepository motorRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
                              SucursalRepository sucursalRepository,
                              MotorRepository motorRepository,
                              TipoVehiculoRepository tipoVehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.sucursalRepository = sucursalRepository;
        this.motorRepository = motorRepository;
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    @Override
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        log.info("Creando vehículo con patente: {}", vehiculo.getPatente());

        // Validar patente única
        if (vehiculoRepository.existsByPatente(vehiculo.getPatente())) {
            throw new BusinessException("Ya existe un vehículo con la patente: " + vehiculo.getPatente());
        }

        // Validar precio positivo
        if (vehiculo.getPrecioDiario().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio diario debe ser mayor a 0");
        }

        // Validar que existan las relaciones
        if (vehiculo.getMotor() != null && vehiculo.getMotor().getIdMotor() != null) {
            Motor motor = motorRepository.findById(vehiculo.getMotor().getIdMotor())
                .orElseThrow(() -> new ResourceNotFoundException("Motor", "id", vehiculo.getMotor().getIdMotor()));
            vehiculo.setMotor(motor);
        }

        if (vehiculo.getTipoVehiculo() != null && vehiculo.getTipoVehiculo().getIdTipoVehiculo() != null) {
            TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(vehiculo.getTipoVehiculo().getIdTipoVehiculo())
                .orElseThrow(() -> new ResourceNotFoundException("TipoVehiculo", "id", vehiculo.getTipoVehiculo().getIdTipoVehiculo()));
            vehiculo.setTipoVehiculo(tipoVehiculo);
        }

        if (vehiculo.getSucursal() != null && vehiculo.getSucursal().getIdSucursal() != null) {
            Sucursal sucursal = sucursalRepository.findById(vehiculo.getSucursal().getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", vehiculo.getSucursal().getIdSucursal()));
            vehiculo.setSucursal(sucursal);
        }

        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);
        log.info("Vehículo creado exitosamente con ID: {}", vehiculoGuardado.getIdVehiculo());

        return vehiculoGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Vehiculo obtenerPorId(Long id) {
        return vehiculoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehiculo", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerPorSucursal(Long idSucursal) {
        return vehiculoRepository.findBySucursalIdSucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerDisponiblesPorSucursal(Long idSucursal) {
        return vehiculoRepository.findDisponiblesBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> buscarDisponiblesEnFechas(Long idSucursal, LocalDate fechaInicio, LocalDate fechaFin) {
        // Validar fechas
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new BusinessException("No se pueden buscar vehículos para fechas pasadas");
        }

        return vehiculoRepository.findDisponiblesEnFechas(idSucursal, fechaInicio, fechaFin);
    }

    @Override
    public Vehiculo actualizarVehiculo(Long id, Vehiculo vehiculoActualizado) {
        Vehiculo vehiculo = obtenerPorId(id);

        // Validar que el vehículo no esté RESERVADO o ENTREGADO (excepto si solo se cambia el estado)
        if ((vehiculo.getEstado() == EstadoVehiculo.RESERVADO || vehiculo.getEstado() == EstadoVehiculo.ENTREGADO)) {
            // Permitir cambiar SOLO el estado si viene en el DTO
            if (vehiculoActualizado.getEstado() != null &&
                !vehiculo.getEstado().equals(vehiculoActualizado.getEstado())) {
                // Solo permitir cambio de estado
                vehiculo.setEstado(vehiculoActualizado.getEstado());
                log.info("Estado del vehículo {} actualizado a: {}", id, vehiculoActualizado.getEstado());
                return vehiculoRepository.save(vehiculo);
            } else {
                // No permitir edición de otros campos
                String estadoActual = vehiculo.getEstado() == EstadoVehiculo.RESERVADO ? "reservado" : "entregado";
                throw new BusinessException("No se puede editar un vehículo que está " + estadoActual + ". Debe cancelar la reserva primero o cambiar su estado.");
            }
        }

        // Validar patente si cambió
        if (!vehiculo.getPatente().equals(vehiculoActualizado.getPatente())) {
            if (vehiculoRepository.existsByPatente(vehiculoActualizado.getPatente())) {
                throw new BusinessException("Ya existe un vehículo con esa patente");
            }
            vehiculo.setPatente(vehiculoActualizado.getPatente());
        }

        // Validar precio
        if (vehiculoActualizado.getPrecioDiario().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio diario debe ser mayor a 0");
        }

        // Actualizar campos básicos
        vehiculo.setModelo(vehiculoActualizado.getModelo());
        vehiculo.setMarca(vehiculoActualizado.getMarca());
        vehiculo.setColor(vehiculoActualizado.getColor());
        vehiculo.setCantPuertas(vehiculoActualizado.getCantPuertas());
        vehiculo.setDescripcion(vehiculoActualizado.getDescripcion());
        vehiculo.setImagenUrl(vehiculoActualizado.getImagenUrl());
        vehiculo.setPrecioDiario(vehiculoActualizado.getPrecioDiario());

        // Actualizar estado si se proporcionó (solo admin puede cambiar estado manualmente)
        // Solo se permiten los estados DISPONIBLE y DESCOMPUESTO manualmente
        // Los estados RESERVADO y ENTREGADO son controlados por el sistema de reservas
        if (vehiculoActualizado.getEstado() != null) {
            EstadoVehiculo nuevoEstado = vehiculoActualizado.getEstado();

            // Validar que solo se establezcan estados permitidos manualmente
            if (nuevoEstado == EstadoVehiculo.RESERVADO || nuevoEstado == EstadoVehiculo.ENTREGADO) {
                throw new BusinessException("No se puede cambiar manualmente el estado a " + nuevoEstado.name() +
                    ". Los estados RESERVADO y ENTREGADO son controlados automáticamente por el sistema de reservas.");
            }

            vehiculo.setEstado(nuevoEstado);
            log.info("Estado del vehículo {} actualizado a: {}", id, nuevoEstado);
        }

        // Actualizar relaciones si cambiaron
        if (vehiculoActualizado.getMotor() != null && vehiculoActualizado.getMotor().getIdMotor() != null) {
            Motor motor = motorRepository.findById(vehiculoActualizado.getMotor().getIdMotor())
                .orElseThrow(() -> new ResourceNotFoundException("Motor", "id", vehiculoActualizado.getMotor().getIdMotor()));
            vehiculo.setMotor(motor);
        }

        if (vehiculoActualizado.getTipoVehiculo() != null && vehiculoActualizado.getTipoVehiculo().getIdTipoVehiculo() != null) {
            TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(vehiculoActualizado.getTipoVehiculo().getIdTipoVehiculo())
                .orElseThrow(() -> new ResourceNotFoundException("TipoVehiculo", "id", vehiculoActualizado.getTipoVehiculo().getIdTipoVehiculo()));
            vehiculo.setTipoVehiculo(tipoVehiculo);
        }

        if (vehiculoActualizado.getSucursal() != null && vehiculoActualizado.getSucursal().getIdSucursal() != null) {
            Sucursal sucursal = sucursalRepository.findById(vehiculoActualizado.getSucursal().getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", vehiculoActualizado.getSucursal().getIdSucursal()));
            vehiculo.setSucursal(sucursal);
        }

        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);
        log.info("Vehículo {} actualizado exitosamente", id);

        return vehiculoGuardado;
    }

    @Override
    public void cambiarEstado(Long id, EstadoVehiculo nuevoEstado) {
        Vehiculo vehiculo = obtenerPorId(id);
        vehiculo.setEstado(nuevoEstado);
        vehiculoRepository.save(vehiculo);
        log.info("Estado del vehículo {} cambiado a: {}", id, nuevoEstado);
    }

    @Override
    public void cambiarSucursal(Long idVehiculo, Long idNuevaSucursal) {
        Vehiculo vehiculo = obtenerPorId(idVehiculo);
        Sucursal nuevaSucursal = sucursalRepository.findById(idNuevaSucursal)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", idNuevaSucursal));

        vehiculo.setSucursal(nuevaSucursal);
        vehiculoRepository.save(vehiculo);
        log.info("Vehículo {} movido a sucursal: {}", idVehiculo, nuevaSucursal.getNombre());
    }

    @Override
    public void eliminarVehiculo(Long id) {
        Vehiculo vehiculo = obtenerPorId(id);

        // Validar que el vehículo no esté RESERVADO o ENTREGADO
        if (vehiculo.getEstado() == EstadoVehiculo.RESERVADO || vehiculo.getEstado() == EstadoVehiculo.ENTREGADO) {
            String estadoActual = vehiculo.getEstado() == EstadoVehiculo.RESERVADO ? "reservado" : "entregado";
            throw new BusinessException("No se puede eliminar un vehículo que está " + estadoActual + ". Debe cancelar la reserva primero.");
        }

        vehiculoRepository.delete(vehiculo);
        log.info("Vehículo eliminado: {}", id);
    }
}
