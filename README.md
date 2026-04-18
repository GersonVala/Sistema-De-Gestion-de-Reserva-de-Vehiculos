# Sistema de Gestión de Reserva de Vehículos

Aplicación web para gestionar el alquiler de vehículos: administración de flota,
usuarios con roles y reservas. Desarrollada en grupo como **proyecto final grupal** de la
materia Programación II en el Técnico Superior en Informática Aplicada (INSPT–UTN).

## Objetivo del proyecto

Aplicar los fundamentos aprendidos en la materia: programación orientada a objetos,
capas de aplicación (MVC), persistencia con base de datos relacional, autenticación
y control de acceso por roles.

## Stack técnico

- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.5.7
- **Vistas:** Thymeleaf + thymeleaf-extras-springsecurity6
- **Persistencia:** Spring Data JPA + Hibernate
- **Validaciones:** Spring Boot Validation (Bean Validation / `@Valid`)
- **Seguridad:** Spring Security 6 con autenticación por base de datos y BCrypt
- **Base de datos:** MySQL (`reserva_vehiculos`)
- **Build:** Maven (Maven Wrapper incluido)
- **Utilidades:** Lombok

## Funcionalidades

La aplicación tiene tres roles con vistas y rutas separadas: **ADMINISTRADOR**, **VENDEDOR** y **CLIENTE**.

### Administrador (`/admin/**`)
- CRUD completo de vehículos (con tipo, motor, imagen, precio diario, sucursal)
- CRUD de tipos de vehículo y motores
- CRUD de sucursales (alta, baja lógica, edición)
- Gestión de usuarios: alta, edición, activación/desactivación, cambio de rol
- Gestión de empleados: asignación de vendedores a sucursales
- Visualización de todas las reservas del sistema

### Vendedor (`/vendedor/**`)
- Visualización de reservas de su sucursal
- Aceptar o cancelar reservas pendientes
- Ver detalle de reservas

### Cliente (`/cliente/**`)
- Explorar catálogo de vehículos disponibles con filtros
- Crear reservas: selección de vehículo, fechas, sucursal de retiro y devolución, método de pago
- Cancelar reservas propias en estado PENDIENTE
- Ver historial de reservas

### Funcionalidades transversales
- Registro de nuevos usuarios (rol CLIENTE por defecto)
- Login con email y contraseña
- Perfil de usuario: edición de datos personales y cambio de contraseña
- Página pública con catálogo de vehículos disponibles

## Arquitectura

Aplicación web bajo patrón **MVC** con las siguientes capas:

```
src/main/java/ProyectoRentaDeAutos/RentaDeAutos/
├── config/         → Seguridad (SecurityConfig), interceptores y filtros HTTP
├── controller/     → Controllers por rol: AdminController, VendedorController, ClienteController, AuthController, PerfilController, IndexController, DashboardController
├── service/        → Interfaces de servicio (una por entidad)
│   └── impl/       → Implementaciones concretas + CustomUserDetailsService
├── repository/     → Interfaces que extienden JpaRepository (patrón Repository)
├── models/         → Entidades JPA: Usuario, Vehiculo, Reserva, Sucursal, Empleado, Motor, TipoVehiculo, Rol
│   └── enums/      → Enumeraciones de dominio (estado de reserva, combustible, etc.)
├── dto/            → Objetos de transferencia de datos
└── exception/      → Excepciones de negocio: BusinessException, ResourceNotFoundException
```

Los servicios se definen como interfaces y se implementan en `impl/`, lo que permite
inyección de dependencias por constructor en los controllers.

### Base de datos

El esquema está en `scrpts_base_de_datos/ScriptBD.sql`. Incluye tablas para todas las
entidades del dominio con sus relaciones. En `triggers.sql` hay triggers MySQL que
sincronizan automáticamente el estado del vehículo (RESERVADO / ENTREGADO / DISPONIBLE)
cuando cambia el estado de una reserva. `CargaDeDatos.sql` contiene datos de ejemplo
para pruebas.

## Cómo correrlo localmente

### Requisitos

- Java 17
- Maven (o usar el wrapper incluido: `./mvnw`)
- MySQL corriendo localmente en el puerto 3306

### Pasos

```bash
git clone https://github.com/GersonVala/Sistema-De-Gestion-de-Reserva-de-Vehiculos.git
cd Sistema-De-Gestion-de-Reserva-de-Vehiculos
```

**1. Crear la base de datos:**

```sql
-- Ejecutar en MySQL Workbench o consola:
source scrpts_base_de_datos/ScriptBD.sql
source scrpts_base_de_datos/triggers.sql
source scrpts_base_de_datos/CargaDeDatos.sql  -- opcional, carga datos de ejemplo
```

**2. Configurar credenciales MySQL:**

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reserva_vehiculos
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

**3. Compilar y ejecutar:**

```bash
./mvnw spring-boot:run
# o en Windows:
mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`

### Usuarios de prueba (con datos de CargaDeDatos.sql)

Si cargaste los datos de ejemplo, podés ingresar con:

| Email | Contraseña | Rol |
|-------|------------|-----|
| gerson@example.com | `Gerson123` | ADMINISTRADOR |
| juan@example.com | `Juan123` | VENDEDOR |
| maria@example.com | `Maria123` | CLIENTE |

> Las contraseñas en la base de datos de ejemplo están en texto plano. En un flujo real de registro, el sistema hashea con BCrypt automáticamente.

## Documentación

La carpeta `Documentacion/` contiene:
- Diagrama UML de clases (`DiagramaUMLClases.png`)
- Diagramas de base de datos
- Casos de uso
- Documento completo del proyecto (PDF)

## Notas

- Este fue un **proyecto académico grupal** con foco didáctico, no una aplicación
  pensada para producción.
- El código refleja el nivel de la materia al momento de cursarla (2025).
- Mantenido público como parte del historial de aprendizaje.
