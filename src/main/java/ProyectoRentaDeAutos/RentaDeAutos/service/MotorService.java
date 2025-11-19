package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Motor;

import java.util.List;

/**
 * Servicio para la gestión de motores.
 * Define operaciones CRUD y validaciones de negocio.
 */
public interface MotorService {

    /**
     * Crear un nuevo motor.
     * @param motor Motor a crear
     * @return Motor creado
     */
    Motor crearMotor(Motor motor);

    /**
     * Obtener un motor por su ID.
     * @param id ID del motor
     * @return Motor encontrado
     */
    Motor obtenerPorId(Long id);

    /**
     * Obtener todos los motores.
     * @return Lista de motores
     */
    List<Motor> obtenerTodos();

    /**
     * Actualizar un motor existente.
     * @param id ID del motor a actualizar
     * @param motor Datos actualizados del motor
     * @return Motor actualizado
     */
    Motor actualizarMotor(Long id, Motor motor);

    /**
     * Eliminar un motor.
     * Valida que no esté siendo usado por vehículos.
     * @param id ID del motor a eliminar
     */
    void eliminarMotor(Long id);

    /**
     * Verificar si un motor está siendo usado por vehículos.
     * @param id ID del motor
     * @return true si está en uso, false en caso contrario
     */
    boolean estaEnUso(Long id);
}
