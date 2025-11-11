package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.EstadoReservaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {

    private Integer id_reserva;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private EstadoReservaEnum estado;
    private BigDecimal precio_reserva;
    private String nombreCompletoUsuario;
    private String emailUsuario;
    private String telefonoSucursal;
}

