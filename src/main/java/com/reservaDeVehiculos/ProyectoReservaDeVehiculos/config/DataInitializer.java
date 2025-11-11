package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CiudadRepository ciudadRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final MotorRepository motorRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    public void run(String... args) {
        try {
            // Verificar si ya hay datos cargados
            if (ciudadRepository.count() > 0) {
                System.out.println("✅ Datos ya cargados, omitiendo inicialización.");
                return;
            }

            System.out.println("🚀 Iniciando carga de datos de prueba...");
            inicializarDatos();
        } catch (Exception e) {
            System.err.println("⚠️ Error al inicializar datos: " + e.getMessage());
            e.printStackTrace();
            // No lanzar la excepción para que la aplicación siga corriendo
        }
    }
    
    @Transactional
    private void inicializarDatos() {

        // ============================================
        // 1. CREAR CIUDADES
        // ============================================
        CiudadesEntity buenosAires = crearCiudad("Buenos Aires", "Buenos Aires");
        CiudadesEntity cordoba = crearCiudad("Córdoba", "Córdoba");
        CiudadesEntity mendoza = crearCiudad("Mendoza", "Mendoza");
        CiudadesEntity chaco = crearCiudad("Resistencia", "Chaco");
        CiudadesEntity chubut = crearCiudad("Rawson", "Chubut");
        CiudadesEntity corrientes = crearCiudad("Corrientes", "Corrientes");

        ciudadRepository.saveAll(List.of(buenosAires, cordoba, mendoza, chaco, chubut, corrientes));
        System.out.println("✅ Ciudades creadas: 6");

        // ============================================
        // 2. CREAR DIRECCIONES
        // ============================================
        DireccionesEntity dirBA = crearDireccion("Av. Corrientes", 1234, buenosAires);
        DireccionesEntity dirCba = crearDireccion("Av. Colón", 567, cordoba);
        DireccionesEntity dirMdz = crearDireccion("Av. San Martín", 890, mendoza);
        DireccionesEntity dirChaco = crearDireccion("Av. 25 de Mayo", 345, chaco);
        DireccionesEntity dirChubut = crearDireccion("Av. Roca", 678, chubut);
        DireccionesEntity dirCorrientes = crearDireccion("Av. 3 de Abril", 456, corrientes);

        direccionRepository.saveAll(List.of(dirBA, dirCba, dirMdz, dirChaco, dirChubut, dirCorrientes));
        System.out.println("✅ Direcciones creadas: 6");

        // ============================================
        // 3. CREAR USUARIOS VENDEDORES
        // ============================================
        UsuariosEntity vendedor1 = crearVendedor("Juan", "Pérez", "juan@tgsmax.com", "12345678", "1122334455", dirBA);
        UsuariosEntity vendedor2 = crearVendedor("María", "González", "maria@tgsmax.com", "23456789", "3511223344", dirCba);
        UsuariosEntity vendedor3 = crearVendedor("Carlos", "López", "carlos@tgsmax.com", "34567890", "2614455667", dirMdz);
        UsuariosEntity vendedor4 = crearVendedor("Ana", "Martínez", "ana@tgsmax.com", "45678901", "3624556677", dirChaco);
        UsuariosEntity vendedor5 = crearVendedor("Pedro", "Ramírez", "pedro@tgsmax.com", "56789012", "2804667788", dirChubut);
        UsuariosEntity vendedor6 = crearVendedor("Laura", "Fernández", "laura@tgsmax.com", "67890123", "3794778899", dirCorrientes);

        usuarioRepository.saveAll(List.of(vendedor1, vendedor2, vendedor3, vendedor4, vendedor5, vendedor6));
        System.out.println("✅ Usuarios vendedores creados: 6");

        // ============================================
        // 4. CREAR SUCURSALES
        // ============================================
        SucursalesEntity sucBA = crearSucursal("011-5555-1001", dirBA, vendedor1);
        SucursalesEntity sucCba = crearSucursal("0351-5555-2002", dirCba, vendedor2);
        SucursalesEntity sucMdz = crearSucursal("0261-5555-3003", dirMdz, vendedor3);
        SucursalesEntity sucChaco = crearSucursal("0362-5555-4004", dirChaco, vendedor4);
        SucursalesEntity sucChubut = crearSucursal("0280-5555-5005", dirChubut, vendedor5);
        SucursalesEntity sucCorrientes = crearSucursal("0379-5555-6006", dirCorrientes, vendedor6);

        sucursalRepository.saveAll(List.of(sucBA, sucCba, sucMdz, sucChaco, sucChubut, sucCorrientes));
        System.out.println("✅ Sucursales creadas: 6");

        // ============================================
        // 5. CREAR TIPOS DE VEHÍCULOS
        // ============================================
        TipoDeVehiculo tipoAuto = crearTipoVehiculo("Auto", "Ideal para la ciudad con excelente rendimiento");
        TipoDeVehiculo tipoCamioneta = crearTipoVehiculo("Camioneta", "Espacio y confort para viajes largos");
        TipoDeVehiculo tipoMoto = crearTipoVehiculo("Moto", "Rapidez y eficiencia para moverse por la ciudad");

        tipoVehiculoRepository.saveAll(List.of(tipoAuto, tipoCamioneta, tipoMoto));
        System.out.println("✅ Tipos de vehículos creados: 3");

        // ============================================
        // 6. CREAR MOTORES
        // ============================================
        Motor motor1 = crearMotor(1.6, 110, TipoCombustible.NAFTA, TipoMotor.MANUAL);
        Motor motor2 = crearMotor(2.0, 150, TipoCombustible.NAFTA, TipoMotor.AUTOMATICO);
        Motor motor3 = crearMotor(3.0, 200, TipoCombustible.DIESEL, TipoMotor.AUTOMATICO);
        Motor motor4 = crearMotor(0.3, 25, TipoCombustible.NAFTA, TipoMotor.MANUAL);
        Motor motor5 = crearMotor(1.4, 100, TipoCombustible.NAFTA, TipoMotor.MANUAL);

        motorRepository.saveAll(List.of(motor1, motor2, motor3, motor4, motor5));
        System.out.println("✅ Motores creados: 5");

        // ============================================
        // 7. CREAR VEHÍCULOS
        // ============================================
        // Autos en diferentes sucursales
        VehiculosEntity auto1 = crearVehiculo("Toyota", "Corolla", "AA123BB", "Blanco", 4, motor1, tipoAuto, sucBA);
        VehiculosEntity auto2 = crearVehiculo("Chevrolet", "Cruze", "BB234CC", "Negro", 4, motor2, tipoAuto, sucCba);
        VehiculosEntity auto3 = crearVehiculo("Volkswagen", "Vento", "CC345DD", "Gris", 4, motor5, tipoAuto, sucMdz);
        
        // Camionetas
        VehiculosEntity camioneta1 = crearVehiculo("Ford", "Ranger", "DD456EE", "Azul", 4, motor3, tipoCamioneta, sucBA);
        VehiculosEntity camioneta2 = crearVehiculo("Toyota", "Hilux", "EE567FF", "Rojo", 4, motor3, tipoCamioneta, sucCba);
        VehiculosEntity camioneta3 = crearVehiculo("Chevrolet", "S10", "FF678GG", "Blanco", 4, motor3, tipoCamioneta, sucChaco);
        
        // Motos
        VehiculosEntity moto1 = crearVehiculo("Honda", "Wave", "GG789HH", "Roja", 0, motor4, tipoMoto, sucMdz);
        VehiculosEntity moto2 = crearVehiculo("Yamaha", "FZ", "HH890II", "Negra", 0, motor4, tipoMoto, sucChubut);
        VehiculosEntity moto3 = crearVehiculo("Zanella", "RX150", "II901JJ", "Azul", 0, motor4, tipoMoto, sucCorrientes);

        vehiculoRepository.saveAll(List.of(auto1, auto2, auto3, camioneta1, camioneta2, camioneta3, moto1, moto2, moto3));
        System.out.println("✅ Vehículos creados: 9");

        System.out.println("🎉 ¡Datos de prueba cargados exitosamente!");
        System.out.println("📊 Resumen:");
        System.out.println("   - Ciudades: 6");
        System.out.println("   - Sucursales: 6 (Buenos Aires, Córdoba, Mendoza, Chaco, Chubut, Corrientes)");
        System.out.println("   - Tipos de Vehículos: 3");
        System.out.println("   - Vehículos disponibles: 9");
    } // Fin de inicializarDatos()

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    private CiudadesEntity crearCiudad(String nombre, String estado) {
        CiudadesEntity ciudad = new CiudadesEntity();
        ciudad.setNombre_ciudad(nombre);
        ciudad.setEstado(estado);
        return ciudad;
    }

    private DireccionesEntity crearDireccion(String calle, int numero, CiudadesEntity ciudad) {
        DireccionesEntity direccion = new DireccionesEntity();
        direccion.setCalle(calle);
        direccion.setNumero_calle(numero);
        direccion.setCiudades(ciudad);
        return direccion;
    }

    private UsuariosEntity crearVendedor(String nombre, String apellido, String email, 
                                         String dni, String telefono, DireccionesEntity direccion) {
        UsuariosEntity usuario = new UsuariosEntity();
        usuario.setNombre_usuario(nombre);
        usuario.setApellido_usuario(apellido);
        usuario.setEmail_usuario(email);
        usuario.setContrasena("password123"); // En producción usar BCrypt
        usuario.setDni_usuario(dni);
        usuario.setTelefono_usuario(telefono);
        usuario.setDireccion(direccion);
        return usuario;
    }

    private SucursalesEntity crearSucursal(String telefono, DireccionesEntity direccion, UsuariosEntity vendedor) {
        SucursalesEntity sucursal = new SucursalesEntity();
        sucursal.setTelefono_sucursal(telefono);
        sucursal.setDireccion(direccion);
        sucursal.setVendedor(vendedor);
        return sucursal;
    }

    private TipoDeVehiculo crearTipoVehiculo(String nombre, String descripcion) {
        TipoDeVehiculo tipo = new TipoDeVehiculo();
        tipo.setNombre_vehiculo(nombre);
        tipo.setDescripcion_vehiculo(descripcion);
        return tipo;
    }

    private Motor crearMotor(double cilindrada, int caballos, TipoCombustible combustible, TipoMotor tipoMotor) {
        Motor motor = new Motor();
        motor.setCilindrada(cilindrada);
        motor.setCaballos_de_fuerza(caballos);
        motor.setTipoCombustible(combustible);
        motor.setTipoMotor(tipoMotor);
        return motor;
    }

    private VehiculosEntity crearVehiculo(String marca, String modelo, String patente, String color,
                                          int puertas, Motor motor, TipoDeVehiculo tipo, SucursalesEntity sucursal) {
        VehiculosEntity vehiculo = new VehiculosEntity();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setPatente(patente);
        vehiculo.setColor(color);
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculo.setCant_puertas(puertas);
        vehiculo.setMotor(motor);
        vehiculo.setTipoDeVehiculo(tipo);
        vehiculo.setSucursal(sucursal);
        return vehiculo;
    }
}
