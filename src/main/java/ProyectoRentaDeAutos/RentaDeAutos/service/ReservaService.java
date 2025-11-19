package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Reserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoReserva;

import java.util.List;

public interface ReservaService {

    Reserva crearReserva(Reserva reserva);

    Reserva obtenerPorId(Long id);

    List<Reserva> obtenerPorUsuario(Long idUsuario);

    List<Reserva> obtenerPorSucursal(Long idSucursal);

    List<Reserva> obtenerPendientesPorSucursal(Long idSucursal);

    List<Reserva> obtenerTodasPorEstado(EstadoReserva estado);

    Reserva aceptarReserva(Long idReserva);

    Reserva cancelarReserva(Long idReserva);

    void validarDisponibilidad(Long idVehiculo, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);

    List<Reserva> obtenerPorVehiculo(Long idVehiculo);

    List<Reserva> obtenerPorVehiculoYEstados(Long idVehiculo, List<EstadoReserva> estados);
}
