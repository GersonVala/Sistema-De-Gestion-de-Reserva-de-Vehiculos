package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.TipoVehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.repository.TipoVehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.VehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.TipoVehiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de tipos de vehículos.
 * Maneja la lógica de negocio para la gestión de tipos de vehículos.
 */
@Service
@Transactional
@Slf4j
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final VehiculoRepository vehiculoRepository;

    public TipoVehiculoServiceImpl(TipoVehiculoRepository tipoVehiculoRepository, VehiculoRepository vehiculoRepository) {
        this.tipoVehiculoRepository = tipoVehiculoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public TipoVehiculo crearTipoVehiculo(TipoVehiculo tipoVehiculo) {
        log.info("Creando nuevo tipo de vehículo: {}", tipoVehiculo.getTipo());

        // Validar que el tipo no esté vacío
        if (tipoVehiculo.getTipo() == null || tipoVehiculo.getTipo().trim().isEmpty()) {
            throw new BusinessException("El tipo de vehículo es obligatorio");
        }

        if (tipoVehiculo.getCaracteristicas() == null || tipoVehiculo.getCaracteristicas().trim().isEmpty()) {
            throw new BusinessException("Las características son obligatorias");
        }

        // Verificar si ya existe un tipo con ese nombre
        if (tipoVehiculoRepository.existsByTipo(tipoVehiculo.getTipo())) {
            throw new BusinessException("Ya existe un tipo de vehículo con ese nombre: " + tipoVehiculo.getTipo());
        }

        TipoVehiculo tipoGuardado = tipoVehiculoRepository.save(tipoVehiculo);
        log.info("Tipo de vehículo creado exitosamente con ID: {}", tipoGuardado.getIdTipoVehiculo());

        return tipoGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public TipoVehiculo obtenerPorId(Long id) {
        return tipoVehiculoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TipoVehiculo", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoVehiculo> obtenerTodos() {
        return tipoVehiculoRepository.findAll();
    }

    @Override
    public TipoVehiculo actualizarTipoVehiculo(Long id, TipoVehiculo tipoVehiculoActualizado) {
        TipoVehiculo tipoVehiculo = obtenerPorId(id);

        log.info("Actualizando tipo de vehículo ID: {}", id);

        // Validaciones
        if (tipoVehiculoActualizado.getTipo() == null || tipoVehiculoActualizado.getTipo().trim().isEmpty()) {
            throw new BusinessException("El tipo de vehículo es obligatorio");
        }

        if (tipoVehiculoActualizado.getCaracteristicas() == null || tipoVehiculoActualizado.getCaracteristicas().trim().isEmpty()) {
            throw new BusinessException("Las características son obligatorias");
        }

        // Verificar si cambió el nombre y si el nuevo nombre ya existe
        if (!tipoVehiculo.getTipo().equals(tipoVehiculoActualizado.getTipo())) {
            if (tipoVehiculoRepository.existsByTipo(tipoVehiculoActualizado.getTipo())) {
                throw new BusinessException("Ya existe un tipo de vehículo con ese nombre: " + tipoVehiculoActualizado.getTipo());
            }
        }

        // Actualizar campos
        tipoVehiculo.setTipo(tipoVehiculoActualizado.getTipo());
        tipoVehiculo.setCaracteristicas(tipoVehiculoActualizado.getCaracteristicas());

        TipoVehiculo tipoGuardado = tipoVehiculoRepository.save(tipoVehiculo);
        log.info("Tipo de vehículo {} actualizado exitosamente", id);

        return tipoGuardado;
    }

    @Override
    public void eliminarTipoVehiculo(Long id) {
        TipoVehiculo tipoVehiculo = obtenerPorId(id);

        // Verificar si está en uso
        if (estaEnUso(id)) {
            long cantidadVehiculos = vehiculoRepository.findByTipoVehiculoIdTipoVehiculo(id).size();
            throw new BusinessException(
                "No se puede eliminar el tipo de vehículo porque está siendo usado por " + cantidadVehiculos + " vehículo(s)"
            );
        }

        tipoVehiculoRepository.delete(tipoVehiculo);
        log.info("Tipo de vehículo {} eliminado exitosamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEnUso(Long id) {
        return !vehiculoRepository.findByTipoVehiculoIdTipoVehiculo(id).isEmpty();
    }
}
