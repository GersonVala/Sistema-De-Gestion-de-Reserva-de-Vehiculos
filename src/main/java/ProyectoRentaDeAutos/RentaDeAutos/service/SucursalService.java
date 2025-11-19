package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;

import java.util.List;

public interface SucursalService {

    Sucursal crearSucursal(Sucursal sucursal);

    Sucursal obtenerPorId(Long id);

    List<Sucursal> obtenerTodas();

    List<Sucursal> obtenerActivas();

    Sucursal actualizarSucursal(Long id, Sucursal sucursal);

    void desactivarSucursal(Long id);

    void activarSucursal(Long id);

    void eliminarSucursal(Long id);
}
