package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Vehiculo;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // Buscar por patente (única)
    Optional<Vehiculo> findByPatente(String patente);

    // Verificar si existe por patente
    boolean existsByPatente(String patente);

    // Buscar vehículos por sucursal
    List<Vehiculo> findBySucursalIdSucursal(Long idSucursal);

    // Buscar vehículos por estado
    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    // Buscar vehículos disponibles en una sucursal
    @Query("SELECT v FROM Vehiculo v WHERE v.sucursal.idSucursal = :idSucursal AND v.estado = 'DISPONIBLE'")
    List<Vehiculo> findDisponiblesBySucursal(@Param("idSucursal") Long idSucursal);

    /**
     * Buscar vehículos disponibles en una sucursal y en un rango de fechas.
     * Un vehículo está disponible si:
     * 1. Su estado es DISPONIBLE
     * 2. Está en la sucursal especificada
     * 3. NO tiene reservas activas (PENDIENTE o ACEPTADA) que se solapen con las fechas
     */
    @Query("SELECT v FROM Vehiculo v WHERE v.sucursal.idSucursal = :idSucursal " +
           "AND v.estado = 'DISPONIBLE' " +
           "AND v.idVehiculo NOT IN (" +
           "  SELECT r.vehiculo.idVehiculo FROM Reserva r " +
           "  WHERE r.estado IN ('PENDIENTE', 'ACEPTADA') " +
           "  AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))" +
           ")")
    List<Vehiculo> findDisponiblesEnFechas(
        @Param("idSucursal") Long idSucursal,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    // Buscar por tipo de vehículo
    List<Vehiculo> findByTipoVehiculoIdTipoVehiculo(Long idTipoVehiculo);

    // Buscar por marca
    List<Vehiculo> findByMarca(String marca);

    // Buscar por modelo
    List<Vehiculo> findByModelo(String modelo);

    // Contar vehículos en una sucursal
    long countBySucursalIdSucursal(Long idSucursal);
}
