package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearMotorRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.MotorResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.Motor;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.MotorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MotorService {

    private final MotorRepository motorRepository;

    @Transactional
    public MotorResponse crear(CrearMotorRequest request) {
        Motor motor = new Motor();
        motor.setCilindrada(request.getCilindrada());
        motor.setCaballos_de_fuerza(request.getCaballos_de_fuerza());
        motor.setTipoCombustible(request.getTipoCombustible());
        motor.setTipoMotor(request.getTipoMotor());

        Motor motorGuardado = motorRepository.save(motor);
        return convertirAResponse(motorGuardado);
    }

    public List<MotorResponse> obtenerTodos() {
        return motorRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public MotorResponse obtenerPorId(Integer id) {
        Motor motor = motorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Motor no encontrado con ID: " + id));
        return convertirAResponse(motor);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!motorRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Motor no encontrado con ID: " + id);
        }
        motorRepository.deleteById(id);
    }

    private MotorResponse convertirAResponse(Motor motor) {
        return new MotorResponse(
                motor.getId_motor(),
                motor.getCilindrada(),
                motor.getCaballos_de_fuerza(),
                motor.getTipoCombustible().name(),
                motor.getTipoMotor().name()
        );
    }
}

