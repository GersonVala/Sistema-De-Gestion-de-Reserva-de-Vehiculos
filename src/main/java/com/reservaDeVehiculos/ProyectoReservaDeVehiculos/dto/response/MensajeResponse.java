package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {

    private String mensaje;
    private boolean exito;
}

