package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Motor;
import ProyectoRentaDeAutos.RentaDeAutos.repository.MotorRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.VehiculoRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.MotorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de motores.
 * Maneja la lógica de negocio para la gestión de motores.
 */
@Service
@Transactional
@Slf4j
public class MotorServiceImpl implements MotorService {

    private final MotorRepository motorRepository;
    private final VehiculoRepository vehiculoRepository;

    public MotorServiceImpl(MotorRepository motorRepository, VehiculoRepository vehiculoRepository) {
        this.motorRepository = motorRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public Motor crearMotor(Motor motor) {
        log.info("Creando nuevo motor: {} - {}", motor.getTipoMotor(), motor.getTipoCombustible());

        // Validaciones básicas
        if (motor.getCilindrada() == null || motor.getCilindrada().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La cilindrada debe ser mayor a 0");
        }

        if (motor.getCaballosDeFuerza() == null || motor.getCaballosDeFuerza() < 1) {
            throw new BusinessException("Los caballos de fuerza deben ser al menos 1");
        }

        if (motor.getTipoCombustible() == null) {
            throw new BusinessException("El tipo de combustible es obligatorio");
        }

        if (motor.getTipoMotor() == null) {
            throw new BusinessException("El tipo de motor es obligatorio");
        }

        Motor motorGuardado = motorRepository.save(motor);
        log.info("Motor creado exitosamente con ID: {}", motorGuardado.getIdMotor());

        return motorGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Motor obtenerPorId(Long id) {
        return motorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Motor", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Motor> obtenerTodos() {
        return motorRepository.findAll();
    }

    @Override
    public Motor actualizarMotor(Long id, Motor motorActualizado) {
        Motor motor = obtenerPorId(id);

        log.info("Actualizando motor ID: {}", id);

        // Validaciones
        if (motorActualizado.getCilindrada() == null || motorActualizado.getCilindrada().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La cilindrada debe ser mayor a 0");
        }

        if (motorActualizado.getCaballosDeFuerza() == null || motorActualizado.getCaballosDeFuerza() < 1) {
            throw new BusinessException("Los caballos de fuerza deben ser al menos 1");
        }

        if (motorActualizado.getTipoCombustible() == null) {
            throw new BusinessException("El tipo de combustible es obligatorio");
        }

        if (motorActualizado.getTipoMotor() == null) {
            throw new BusinessException("El tipo de motor es obligatorio");
        }

        // Actualizar campos
        motor.setCilindrada(motorActualizado.getCilindrada());
        motor.setCaballosDeFuerza(motorActualizado.getCaballosDeFuerza());
        motor.setTipoCombustible(motorActualizado.getTipoCombustible());
        motor.setTipoMotor(motorActualizado.getTipoMotor());

        Motor motorGuardado = motorRepository.save(motor);
        log.info("Motor {} actualizado exitosamente", id);

        return motorGuardado;
    }

    @Override
    public void eliminarMotor(Long id) {
        Motor motor = obtenerPorId(id);

        // Verificar si está en uso
        if (estaEnUso(id)) {
            long cantidadVehiculos = vehiculoRepository.findAll().stream()
                .filter(v -> v.getMotor().getIdMotor().equals(id))
                .count();
            throw new BusinessException(
                "No se puede eliminar el motor porque está siendo usado por " + cantidadVehiculos + " vehículo(s)"
            );
        }

        motorRepository.delete(motor);
        log.info("Motor {} eliminado exitosamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEnUso(Long id) {
        return vehiculoRepository.findAll().stream()
            .anyMatch(v -> v.getMotor() != null && v.getMotor().getIdMotor().equals(id));
    }
}
