package ProyectoRentaDeAutos.RentaDeAutos.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_vehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_vehiculo")
    private Long idTipoVehiculo;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @NotBlank(message = "Las características son obligatorias")
    @Column(name = "caracteristicas", nullable = false, length = 150)
    private String caracteristicas;
}
