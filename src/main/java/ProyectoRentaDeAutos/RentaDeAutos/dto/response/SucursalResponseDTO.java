package ProyectoRentaDeAutos.RentaDeAutos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para sucursales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponseDTO {

    private Long idSucursal;
    private String nombre;
    private String direccion;
    private String imagenUrl;
    private Boolean estado;
    private Integer cantidadVehiculos;
    private Integer cantidadEmpleados;
}
