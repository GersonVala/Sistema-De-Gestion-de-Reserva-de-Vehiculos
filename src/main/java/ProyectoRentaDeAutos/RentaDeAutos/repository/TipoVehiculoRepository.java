package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    // Buscar por tipo (Sedan, SUV, Van, etc.)
    Optional<TipoVehiculo> findByTipo(String tipo);

    // Verificar si existe por tipo
    boolean existsByTipo(String tipo);
}
