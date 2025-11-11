package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearDireccionRequest {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 40, message = "La calle no puede exceder 40 caracteres")
    private String calle;

    @NotNull(message = "El número de calle es obligatorio")
    @Positive(message = "El número de calle debe ser positivo")
    private Integer numero_calle;

    @NotNull(message = "El ID de la ciudad es obligatorio")
    private Integer id_ciudad;
}

