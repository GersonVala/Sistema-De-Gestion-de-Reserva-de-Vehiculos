package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.service;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.CrearVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.request.ActualizarVehiculoRequest;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.dto.response.VehiculoResponse;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoNoEncontradoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.exception.RecursoDuplicadoException;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.util.VehicleTypeStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final MotorRepository motorRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final SucursalRepository sucursalRepository;
    private final VehicleTypeStrategyFactory vehicleTypeStrategyFactory;

    @Transactional
    public VehiculoResponse crear(CrearVehiculoRequest request) {
        // Validar que no exista la patente
        if (vehiculoRepository.existsByPatente(request.getPatente())) {
            throw new RecursoDuplicadoException("Ya existe un vehículo con la patente: " + request.getPatente());
        }

        // Validar motor
        Motor motor = motorRepository.findById(request.getId_motor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Motor no encontrado"));

        // Validar tipo de vehículo
        TipoDeVehiculo tipoVehiculo = tipoVehiculoRepository.findById(request.getId_tipo_vehiculo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de vehículo no encontrado"));

        // Validar atributos específicos del tipo de vehículo
        vehicleTypeStrategyFactory.getStrategy(tipoVehiculo.getNombre_vehiculo()).validateSpecificAttributes(request);

        // Validar sucursal
        SucursalesEntity sucursal = sucursalRepository.findById(request.getId_sucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));

        // Crear vehículo
        VehiculosEntity vehiculo = new VehiculosEntity();
        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setPatente(request.getPatente());
        vehiculo.setColor(request.getColor());
        vehiculo.setEstado(request.getEstado());
        vehiculo.setCant_puertas(request.getCant_puertas());
        vehiculo.setMotor(motor);
        vehiculo.setTipoDeVehiculo(tipoVehiculo);
        vehiculo.setSucursal(sucursal);

        VehiculosEntity vehiculoGuardado = vehiculoRepository.save(vehiculo);
        return convertirAResponse(vehiculoGuardado);
    }

    public List<VehiculoResponse> obtenerTodos() {
        return vehiculoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public VehiculoResponse obtenerPorId(Integer id) {
        VehiculosEntity vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado con ID: " + id));
        return convertirAResponse(vehiculo);
    }

    public List<VehiculoResponse> obtenerPorEstado(EstadoVehiculo estado) {
        return vehiculoRepository.findByEstado(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<VehiculoResponse> obtenerDisponibles() {
        return obtenerPorEstado(EstadoVehiculo.DISPONIBLE);
    }

    @Transactional
    public VehiculoResponse actualizar(Integer id, ActualizarVehiculoRequest request) {
        VehiculosEntity vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado con ID: " + id));

        if (request.getMarca() != null) {
            vehiculo.setMarca(request.getMarca());
        }
        if (request.getModelo() != null) {
            vehiculo.setModelo(request.getModelo());
        }
        if (request.getColor() != null) {
            vehiculo.setColor(request.getColor());
        }
        if (request.getEstado() != null) {
            vehiculo.setEstado(request.getEstado());
        }
        if (request.getCant_puertas() != null) {
            vehiculo.setCant_puertas(request.getCant_puertas());
        }
        if (request.getId_motor() != null) {
            Motor motor = motorRepository.findById(request.getId_motor())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Motor no encontrado"));
            vehiculo.setMotor(motor);
        }
        if (request.getId_tipo_vehiculo() != null) {
            TipoDeVehiculo tipoVehiculo = tipoVehiculoRepository.findById(request.getId_tipo_vehiculo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de vehículo no encontrado"));
            vehiculo.setTipoDeVehiculo(tipoVehiculo);
        }
        if (request.getId_sucursal() != null) {
            SucursalesEntity sucursal = sucursalRepository.findById(request.getId_sucursal())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));
            vehiculo.setSucursal(sucursal);
        }

        VehiculosEntity vehiculoActualizado = vehiculoRepository.save(vehiculo);
        return convertirAResponse(vehiculoActualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Vehículo no encontrado con ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }

    @Transactional
    public void cambiarEstado(Integer id, EstadoVehiculo nuevoEstado) {
        VehiculosEntity vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado con ID: " + id));
        vehiculo.setEstado(nuevoEstado);
        vehiculoRepository.save(vehiculo);
    }

    private VehiculoResponse convertirAResponse(VehiculosEntity vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId_vehiculo(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getPatente(),
                vehiculo.getColor(),
                vehiculo.getEstado(),
                vehiculo.getCant_puertas(),
                vehiculo.getTipoDeVehiculo().getNombre_vehiculo(),
                vehiculo.getTipoDeVehiculo().getDescripcion_vehiculo(),
                vehiculo.getMotor().getCilindrada(),
                vehiculo.getMotor().getCaballos_de_fuerza(),
                vehiculo.getMotor().getTipoCombustible().name(),
                vehiculo.getMotor().getTipoMotor().name()
        );
    }
}
