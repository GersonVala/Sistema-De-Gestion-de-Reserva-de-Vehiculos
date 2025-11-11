package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoCombustible;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoMotor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrearMotorRequest {

    @NotNull(message = "La cilindrada es obligatoria")
    @Positive(message = "La cilindrada debe ser positiva")
    private Double cilindrada;

    @NotNull(message = "Los caballos de fuerza son obligatorios")
    @Positive(message = "Los caballos de fuerza deben ser positivos")
    private Integer caballos_de_fuerza;

    @NotNull(message = "El tipo de combustible es obligatorio")
    private TipoCombustible tipoCombustible;

    @NotNull(message = "El tipo de motor es obligatorio")
    private TipoMotor tipoMotor;
}

