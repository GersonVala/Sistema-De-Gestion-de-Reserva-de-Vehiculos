package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorResponse {

    private Integer id_motor;
    private Double cilindrada;
    private Integer caballos_de_fuerza;
    private String tipoCombustible;
    private String tipoMotor;
}

