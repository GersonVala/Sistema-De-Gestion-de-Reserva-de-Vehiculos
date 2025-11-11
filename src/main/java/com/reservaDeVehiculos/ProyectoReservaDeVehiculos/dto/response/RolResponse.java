package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {

    private Integer id_rol;
    private String estado;
    private String descripcion_rol;
}

