package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearDireccionRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.DireccionResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.CiudadesEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.DireccionesEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.CiudadRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.DireccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final CiudadRepository ciudadRepository;

    @Transactional
    public DireccionResponse crear(CrearDireccionRequest request) {
        CiudadesEntity ciudad = ciudadRepository.findById(request.getId_ciudad())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudad no encontrada"));

        DireccionesEntity direccion = new DireccionesEntity();
        direccion.setCalle(request.getCalle());
        direccion.setNumero_calle(request.getNumero_calle());
        direccion.setCiudades(ciudad);

        DireccionesEntity direccionGuardada = direccionRepository.save(direccion);
        return convertirAResponse(direccionGuardada);
    }

    public List<DireccionResponse> obtenerTodas() {
        return direccionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public DireccionResponse obtenerPorId(Integer id) {
        DireccionesEntity direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada con ID: " + id));
        return convertirAResponse(direccion);
    }

    public List<DireccionResponse> obtenerPorCiudad(Integer idCiudad) {
        return direccionRepository.findByCiudadId(idCiudad).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!direccionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Dirección no encontrada con ID: " + id);
        }
        direccionRepository.deleteById(id);
    }

    private DireccionResponse convertirAResponse(DireccionesEntity direccion) {
        return new DireccionResponse(
                direccion.getId_direccion(),
                direccion.getCalle(),
                direccion.getNumero_calle(),
                direccion.getCiudades().getNombre_ciudad(),
                direccion.getCiudades().getEstado()
        );
    }
}

