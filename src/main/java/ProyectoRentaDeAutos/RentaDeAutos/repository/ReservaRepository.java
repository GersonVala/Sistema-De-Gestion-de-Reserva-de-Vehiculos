package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Reserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar reservas por usuario
    List<Reserva> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar reservas por usuario ordenadas por fecha más reciente
    List<Reserva> findByUsuarioIdUsuarioOrderByFechaInicioDesc(Long idUsuario);

    // Buscar reservas por estado
    List<Reserva> findByEstado(EstadoReserva estado);

    // Buscar reservas por sucursal de retiro (para vendedores)
    List<Reserva> findBySucursalRetiroIdSucursal(Long idSucursal);

    // Buscar reservas pendientes por sucursal (para vendedores)
    @Query("SELECT r FROM Reserva r WHERE r.sucursalRetiro.idSucursal = :idSucursal AND r.estado = 'PENDIENTE' ORDER BY r.fechaInicio ASC")
    List<Reserva> findPendientesBySucursal(@Param("idSucursal") Long idSucursal);

    // Buscar reservas activas (PENDIENTE o ACEPTADA) por sucursal
    @Query("SELECT r FROM Reserva r WHERE r.sucursalRetiro.idSucursal = :idSucursal AND r.estado IN ('PENDIENTE', 'ACEPTADA') ORDER BY r.fechaInicio ASC")
    List<Reserva> findActivasBySucursal(@Param("idSucursal") Long idSucursal);

    // Buscar todas las reservas de una sucursal (cualquier estado)
    @Query("SELECT r FROM Reserva r WHERE r.sucursalRetiro.idSucursal = :idSucursal ORDER BY r.fechaInicio DESC")
    List<Reserva> findAllBySucursal(@Param("idSucursal") Long idSucursal);

    // Buscar reservas por vehículo
    List<Reserva> findByVehiculoIdVehiculo(Long idVehiculo);

    // Verificar si hay reservas activas para un vehículo en un rango de fechas
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.vehiculo.idVehiculo = :idVehiculo " +
           "AND r.estado IN ('PENDIENTE', 'ACEPTADA') " +
           "AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))")
    boolean existeReservaActivaEnFechas(
        @Param("idVehiculo") Long idVehiculo,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    // Buscar reservas activas de un vehículo
    @Query("SELECT r FROM Reserva r WHERE r.vehiculo.idVehiculo = :idVehiculo AND r.estado IN ('PENDIENTE', 'ACEPTADA')")
    List<Reserva> findReservasActivasByVehiculo(@Param("idVehiculo") Long idVehiculo);

    // Buscar reservas por rango de fechas
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<Reserva> findByFechasBetween(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    // Para reportes/estadísticas del admin
    @Query("SELECT r FROM Reserva r WHERE r.estado = :estado ORDER BY r.fechaInicio DESC")
    List<Reserva> findAllByEstadoOrderByFechaInicio(@Param("estado") EstadoReserva estado);

    // Buscar reservas de un vehículo con estados específicos (para detalle de vehículo)
    @Query("SELECT r FROM Reserva r WHERE r.vehiculo.idVehiculo = :idVehiculo AND r.estado IN :estados ORDER BY r.fechaInicio DESC")
    List<Reserva> findByVehiculoAndEstadoIn(
        @Param("idVehiculo") Long idVehiculo,
        @Param("estados") List<EstadoReserva> estados
    );

    // Contar reservas activas (PENDIENTE o ACEPTADA) que involucran una sucursal (retiro o devolución)
    @Query("SELECT COUNT(r) FROM Reserva r WHERE (r.sucursalRetiro.idSucursal = :idSucursal OR r.sucursalDevolucion.idSucursal = :idSucursal) AND r.estado IN ('PENDIENTE', 'ACEPTADA')")
    long countReservasActivasBySucursal(@Param("idSucursal") Long idSucursal);
}
