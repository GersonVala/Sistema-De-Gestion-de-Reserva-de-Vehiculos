package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // Buscar empleado por usuario (relación uno a uno implícita)
    Optional<Empleado> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar empleados por sucursal
    List<Empleado> findBySucursalIdSucursal(Long idSucursal);

    // Buscar empleados activos por sucursal
    @Query("SELECT e FROM Empleado e WHERE e.sucursal.idSucursal = :idSucursal AND e.estado = true")
    List<Empleado> findActivosBySucursal(@Param("idSucursal") Long idSucursal);

    // Buscar empleados activos
    List<Empleado> findByEstadoTrue();

    // Verificar si un usuario ya es empleado
    boolean existsByUsuarioIdUsuario(Long idUsuario);

    // Obtener la sucursal de un empleado por email del usuario
    @Query("SELECT e FROM Empleado e WHERE e.usuario.email = :email AND e.estado = true")
    Optional<Empleado> findByUsuarioEmail(@Param("email") String email);

    // Contar empleados en una sucursal
    long countBySucursalIdSucursal(Long idSucursal);
}
