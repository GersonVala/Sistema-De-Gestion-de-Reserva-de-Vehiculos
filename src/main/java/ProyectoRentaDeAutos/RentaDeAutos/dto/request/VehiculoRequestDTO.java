package ProyectoRentaDeAutos.RentaDeAutos.dto.request;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación y actualización de vehículos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoRequestDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 50, message = "La patente no puede tener más de 50 caracteres")
    private String patente;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede tener más de 50 caracteres")
    private String modelo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede tener más de 50 caracteres")
    private String color;

    @NotNull(message = "La cantidad de puertas es obligatoria")
    @Min(value = 2, message = "Debe tener al menos 2 puertas")
    @Max(value = 6, message = "No puede tener más de 6 puertas")
    private Integer cantPuertas;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @Size(max = 255, message = "La URL de imagen no puede tener más de 255 caracteres")
    private String imagenUrl;

    @NotNull(message = "El precio diario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precioDiario;

    @NotNull(message = "El motor es obligatorio")
    private Long motorId;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private Long tipoVehiculoId;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    // Campo opcional para actualización - permite al admin cambiar el estado manualmente
    private EstadoVehiculo estado;
}
