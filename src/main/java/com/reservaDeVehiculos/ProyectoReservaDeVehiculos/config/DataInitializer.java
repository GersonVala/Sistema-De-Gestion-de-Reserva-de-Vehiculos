package com.reservaDeVehiculos.ProyectoReservaDeVehiculos.config;

import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.entity.*;
import com.reservaDeVehiculos.ProyectoReservaDeVehiculos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CiudadRepository ciudadRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final SucursalRepository sucursalRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final MotorRepository motorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PasswordEncoder passwordEncoder;

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
        // 0. CREAR ROLES (PRIMERO)
        // ============================================
        System.out.println("📋 Creando roles...");
        RolesEntity rolAdmin = crearRol(RolEnum.ADMINISTRADOR.name(), "Administrador del sistema");
        System.out.println("   ✅ Rol ADMINISTRADOR creado - ID: " + rolAdmin.getId_rol());
        
        RolesEntity rolCliente = crearRol(RolEnum.CLIENTE.name(), "Cliente del sistema");
        System.out.println("   ✅ Rol CLIENTE creado - ID: " + rolCliente.getId_rol());
        
        RolesEntity rolVendedor = crearRol(RolEnum.VENDEDOR.name(), "Vendedor de sucursal");
        System.out.println("   ✅ Rol VENDEDOR creado - ID: " + rolVendedor.getId_rol());
        
        System.out.println("✅ Roles creados: 3");
        
        // Verificar que los roles estén en BD
        long totalRoles = rolRepository.count();
        System.out.println("📊 Total de roles en BD: " + totalRoles);

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
        // 2. CREAR DIRECCIONES (una por cada usuario + direcciones para sucursales)
        // ============================================
        // Direcciones para usuarios
        DireccionesEntity dirAdmin = crearDireccion("Av. Corrientes", 1234, buenosAires);
        DireccionesEntity dirVendedor1 = crearDireccion("Av. Callao", 2000, buenosAires);
        DireccionesEntity dirVendedor2 = crearDireccion("Av. Colón", 567, cordoba);
        DireccionesEntity dirVendedor3 = crearDireccion("Av. San Martín", 890, mendoza);
        DireccionesEntity dirCliente1 = crearDireccion("Av. Roca", 678, chubut);
        DireccionesEntity dirCliente2 = crearDireccion("Av. 3 de Abril", 456, corrientes);
        DireccionesEntity dirCliente3 = crearDireccion("Av. Rivadavia", 5000, buenosAires);
        
        // Direcciones para sucursales
        DireccionesEntity dirSucBA = crearDireccion("Av. 9 de Julio", 100, buenosAires);
        DireccionesEntity dirSucCba = crearDireccion("Av. Hipólito Yrigoyen", 200, cordoba);
        DireccionesEntity dirSucMdz = crearDireccion("Av. Las Heras", 300, mendoza);

        List<DireccionesEntity> direccionesGuardadas = direccionRepository.saveAll(List.of(
            dirAdmin, dirVendedor1, dirVendedor2, dirVendedor3, 
            dirCliente1, dirCliente2, dirCliente3,
            dirSucBA, dirSucCba, dirSucMdz
        ));
        System.out.println("✅ Direcciones creadas: " + direccionesGuardadas.size() + " (7 usuarios + 3 sucursales)");

        // ============================================
        // 3. CREAR ADMINISTRADOR
        // ============================================
        String passwordEncriptada = passwordEncoder.encode("password");
        UsuariosEntity admin = crearUsuario("Admin", "Sistema", "admin@tgsmax.com", passwordEncriptada, "00000000", "1111111111", dirAdmin);
        asignarRol(admin, rolAdmin);
        System.out.println("✅ Administrador creado: admin@tgsmax.com / password");

        // ============================================
        // 4. CREAR USUARIOS VENDEDORES
        // ============================================
        UsuariosEntity vendedor1 = crearUsuario("Juan", "Pérez", "juan@tgsmax.com", passwordEncriptada, "12345678", "1122334455", dirVendedor1);
        UsuariosEntity vendedor2 = crearUsuario("María", "González", "maria@tgsmax.com", passwordEncriptada, "23456789", "3511223344", dirVendedor2);
        UsuariosEntity vendedor3 = crearUsuario("Carlos", "López", "carlos@tgsmax.com", passwordEncriptada, "34567890", "2614455667", dirVendedor3);

        System.out.println("✅ Usuarios vendedores creados: 3");

        // ============================================
        // 5. CREAR CLIENTES
        // ============================================
        UsuariosEntity cliente1 = crearUsuario("Pedro", "Ramírez", "pedro@email.com", passwordEncriptada, "56789012", "2804667788", dirCliente1);
        UsuariosEntity cliente2 = crearUsuario("Laura", "Fernández", "laura@email.com", passwordEncriptada, "67890123", "3794778899", dirCliente2);
        UsuariosEntity cliente3 = crearUsuario("Roberto", "Silva", "roberto@email.com", passwordEncriptada, "78901234", "1155443322", dirCliente3);

        asignarRol(cliente1, rolCliente);
        asignarRol(cliente2, rolCliente);
        asignarRol(cliente3, rolCliente);
        System.out.println("✅ Clientes creados: 3 (pedro@email.com, laura@email.com, roberto@email.com) / password");

        // ============================================
        // 6. CREAR SUCURSALES
        // ============================================
        SucursalesEntity sucBA = crearSucursal("011-5555-1001", dirSucBA, vendedor1);
        SucursalesEntity sucCba = crearSucursal("0351-5555-2002", dirSucCba, vendedor2);
        SucursalesEntity sucMdz = crearSucursal("0261-5555-3003", dirSucMdz, vendedor3);

        sucursalRepository.saveAll(List.of(sucBA, sucCba, sucMdz));
        System.out.println("✅ Sucursales creadas: 3");

        // Asignar sucursales a vendedores
        vendedor1.setSucursal(sucBA);
        vendedor2.setSucursal(sucCba);
        vendedor3.setSucursal(sucMdz);
        usuarioRepository.saveAll(List.of(vendedor1, vendedor2, vendedor3));

        // Asignar rol VENDEDOR
        asignarRol(vendedor1, rolVendedor);
        asignarRol(vendedor2, rolVendedor);
        asignarRol(vendedor3, rolVendedor);
        System.out.println("✅ Vendedores asignados a sucursales y roles configurados");

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
        VehiculosEntity camioneta3 = crearVehiculo("Chevrolet", "S10", "FF678GG", "Blanco", 4, motor3, tipoCamioneta, sucMdz);
        
        // Motos
        VehiculosEntity moto1 = crearVehiculo("Honda", "Wave", "GG789HH", "Roja", 0, motor4, tipoMoto, sucBA);
        VehiculosEntity moto2 = crearVehiculo("Yamaha", "FZ", "HH890II", "Negra", 0, motor4, tipoMoto, sucCba);
        VehiculosEntity moto3 = crearVehiculo("Zanella", "RX150", "II901JJ", "Azul", 0, motor4, tipoMoto, sucMdz);

        vehiculoRepository.saveAll(List.of(auto1, auto2, auto3, camioneta1, camioneta2, camioneta3, moto1, moto2, moto3));
        System.out.println("✅ Vehículos creados: 9");

        System.out.println("🎉 ¡Datos de prueba cargados exitosamente!");
        System.out.println("📊 Resumen:");
        System.out.println("   - Usuarios: 7 (1 admin, 3 vendedores, 3 clientes)");
        System.out.println("   - Direcciones: 10 (7 para usuarios, 3 para sucursales)");
        System.out.println("   - Ciudades: 6");
        System.out.println("   - Sucursales: 3 (Buenos Aires, Córdoba, Mendoza)");
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

    private UsuariosEntity crearUsuario(String nombre, String apellido, String email, 
                                        String password, String dni, String telefono, DireccionesEntity direccion) {
        UsuariosEntity usuario = new UsuariosEntity();
        usuario.setNombre_usuario(nombre);
        usuario.setApellido_usuario(apellido);
        usuario.setEmail_usuario(email);
        usuario.setContrasena(password); // Ya viene encriptado desde el método que llama
        usuario.setDni_usuario(dni);
        usuario.setTelefono_usuario(telefono);
        usuario.setDireccion(direccion);
        return usuarioRepository.save(usuario);
    }

    private void asignarRol(UsuariosEntity usuario, RolesEntity rol) {
        Usuario_rolesEntity usuarioRol = new Usuario_rolesEntity();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        usuarioRolRepository.save(usuarioRol);
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

    private RolesEntity crearRol(String nombreRol, String descripcion) {
        RolesEntity rol = new RolesEntity();
        rol.setNombre_rol(nombreRol);
        rol.setDescripcion_rol(descripcion);
        return rolRepository.save(rol);
    }
}
