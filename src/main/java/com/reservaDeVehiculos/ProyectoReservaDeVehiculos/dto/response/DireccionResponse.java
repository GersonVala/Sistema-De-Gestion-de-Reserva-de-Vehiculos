package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionResponse {

    private Integer id_direccion;
    private String calle;
    private Integer numero_calle;
    private String nombre_ciudad;
    private String estado_ciudad;
}

