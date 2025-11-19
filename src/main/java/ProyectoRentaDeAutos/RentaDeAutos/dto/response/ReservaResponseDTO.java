package ProyectoRentaDeAutos.RentaDeAutos.dto.response;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoReserva;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para reservas.
 * Incluye toda la información necesaria para mostrar al usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long idReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal precio;
    private MetodoPago metodoPago;
    private EstadoReserva estado;

    // Información del usuario
    private String usuarioNombre;
    private String usuarioEmail;

    // Información del vehículo
    private String vehiculoMarca;
    private String vehiculoModelo;
    private String vehiculoPatente;

    // Información de sucursales
    private String sucursalRetiroNombre;
    private String sucursalRetiroDireccion;
    private String sucursalDevolucionNombre;
    private String sucursalDevolucionDireccion;

    /**
     * Calcula la cantidad de días de la reserva.
     */
    public long getDias() {
        if (fechaInicio != null && fechaFin != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        }
        return 0;
    }
}
