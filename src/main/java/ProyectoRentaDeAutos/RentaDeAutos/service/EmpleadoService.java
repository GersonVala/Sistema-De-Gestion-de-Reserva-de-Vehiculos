package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Empleado;

import java.util.List;

public interface EmpleadoService {

    Empleado crearEmpleado(Long idUsuario, Long idSucursal);

    Empleado obtenerPorId(Long id);

    Empleado obtenerPorUsuario(Long idUsuario);

    Empleado obtenerPorEmail(String email);

    List<Empleado> obtenerTodos();

    List<Empleado> obtenerPorSucursal(Long idSucursal);

    Empleado cambiarSucursal(Long idEmpleado, Long idNuevaSucursal);

    void desactivarEmpleado(Long id);

    void activarEmpleado(Long id);

    void eliminarEmpleado(Long id);
}
