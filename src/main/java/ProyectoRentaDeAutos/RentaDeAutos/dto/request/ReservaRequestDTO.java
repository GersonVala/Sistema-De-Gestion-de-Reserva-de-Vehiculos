package ProyectoRentaDeAutos.RentaDeAutos.dto.request;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.MetodoPago;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para la creación de reservas.
 * Valida que las fechas y relaciones sean correctas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "La sucursal de retiro es obligatoria")
    private Long sucursalRetiroId;

    @NotNull(message = "La sucursal de devolución es obligatoria")
    private Long sucursalDevolucionId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    /**
     * Valida que la fecha de fin sea posterior a la de inicio.
     */
    public boolean fechasValidas() {
        return fechaInicio != null && fechaFin != null && fechaFin.isAfter(fechaInicio);
    }
}
