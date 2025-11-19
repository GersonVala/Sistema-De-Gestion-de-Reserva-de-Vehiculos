package ProyectoRentaDeAutos.RentaDeAutos.models;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.EstadoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @NotBlank(message = "La patente es obligatoria")
    @Column(name = "patente", nullable = false, unique = true, length = 50)
    private String patente;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @NotBlank(message = "La marca es obligatoria")
    @Column(name = "marca", nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    @NotNull(message = "La cantidad de puertas es obligatoria")
    @Min(value = 2, message = "La cantidad de puertas debe ser al menos 2")
    @Column(name = "cant_puertas", nullable = false)
    private Integer cantPuertas;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion", nullable = false, length = 150)
    private String descripcion;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @NotNull(message = "El precio diario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio diario debe ser mayor a 0")
    @Column(name = "precio_diario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioDiario;

    @NotNull(message = "El motor es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_motor", nullable = false)
    private Motor motor;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "La sucursal es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;
}
