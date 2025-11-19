package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Motor;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.TipoCombustible;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.TipoMotor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotorRepository extends JpaRepository<Motor, Long> {

    // Buscar motores por tipo de combustible
    List<Motor> findByTipoCombustible(TipoCombustible tipoCombustible);

    // Buscar motores por tipo de motor
    List<Motor> findByTipoMotor(TipoMotor tipoMotor);
}
