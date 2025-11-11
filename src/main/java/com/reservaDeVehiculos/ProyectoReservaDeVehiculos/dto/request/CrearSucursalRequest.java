package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearSucursalRequest {

    @NotBlank(message = "El teléfono de la sucursal es obligatorio")
    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    private String telefono_sucursal;

    @NotNull(message = "El ID de la dirección es obligatorio")
    private Integer id_direccion;

    @NotNull(message = "El ID del vendedor es obligatorio")
    private Integer id_vendedor;
}
