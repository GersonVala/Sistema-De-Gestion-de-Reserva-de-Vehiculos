package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearTipoVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.TipoVehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.TipoDeVehiculo;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoDuplicadoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.TipoVehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoVehiculoService {

    private final TipoVehiculoRepository tipoVehiculoRepository;

    @Transactional
    public TipoVehiculoResponse crear(CrearTipoVehiculoRequest request) {
        if (tipoVehiculoRepository.existsByNombre(request.getNombre_vehiculo())) {
            throw new RecursoDuplicadoException("Ya existe un tipo de vehículo con el nombre: " + request.getNombre_vehiculo());
        }

        TipoDeVehiculo tipoVehiculo = new TipoDeVehiculo();
        tipoVehiculo.setNombre_vehiculo(request.getNombre_vehiculo());
        tipoVehiculo.setDescripcion_vehiculo(request.getDescripcion_vehiculo());

        TipoDeVehiculo tipoGuardado = tipoVehiculoRepository.save(tipoVehiculo);
        return convertirAResponse(tipoGuardado);
    }

    public List<TipoVehiculoResponse> obtenerTodos() {
        return tipoVehiculoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public TipoVehiculoResponse obtenerPorId(Integer id) {
        TipoDeVehiculo tipoVehiculo = tipoVehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de vehículo no encontrado con ID: " + id));
        return convertirAResponse(tipoVehiculo);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!tipoVehiculoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Tipo de vehículo no encontrado con ID: " + id);
        }
        tipoVehiculoRepository.deleteById(id);
    }

    private TipoVehiculoResponse convertirAResponse(TipoDeVehiculo tipoVehiculo) {
        return new TipoVehiculoResponse(
                tipoVehiculo.getId_tipo_vehiculo(),
                tipoVehiculo.getNombre_vehiculo(),
                tipoVehiculo.getDescripcion_vehiculo()
        );
    }
}

