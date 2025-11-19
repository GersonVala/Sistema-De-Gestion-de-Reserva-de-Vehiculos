package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    // Buscar sucursales activas
    List<Sucursal> findByEstadoTrue();

    // Buscar por nombre
    Optional<Sucursal> findByNombre(String nombre);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}
