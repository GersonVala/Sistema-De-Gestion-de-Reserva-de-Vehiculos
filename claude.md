# Proyecto: Sistema de Renta de Vehículos

## Contexto General

Proyecto académico para programación que implementa un sistema de gestión y reserva de vehículos de alquiler utilizando Spring Boot y arquitectura MVC.

### Tecnologías Requeridas

- Spring Boot 3.5.7
- Spring MVC
- Spring Data JPA
- Spring Security
- Lombok
- Thymeleaf (frontend)
- Bootstrap (estilos)
- MySQL (base de datos)

### Principios de Diseño

- Arquitectura MVC
- Principios SOLID
- Pilares de la programación orientada a objetos: Abstracción, encapsulamiento, polimorfismo y herencia.
- Escalable y simple
- Funcional

---

## Tipos de Usuarios y Funcionalidades

### 1. Cliente (ROL: CLIENTE)

**Funcionalidades:**

- Auto-registro en el sistema
- Login requerido para realizar reservas
- Buscar vehículos por:
  - Fecha de inicio y fin
  - Sucursal de retiro
- Ver detalles de vehículos disponibles
- Crear reservas con:
  - Fecha inicio y sucursal de retiro
  - Fecha fin y sucursal de devolución
  - Método de pago (ficticio, solo registro)
  - Monto (ficticio, solo registro)
- Cancelar sus propias reservas
- **NO puede modificar reservas**, solo cancelarlas

### 2. Vendedor (ROL: VENDEDOR/EMPLEADO)

**Funcionalidades:**

- Asignado a UNA sucursal específica
- Ver reservas SOLO de su sucursal
- Gestionar reservas:
  - Ver reservas PENDIENTES
  - Aceptar reservas (cambia estado a ACEPTADA)
  - Rechazar/Cancelar reservas
- NO puede gestionar múltiples sucursales

### 3. Administrador (ROL: ADMIN)

**Funcionalidades:**

- CRUD completo de Sucursales
- CRUD completo de Vehículos
  - Puede cambiar vehículos de sucursal
- CRUD de Vendedores (crear empleados y asignarlos a sucursales)
- Gestionar usuarios clientes
- Ver TODAS las reservas del sistema (no solo de una sucursal)
- Ver estadísticas/reportes (opcional, futuro)

---

## Flujo de Reservas

### Estado de Reserva

1. **PENDIENTE**: Cliente solicita reserva → Vehículo pasa a estado RESERVADO
2. **ACEPTADA**: Vendedor acepta → Vehículo pasa a estado ENTREGADO
3. **CANCELADA**: Cliente o vendedor cancela → Vehículo pasa a DISPONIBLE

### Estados de Vehículo

- **DISPONIBLE**: Listo para alquilar
- **RESERVADO**: Hay una reserva PENDIENTE
- **ENTREGADO**: Reserva ACEPTADA, cliente tiene el vehículo
- **DESCOMPUESTO**: Fuera de servicio

### Flujo Completo

```
CLIENTE:
1. Selecciona fecha inicio/fin + sucursal
2. Ve vehículos disponibles
3. Crea reserva → Estado: PENDIENTE
4. Vehículo → Estado: RESERVADO

VENDEDOR:
5. Ve reserva pendiente en su sucursal
6. Acepta reserva → Estado: ACEPTADA
7. Vehículo → Estado: ENTREGADO

AL DEVOLVER (manejo con triggers):
8. Fecha fin llega → Vehículo vuelve a sucursal de devolución
9. Vehículo → Estado: DISPONIBLE
```

### Reglas de Negocio

- Un vehículo NO puede tener múltiples reservas para las mismas fechas
- Un vehículo solo puede tener UNA reserva activa a la vez
- Los triggers de BD manejan automáticamente los cambios de estado
- No hay pagos reales, solo se registra método y monto
- Cliente NO puede modificar reservas, solo cancelar

---

## Base de Datos

### Script Principal

**Ubicación:** `scrpts_base_de_datos/ScriptBD.sql`

### Tablas Principales

#### usuarios

- id_usuario (PK, AUTO_INCREMENT)
- nombre, apellido, email (UNIQUE), contra (hasheada)
- dni (UNIQUE), telefono, direccion
- estado (TINYINT, default 1)
- id_rol (FK → roles)

#### roles

- id_rol (PK, AUTO_INCREMENT)
- nombre (VARCHAR 30)
- descripcion (VARCHAR 50)
- Roles: ADMIN, VENDEDOR, CLIENTE

#### vehiculos

- id_vehiculo (PK, AUTO_INCREMENT)
- patente (UNIQUE), modelo, marca, color
- estado (ENUM: 'RESERVADO', 'ENTREGADO', 'DISPONIBLE', 'DESCOMPUESTO')
- cant_puertas, descripcion
- precio_diario (DECIMAL 10,2)
- id_motor (FK → motores)
- id_tipo_vehiculo (FK → tipo_vehiculo)
- id_sucursal (FK → sucursales)
- **AGREGAR:** imagen_url (VARCHAR 255) para guardar URL de imagen

#### motores

- id_motor (PK, AUTO_INCREMENT)
- cilindrada (DECIMAL 3,2)
- caballos_de_fuerza (INT)
- tipo_combustible (ENUM: 'NAFTA', 'DIESEL', 'GNC')
- tipo_motor (ENUM: 'MANUAL', 'HIBRIDO', 'ELECTRICO', 'AUTOMATICO')

#### tipo_vehiculo

- id_tipo_vehiculo (PK, AUTO_INCREMENT)
- tipo (VARCHAR 50): ej. Sedan, SUV, Van, Moto
- caracteristicas (VARCHAR 150)

#### reservas

- id_reserva (PK, AUTO_INCREMENT)
- fecha_inicio, fecha_fin (DATE)
- precio (DECIMAL 10,2)
- metodo_pago (ENUM: 'TRANSFERENCIA', 'TARJETA', 'EFECTIVO')
- estado (ENUM: 'ACEPTADA', 'PENDIENTE', 'CANCELADA', default 'PENDIENTE')
- id_usuario (FK → usuarios)
- id_sucursal_retiro (FK → sucursales)
- id_sucursal_devolucion (FK → sucursales)
- id_vehiculo (FK → vehiculos)

#### sucursales

- id_sucursal (PK, AUTO_INCREMENT)
- nombre, direccion
- estado (TINYINT, default 1)
- **AGREGAR:** imagen_url (VARCHAR 255) para guardar URL de imagen

#### empleados

- id_empleado (PK, AUTO_INCREMENT)
- id_usuario (FK → usuarios)
- id_sucursal (FK → sucursales) - UN vendedor = UNA sucursal
- estado (TINYINT, default 1)

### Triggers

**Ubicación:** `scrpts_base_de_datos/triggers.sql`

Los triggers manejan automáticamente:

1. Cambio de estado de vehículo cuando cambia estado de reserva
2. Liberación de vehículo al cancelar reserva
3. Movimiento de vehículo a sucursal de devolución cuando se cumple fecha_fin
4. Manejo de cambios de vehículo en una reserva

**IMPORTANTE:** Los triggers están bien diseñados, mantenerlos.

### Modificación Necesaria para Imágenes

```sql
-- Agregar a tabla vehiculos
ALTER TABLE vehiculos ADD COLUMN imagen_url VARCHAR(255) AFTER descripcion;

-- Agregar a tabla sucursales
ALTER TABLE sucursales ADD COLUMN imagen_url VARCHAR(255) AFTER direccion;
```

---

## Arquitectura Propuesta (MVC + SOLID)

### Estructura de Capas

```
src/main/java/com/GestionReservasDeVehiculos/RentaDeVehiculos/
│
├── config/                          # Configuraciones
│   ├── SecurityConfig.java          # Spring Security + UserDetailsService
│   └── WebConfig.java               # Configuraciones web (opcional)
│
├── models/                          # Entidades JPA
│   ├── Usuario.java
│   ├── Rol.java
│   ├── Empleado.java
│   ├── Vehiculo.java
│   ├── Motor.java
│   ├── TipoVehiculo.java
│   ├── Sucursal.java
│   ├── Reserva.java
│   └── enums/
│       ├── EstadoVehiculo.java
│       ├── EstadoReserva.java
│       ├── TipoCombustible.java
│       ├── TipoMotor.java
│       └── MetodoPago.java
│
├── dto/                             # Data Transfer Objects
│   ├── request/
│   │   ├── UsuarioRegistroDTO.java
│   │   ├── ReservaRequestDTO.java
│   │   ├── VehiculoRequestDTO.java
│   │   └── SucursalRequestDTO.java
│   └── response/
│       ├── VehiculoResponseDTO.java
│       ├── ReservaResponseDTO.java
│       └── UsuarioResponseDTO.java
│
├── repository/                      # Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── RolRepository.java
│   ├── VehiculoRepository.java
│   ├── ReservaRepository.java
│   ├── SucursalRepository.java
│   ├── EmpleadoRepository.java
│   ├── TipoVehiculoRepository.java
│   └── MotorRepository.java
│
├── service/                         # Interfaces de servicios
│   ├── UsuarioService.java
│   ├── VehiculoService.java
│   ├── ReservaService.java
│   ├── SucursalService.java
│   └── EmpleadoService.java
│
├── service/impl/                    # Implementaciones
│   ├── UsuarioServiceImpl.java
│   ├── VehiculoServiceImpl.java
│   ├── ReservaServiceImpl.java
│   ├── SucursalServiceImpl.java
│   ├── EmpleadoServiceImpl.java
│   └── CustomUserDetailsService.java  # Para Spring Security
│
├── controller/                      # Controladores MVC
│   ├── AuthController.java          # Login/Register
│   ├── ClienteController.java       # Búsqueda y reservas
│   ├── VendedorController.java      # Gestión de reservas
│   └── AdminController.java         # CRUD completo
│
├── exception/                       # Manejo de excepciones
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
│
└── util/                            # Utilidades
    └── ValidationUtil.java
```

### Principios SOLID

1. **Single Responsibility (S)**

   - Cada servicio maneja UNA entidad
   - Controladores separados por rol de usuario

2. **Open/Closed (O)**

   - Interfaces de servicio permiten extender sin modificar
   - Uso de DTOs para desacoplar modelos de API

3. **Liskov Substitution (L)**

   - Implementaciones de servicios intercambiables vía interfaces

4. **Interface Segregation (I)**

   - Repositorios JPA específicos
   - Servicios con métodos cohesivos

5. **Dependency Inversion (D)**
   - Controladores dependen de interfaces Service, no de implementaciones
   - Inyección de dependencias vía constructor

---

## Validaciones Necesarias

### Usuario/Registro

- Email único y formato válido
- DNI único y formato válido
- Contraseña mínimo 6 caracteres
- Campos obligatorios no vacíos

### Reserva

- Fecha fin > fecha inicio
- No permitir reservas en fechas pasadas
- Verificar disponibilidad del vehículo en fechas seleccionadas
- Sucursal de retiro y devolución deben existir
- Precio > 0

### Vehículo

- Patente única
- Precio diario > 0
- Motor, TipoVehiculo y Sucursal deben existir

---

## Plan de Implementación

### Fase 1: Reestructuración y Corrección

1. Crear entidades (tipos de datos, mapeos bidireccionales)
2. Actualizar script BD con campos de imágenes (imagen_url)

### Fase 2: Backend Core

5. Crear todos los repositorios con queries personalizadas:
   - VehiculoRepository: buscar por sucursal, disponibles en fechas
   - ReservaRepository: buscar por usuario, por sucursal, por estado
   - EmpleadoRepository: buscar por usuario, validar sucursal
6. Implementar servicios con lógica de negocio
7. Crear DTOs para Request/Response
8. Configurar CustomUserDetailsService y Spring Security completo
   - Cargar usuarios por email
   - Hasear contraseñas con BCrypt
   - Asignar autoridades basadas en roles

### Fase 3: Controladores

9. **AuthController** (login, registro)

   - GET /login
   - GET /register
   - POST /register

10. **ClienteController** (buscar vehículos, crear reservas)

    - GET /cliente/vehiculos (búsqueda por fecha y sucursal)
    - GET /cliente/reservas (ver mis reservas)
    - POST /cliente/reservas (crear reserva)
    - POST /cliente/reservas/{id}/cancelar

11. **VendedorController** (ver/gestionar reservas de su sucursal)

    - GET /vendedor/reservas (solo de su sucursal)
    - POST /vendedor/reservas/{id}/aceptar
    - POST /vendedor/reservas/{id}/rechazar

12. **AdminController** (CRUD sucursales, vehículos, vendedores)
    - Sucursales: GET, POST, PUT, DELETE /admin/sucursales
    - Vehículos: GET, POST, PUT, DELETE /admin/vehiculos
    - Empleados: GET, POST, PUT, DELETE /admin/empleados
    - Usuarios: GET /admin/usuarios

### Fase 4: Frontend (Thymeleaf + Bootstrap)

13. Ajustar vistas HTML existentes:
    - login.html
    - register.html
    - dashboard.html (redirige según rol)
    - Cliente: reservation.html, reservas.html
    - Vendedor: vendedor-dashboard.html
    - Admin: admin-\*.html
14. Integrar Bootstrap correctamente
15. Implementar validaciones en formularios (Thymeleaf + Bean Validation)
