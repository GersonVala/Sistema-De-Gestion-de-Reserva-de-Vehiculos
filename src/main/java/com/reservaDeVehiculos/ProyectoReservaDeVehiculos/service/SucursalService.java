package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearSucursalRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.SucursalResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.DireccionesEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.SucursalesEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.UsuariosEntity;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.DireccionRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.SucursalRepository;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public SucursalResponse crear(CrearSucursalRequest request) {
        DireccionesEntity direccion = direccionRepository.findById(request.getId_direccion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada"));

        UsuariosEntity vendedor = usuarioRepository.findById(request.getId_vendedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vendedor no encontrado"));

        SucursalesEntity sucursal = new SucursalesEntity();
        sucursal.setTelefono_sucursal(request.getTelefono_sucursal());
        sucursal.setDireccion(direccion);
        sucursal.setVendedor(vendedor);

        SucursalesEntity sucursalGuardada = sucursalRepository.save(sucursal);
        return convertirAResponse(sucursalGuardada);
    }

    public List<SucursalResponse> obtenerTodas() {
        return sucursalRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public SucursalResponse obtenerPorId(Integer id) {
        SucursalesEntity sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada con ID: " + id));
        return convertirAResponse(sucursal);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!sucursalRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Sucursal no encontrada con ID: " + id);
        }
        sucursalRepository.deleteById(id);
    }

    private SucursalResponse convertirAResponse(SucursalesEntity sucursal) {
        return new SucursalResponse(
                sucursal.getId_sucursal(),
                sucursal.getTelefono_sucursal()
        );
    }
}

