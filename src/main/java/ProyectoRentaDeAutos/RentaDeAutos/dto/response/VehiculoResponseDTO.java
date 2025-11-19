package ProyectoRentaDeAutos.RentaDeAutos.dto.response;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para vehículos.
 * Incluye información completa para mostrar al usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponseDTO {

    private Long idVehiculo;
    private String patente;
    private String modelo;
    private String marca;
    private String color;
    private EstadoVehiculo estado;
    private Integer cantPuertas;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precioDiario;

    // Información del motor
    private String tipoMotor;
    private String tipoCombustible;
    private BigDecimal cilindrada;
    private Integer caballosDeFuerza;

    // Información del tipo de vehículo
    private String tipoVehiculo;
    private String caracteristicasTipo;

    // Información de la sucursal
    private String sucursalNombre;
    private String sucursalDireccion;
}
