package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.TipoVehiculo;

import java.util.List;

/**
 * Servicio para la gestión de tipos de vehículos.
 * Define operaciones CRUD y validaciones de negocio.
 */
public interface TipoVehiculoService {

    /**
     * Crear un nuevo tipo de vehículo.
     * @param tipoVehiculo Tipo de vehículo a crear
     * @return Tipo de vehículo creado
     */
    TipoVehiculo crearTipoVehiculo(TipoVehiculo tipoVehiculo);

    /**
     * Obtener un tipo de vehículo por su ID.
     * @param id ID del tipo de vehículo
     * @return Tipo de vehículo encontrado
     */
    TipoVehiculo obtenerPorId(Long id);

    /**
     * Obtener todos los tipos de vehículos.
     * @return Lista de tipos de vehículos
     */
    List<TipoVehiculo> obtenerTodos();

    /**
     * Actualizar un tipo de vehículo existente.
     * @param id ID del tipo de vehículo a actualizar
     * @param tipoVehiculo Datos actualizados del tipo de vehículo
     * @return Tipo de vehículo actualizado
     */
    TipoVehiculo actualizarTipoVehiculo(Long id, TipoVehiculo tipoVehiculo);

    /**
     * Eliminar un tipo de vehículo.
     * Valida que no esté siendo usado por vehículos.
     * @param id ID del tipo de vehículo a eliminar
     */
    void eliminarTipoVehiculo(Long id);

    /**
     * Verificar si un tipo de vehículo está siendo usado por vehículos.
     * @param id ID del tipo de vehículo
     * @return true si está en uso, false en caso contrario
     */
    boolean estaEnUso(Long id);
}
