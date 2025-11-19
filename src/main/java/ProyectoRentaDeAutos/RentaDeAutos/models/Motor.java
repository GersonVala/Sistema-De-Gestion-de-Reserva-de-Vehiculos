package ProyectoRentaDeAutos.RentaDeAutos.models;

import ProyectoRentaDeAutos.RentaDeAutos.models.enums.TipoCombustible;
import ProyectoRentaDeAutos.RentaDeAutos.models.enums.TipoMotor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "motores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Motor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motor")
    private Long idMotor;

    @NotNull(message = "La cilindrada es obligatoria")
    @Column(name = "cilindrada", nullable = false, precision = 3, scale = 2)
    private BigDecimal cilindrada;

    @NotNull(message = "Los caballos de fuerza son obligatorios")
    @Min(value = 1, message = "Los caballos de fuerza deben ser al menos 1")
    @Column(name = "caballos_de_fuerza", nullable = false)
    private Integer caballosDeFuerza;

    @NotNull(message = "El tipo de combustible es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", nullable = false)
    private TipoCombustible tipoCombustible;

    @NotNull(message = "El tipo de motor es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_motor", nullable = false)
    private TipoMotor tipoMotor;
}
