package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.repository.EmpleadoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.ReservaRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.SucursalRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.VehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.SucursalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final ReservaRepository reservaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpleadoRepository empleadoRepository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository,
                              ReservaRepository reservaRepository,
                              VehiculoRepository vehiculoRepository,
                              EmpleadoRepository empleadoRepository) {
        this.sucursalRepository = sucursalRepository;
        this.reservaRepository = reservaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public Sucursal crearSucursal(Sucursal sucursal) {
        log.info("Creando sucursal: {}", sucursal.getNombre());

        // Validar nombre único
        if (sucursalRepository.existsByNombre(sucursal.getNombre())) {
            throw new BusinessException("Ya existe una sucursal con el nombre: " + sucursal.getNombre());
        }

        sucursal.setEstado(true);
        Sucursal sucursalGuardada = sucursalRepository.save(sucursal);
        log.info("Sucursal creada exitosamente con ID: {}", sucursalGuardada.getIdSucursal());

        return sucursalGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Sucursal obtenerPorId(Long id) {
        return sucursalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> obtenerTodas() {
        return sucursalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> obtenerActivas() {
        return sucursalRepository.findByEstadoTrue();
    }

    @Override
    public Sucursal actualizarSucursal(Long id, Sucursal sucursalActualizada) {
        Sucursal sucursal = obtenerPorId(id);

        // Validar nombre único si cambió
        if (!sucursal.getNombre().equals(sucursalActualizada.getNombre())) {
            if (sucursalRepository.existsByNombre(sucursalActualizada.getNombre())) {
                throw new BusinessException("Ya existe una sucursal con ese nombre");
            }
        }

        sucursal.setNombre(sucursalActualizada.getNombre());
        sucursal.setDireccion(sucursalActualizada.getDireccion());
        sucursal.setImagenUrl(sucursalActualizada.getImagenUrl());

        return sucursalRepository.save(sucursal);
    }

    @Override
    public void desactivarSucursal(Long id) {
        Sucursal sucursal = obtenerPorId(id);

        // Validar que no tenga reservas activas
        long reservasActivas = reservaRepository.countReservasActivasBySucursal(id);
        if (reservasActivas > 0) {
            throw new BusinessException("No se puede desactivar la sucursal '" + sucursal.getNombre() +
                "' porque tiene " + reservasActivas + " reserva(s) activa(s). " +
                "Debe gestionar (aceptar, rechazar o cancelar) todas las reservas antes de desactivar la sucursal.");
        }

        sucursal.setEstado(false);
        sucursalRepository.save(sucursal);
        log.info("Sucursal desactivada: {}", id);
    }

    @Override
    public void activarSucursal(Long id) {
        Sucursal sucursal = obtenerPorId(id);
        sucursal.setEstado(true);
        sucursalRepository.save(sucursal);
        log.info("Sucursal activada: {}", id);
    }

    @Override
    public void eliminarSucursal(Long id) {
        Sucursal sucursal = obtenerPorId(id);

        // Validar todas las dependencias antes de eliminar
        List<String> problemas = new ArrayList<>();

        // Verificar reservas activas
        long reservasActivas = reservaRepository.countReservasActivasBySucursal(id);
        if (reservasActivas > 0) {
            problemas.add(reservasActivas + " reserva(s) activa(s)");
        }

        // Verificar empleados asignados
        long empleados = empleadoRepository.countBySucursalIdSucursal(id);
        if (empleados > 0) {
            problemas.add(empleados + " empleado(s) asignado(s)");
        }

        // Verificar vehículos asignados
        long vehiculos = vehiculoRepository.countBySucursalIdSucursal(id);
        if (vehiculos > 0) {
            problemas.add(vehiculos + " vehículo(s) asignado(s)");
        }

        // Si hay problemas, lanzar excepción con mensaje descriptivo
        if (!problemas.isEmpty()) {
            String mensaje = "No se puede eliminar la sucursal '" + sucursal.getNombre() + "' porque tiene: " +
                String.join(", ", problemas) + ". " +
                "Debe reasignar o eliminar estos elementos antes de eliminar la sucursal.";
            throw new BusinessException(mensaje);
        }

        sucursalRepository.delete(sucursal);
        log.info("Sucursal eliminada: {}", id);
    }
}
