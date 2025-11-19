package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;

import java.time.LocalDate;
import java.util.List;

public interface VehiculoService {

    Vehiculo crearVehiculo(Vehiculo vehiculo);

    Vehiculo obtenerPorId(Long id);

    List<Vehiculo> obtenerTodos();

    List<Vehiculo> obtenerPorSucursal(Long idSucursal);

    List<Vehiculo> obtenerDisponiblesPorSucursal(Long idSucursal);

    List<Vehiculo> buscarDisponiblesEnFechas(Long idSucursal, LocalDate fechaInicio, LocalDate fechaFin);

    Vehiculo actualizarVehiculo(Long id, Vehiculo vehiculo);

    void cambiarEstado(Long id, EstadoVehiculo nuevoEstado);

    void cambiarSucursal(Long idVehiculo, Long idNuevaSucursal);

    void eliminarVehiculo(Long id);
}
