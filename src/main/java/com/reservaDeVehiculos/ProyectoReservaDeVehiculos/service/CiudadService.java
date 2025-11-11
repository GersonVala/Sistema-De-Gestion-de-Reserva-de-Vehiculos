package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearCiudadRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.CiudadResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.CiudadesEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoDuplicadoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CiudadService {

    private final CiudadRepository ciudadRepository;

    @Transactional
    public CiudadResponse crear(CrearCiudadRequest request) {
        if (ciudadRepository.existsByNombreCiudad(request.getNombre_ciudad())) {
            throw new RecursoDuplicadoException("Ya existe una ciudad con el nombre: " + request.getNombre_ciudad());
        }

        CiudadesEntity ciudad = new CiudadesEntity();
        ciudad.setNombre_ciudad(request.getNombre_ciudad());
        ciudad.setEstado(request.getEstado());

        CiudadesEntity ciudadGuardada = ciudadRepository.save(ciudad);
        return convertirAResponse(ciudadGuardada);
    }

    public List<CiudadResponse> obtenerTodas() {
        return ciudadRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public CiudadResponse obtenerPorId(Integer id) {
        CiudadesEntity ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudad no encontrada con ID: " + id));
        return convertirAResponse(ciudad);
    }

    public List<CiudadResponse> obtenerPorEstado(String estado) {
        return ciudadRepository.findByEstado(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!ciudadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Ciudad no encontrada con ID: " + id);
        }
        ciudadRepository.deleteById(id);
    }

    private CiudadResponse convertirAResponse(CiudadesEntity ciudad) {
        return new CiudadResponse(
                ciudad.getId_ciudad(),
                ciudad.getNombre_ciudad(),
                ciudad.getEstado()
        );
    }
}

