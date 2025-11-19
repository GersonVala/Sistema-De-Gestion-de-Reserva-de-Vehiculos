package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Empleado;
import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.repository.EmpleadoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.SucursalRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.UsuarioRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.EmpleadoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository,
                              UsuarioRepository usuarioRepository,
                              SucursalRepository sucursalRepository) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public Empleado crearEmpleado(Long idUsuario, Long idSucursal) {
        log.info("Creando empleado para usuario: {}", idUsuario);

        // Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", idUsuario));

        // Validar que el usuario sea VENDEDOR
        if (!"VENDEDOR".equals(usuario.getRol().getNombre())) {
            throw new BusinessException("El usuario debe tener rol VENDEDOR para ser asignado como empleado");
        }

        // Validar que el usuario no sea ya un empleado
        if (empleadoRepository.existsByUsuarioIdUsuario(idUsuario)) {
            throw new BusinessException("El usuario ya está asignado como empleado");
        }

        // Validar que la sucursal exista
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", idSucursal));

        // Crear empleado
        Empleado empleado = new Empleado();
        empleado.setUsuario(usuario);
        empleado.setSucursal(sucursal);
        empleado.setEstado(true);

        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        log.info("Empleado creado exitosamente con ID: {}", empleadoGuardado.getIdEmpleado());

        return empleadoGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado obtenerPorId(Long id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado obtenerPorUsuario(Long idUsuario) {
        return empleadoRepository.findByUsuarioIdUsuario(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado", "idUsuario", idUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado obtenerPorEmail(String email) {
        return empleadoRepository.findByUsuarioEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Empleado", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerPorSucursal(Long idSucursal) {
        return empleadoRepository.findActivosBySucursal(idSucursal);
    }

    @Override
    public Empleado cambiarSucursal(Long idEmpleado, Long idNuevaSucursal) {
        Empleado empleado = obtenerPorId(idEmpleado);

        Sucursal nuevaSucursal = sucursalRepository.findById(idNuevaSucursal)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", idNuevaSucursal));

        empleado.setSucursal(nuevaSucursal);
        Empleado empleadoActualizado = empleadoRepository.save(empleado);

        log.info("Empleado {} reasignado a sucursal: {}", idEmpleado, nuevaSucursal.getNombre());
        return empleadoActualizado;
    }

    @Override
    public void desactivarEmpleado(Long id) {
        Empleado empleado = obtenerPorId(id);
        empleado.setEstado(false);
        empleadoRepository.save(empleado);
        log.info("Empleado desactivado: {}", id);
    }

    @Override
    public void activarEmpleado(Long id) {
        Empleado empleado = obtenerPorId(id);
        empleado.setEstado(true);
        empleadoRepository.save(empleado);
        log.info("Empleado activado: {}", id);
    }

    @Override
    public void eliminarEmpleado(Long id) {
        Empleado empleado = obtenerPorId(id);
        empleadoRepository.delete(empleado);
        log.info("Empleado eliminado: {}", id);
    }
}
