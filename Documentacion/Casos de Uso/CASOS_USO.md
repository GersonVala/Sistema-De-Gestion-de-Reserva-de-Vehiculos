# Casos de Uso Detallados - Sistema de Renta de Vehículos

## 1. Casos de Uso por Actor

### 1.1 Mapa de Casos de Uso

```
┌─────────────────────────────────────────────────────────────┐
│                    SISTEMA RENTA VEHÍCULOS                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   CLIENTE                VENDEDOR              ADMIN        │
│     │                       │                    │          │
│     │                       │                    │          │
│     ├─ CU-001: Registrarse │                    │          │
│     ├─ CU-002: Login       │                    │          │
│     ├─ CU-003: Buscar      │                    │          │
│     │   Vehículos          │                    │          │
│     ├─ CU-004: Crear       │                    │          │
│     │   Reserva            │                    │          │
│     ├─ CU-005: Ver Mis     │                    │          │
│     │   Reservas           │                    │          │
│     ├─ CU-006: Cancelar    │                    │          │
│     │   Reserva            │                    │          │
│     │                      │                    │          │
│     │                      ├─ CU-007: Ver       │          │
│     │                      │   Reservas Sucursal│          │
│     │                      ├─ CU-008: Aceptar   │          │
│     │                      │   Reserva          │          │
│     │                      ├─ CU-009: Rechazar  │          │
│     │                      │   Reserva          │          │
│     │                      │                    │          │
│     │                      │                    ├─ CU-010: │
│     │                      │                    │  CRUD     │
│     │                      │                    │  Sucursal │
│     │                      │                    ├─ CU-011: │
│     │                      │                    │  CRUD     │
│     │                      │                    │  Vehículo │
│     │                      │                    ├─ CU-012: │
│     │                      │                    │  CRUD     │
│     │                      │                    │  Empleado │
│     │                      │                    ├─ CU-013: │
│     │                      │                    │  Ver Todos│
│     │                      │                    │  Reservas │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Casos de Uso - CLIENTE

### CU-001: Registrarse en el Sistema

**Actor:** Usuario anónimo (futuro cliente)

**Precondiciones:**

- Usuario no tiene cuenta
- Sistema operativo

**Flujo Principal:**

1. Usuario accede a la página de registro (`/register`)
2. Sistema muestra formulario de registro
3. Usuario ingresa datos:
   - Nombre, Apellido
   - Email, Contraseña
   - DNI, Teléfono, Dirección
4. Usuario hace clic en "Registrarse"
5. Sistema valida datos
6. Sistema hashea contraseña con BCrypt
7. Sistema crea usuario con rol CLIENTE
8. Sistema redirige a página de login
9. Sistema muestra mensaje: "Registro exitoso, inicie sesión"

**Flujos Alternativos:**

**FA-001: Email ya existe**

- 5a. Sistema detecta email duplicado
- 5b. Sistema muestra error: "El email ya está registrado"
- 5c. Retorna al paso 3

**FA-002: DNI ya existe**

- 5a. Sistema detecta DNI duplicado
- 5b. Sistema muestra error: "El DNI ya está registrado"
- 5c. Retorna al paso 3

**FA-003: Datos inválidos**

- 5a. Sistema detecta campos vacíos o formato inválido
- 5b. Sistema muestra errores específicos por campo
- 5c. Retorna al paso 3

**Postcondiciones:**

- Usuario creado en BD con rol CLIENTE
- Contraseña hasheada almacenada
- Usuario puede iniciar sesión

**Validaciones:**

```java
// UsuarioRegistroDTO
- @NotBlank nombre, apellido, email, contra, dni
- @Email email
- @Size(min=6) contra
- @Pattern(regexp="[0-9]+") dni
```

---

### CU-002: Iniciar Sesión

**Actor:** Cliente registrado

**Precondiciones:**

- Usuario tiene cuenta activa
- Usuario no está autenticado

**Flujo Principal:**

1. Usuario accede a `/login`
2. Sistema muestra formulario de login
3. Usuario ingresa email y contraseña
4. Usuario hace clic en "Iniciar sesión"
5. Sistema verifica credenciales (Spring Security + CustomUserDetailsService)
6. Sistema carga rol del usuario
7. Sistema crea sesión autenticada
8. Sistema redirige a `/dashboard`
9. Dashboard redirige según rol a:
   - CLIENTE → `/cliente/vehiculos`
   - VENDEDOR → `/vendedor/reservas`
   - ADMIN → `/admin/sucursales`

**Flujos Alternativos:**

**FA-001: Credenciales incorrectas**

- 5a. Sistema no encuentra usuario o contraseña no coincide
- 5b. Sistema muestra: "Email o contraseña incorrectos"
- 5c. Retorna al paso 3

**FA-002: Usuario inactivo**

- 5a. Sistema detecta estado = 0
- 5b. Sistema muestra: "Cuenta desactivada, contacte administrador"
- 5c. Termina caso de uso

**Postcondiciones:**

- Sesión autenticada creada
- Usuario puede acceder a endpoints protegidos
- Spring Security almacena Principal con roles

---

### CU-003: Buscar Vehículos Disponibles

**Actor:** Cliente autenticado

**Precondiciones:**

- Cliente con sesión activa
- Al menos 1 sucursal activa en BD

**Flujo Principal:**

1. Cliente accede a `/cliente/vehiculos`
2. Sistema muestra formulario de búsqueda con:
   - Fecha inicio (date picker)
   - Fecha fin (date picker)
   - Sucursal de retiro (dropdown)
3. Cliente selecciona:
   - Fecha inicio: 2025-12-20
   - Fecha fin: 2025-12-25
   - Sucursal: Buenos Aires (id=1)
4. Cliente hace clic en "Buscar"
5. Sistema valida fechas
6. Sistema ejecuta query:
   ```sql
   SELECT v.* FROM vehiculos v
   WHERE v.id_sucursal = 1
   AND v.estado = 'DISPONIBLE'
   AND v.id_vehiculo NOT IN (
     SELECT r.id_vehiculo FROM reservas r
     WHERE r.estado IN ('PENDIENTE', 'ACEPTADA')
     AND ((r.fecha_inicio <= '2025-12-25' AND r.fecha_fin >= '2025-12-20'))
   )
   ```
7. Sistema retorna lista de vehículos disponibles
8. Sistema muestra cards con:
   - Imagen, Marca/Modelo, Precio/día
   - Botón "Reservar"

**Flujos Alternativos:**

**FA-001: Fecha fin anterior a fecha inicio**

- 5a. Sistema detecta fechas inválidas
- 5b. Sistema muestra: "Fecha fin debe ser posterior a fecha inicio"
- 5c. Retorna al paso 3

**FA-002: Fechas en el pasado**

- 5a. Sistema detecta fecha_inicio < hoy
- 5b. Sistema muestra: "No se permiten reservas en fechas pasadas"
- 5c. Retorna al paso 3

**FA-003: Sin vehículos disponibles**

- 7a. Query retorna lista vacía
- 7b. Sistema muestra: "No hay vehículos disponibles para esas fechas"
- 7c. Termina caso de uso

**Postcondiciones:**

- Cliente ve vehículos disponibles
- Cliente puede proceder a reservar

**Diagrama de Secuencia:**

```
Cliente    Controller    Service    Repository    BD
  │──GET──────>│            │           │          │
  │ /vehiculos │            │           │          │
  │            │──buscar()─>│           │          │
  │            │            │──find()──>│          │
  │            │            │           │─SELECT──>│
  │            │            │           │<─Results─│
  │            │            │<─List─────│          │
  │            │<─DTOs──────│           │          │
  │<─HTML──────│            │           │          │
```

---

### CU-004: Crear Reserva

**Actor:** Cliente autenticado

**Precondiciones:**

- Cliente tiene sesión activa
- Cliente ha buscado vehículos
- Vehículo seleccionado está DISPONIBLE

**Flujo Principal:**

1. Cliente hace clic en "Reservar" en un vehículo
2. Sistema muestra formulario pre-llenado:
   - Vehículo: Toyota Corolla (id=7)
   - Fecha inicio: 2025-12-20 (readonly)
   - Fecha fin: 2025-12-25 (readonly)
   - Sucursal retiro: Buenos Aires (readonly)
   - Sucursal devolución: (dropdown editable)
   - Método de pago: (dropdown: TRANSFERENCIA, TARJETA, EFECTIVO)
   - Precio calculado: $40,000 (8000/día \* 5 días)
3. Cliente selecciona:
   - Sucursal devolución: Buenos Aires
   - Método pago: TARJETA
4. Cliente hace clic en "Confirmar Reserva"
5. Sistema valida datos
6. Sistema verifica disponibilidad nuevamente
7. Sistema calcula precio final
8. Sistema crea entidad Reserva:
   - estado = PENDIENTE
   - id_usuario = cliente autenticado
   - Resto de datos del form
9. Sistema guarda reserva
10. Trigger `after_reserva_insert` cambia vehículo a RESERVADO
11. Sistema redirige a `/cliente/reservas`
12. Sistema muestra: "Reserva creada exitosamente, estado: PENDIENTE"

**Flujos Alternativos:**

**FA-001: Vehículo ya no disponible**

- 6a. Otro cliente reservó en el mismo instante
- 6b. Sistema detecta conflicto de fechas
- 6c. Sistema muestra: "El vehículo ya no está disponible"
- 6d. Sistema redirige a búsqueda

**FA-002: Sucursal devolución inválida**

- 5a. Cliente selecciona sucursal inactiva
- 5b. Sistema muestra: "Sucursal no disponible"
- 5c. Retorna al paso 3

**FA-003: Error en transacción**

- 9a. Falla al guardar (BD caída, etc.)
- 9b. Sistema hace rollback
- 9c. Sistema muestra: "Error al crear reserva, intente nuevamente"

**Postcondiciones:**

- Reserva creada con estado PENDIENTE
- Vehículo en estado RESERVADO
- Cliente ve reserva en "Mis Reservas"
- Vendedor de la sucursal ve reserva pendiente

**Código clave:**

```java
@Transactional
public ReservaResponseDTO crearReserva(ReservaRequestDTO request, Integer idUsuario) {
    // Validar fechas
    validarFechas(request);

    // Verificar disponibilidad (double-check)
    if (!vehiculoService.isDisponible(request.getIdVehiculo(),
                                      request.getFechaInicio(),
                                      request.getFechaFin())) {
        throw new BusinessException("Vehículo no disponible");
    }

    // Obtener entidades
    Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
    Vehiculo vehiculo = vehiculoRepository.findById(request.getIdVehiculo()).orElseThrow();
    Sucursal sucRetiro = sucursalRepository.findById(request.getIdSucursalRetiro()).orElseThrow();
    Sucursal sucDevol = sucursalRepository.findById(request.getIdSucursalDevolucion()).orElseThrow();

    // Calcular precio
    long dias = ChronoUnit.DAYS.between(request.getFechaInicio(), request.getFechaFin());
    BigDecimal precio = vehiculo.getPrecioDiario().multiply(BigDecimal.valueOf(dias));

    // Crear reserva
    Reserva reserva = Reserva.builder()
        .fechaInicio(request.getFechaInicio())
        .fechaFin(request.getFechaFin())
        .precio(precio)
        .metodoPago(request.getMetodoPago())
        .estado(EstadoReserva.PENDIENTE)
        .usuario(usuario)
        .vehiculo(vehiculo)
        .sucursalRetiro(sucRetiro)
        .sucursalDevolucion(sucDevol)
        .build();

    // Guardar (trigger cambia estado vehículo)
    Reserva guardada = reservaRepository.save(reserva);

    return convertirADTO(guardada);
}
```

---

### CU-005: Ver Mis Reservas

**Actor:** Cliente autenticado

**Precondiciones:**

- Cliente con sesión activa

**Flujo Principal:**

1. Cliente accede a `/cliente/reservas`
2. Sistema obtiene id del cliente autenticado
3. Sistema ejecuta:
   ```java
   List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idCliente);
   ```
4. Sistema convierte a DTOs
5. Sistema muestra tabla con:
   - ID Reserva
   - Vehículo (marca/modelo)
   - Fechas (inicio - fin)
   - Precio
   - Estado (badge coloreado: PENDIENTE=amarillo, ACEPTADA=verde, CANCELADA=rojo)
   - Acciones: Botón "Cancelar" si estado=PENDIENTE o ACEPTADA

**Flujos Alternativos:**

**FA-001: Sin reservas**

- 3a. Query retorna lista vacía
- 3b. Sistema muestra: "No tienes reservas aún"
- 3c. Muestra botón "Buscar vehículos"

**Postcondiciones:**

- Cliente visualiza todas sus reservas
- Cliente puede cancelar reservas

---

### CU-006: Cancelar Reserva

**Actor:** Cliente autenticado

**Precondiciones:**

- Cliente tiene reserva en estado PENDIENTE o ACEPTADA
- Cliente es dueño de la reserva

**Flujo Principal:**

1. Cliente en `/cliente/reservas` hace clic en "Cancelar" (id_reserva=42)
2. Sistema muestra modal de confirmación:
   "¿Está seguro de cancelar la reserva #42?"
3. Cliente confirma
4. Sistema verifica:
   - Reserva existe
   - Reserva pertenece al cliente
   - Estado es PENDIENTE o ACEPTADA
5. Sistema actualiza:
   ```java
   reserva.setEstado(EstadoReserva.CANCELADA);
   reservaRepository.save(reserva);
   ```
6. Trigger `after_reserva_update` cambia vehículo a DISPONIBLE
7. Sistema recarga página
8. Sistema muestra: "Reserva cancelada exitosamente"

**Flujos Alternativos:**

**FA-001: Reserva ya cancelada**

- 4a. Estado ya es CANCELADA
- 4b. Sistema muestra: "La reserva ya fue cancelada"
- 4c. Termina caso de uso

**FA-002: Reserva no pertenece al cliente**

- 4a. id_usuario de reserva != id_usuario autenticado
- 4b. Sistema muestra: "No autorizado"
- 4c. Termina caso de uso

**Postcondiciones:**

- Reserva en estado CANCELADA
- Vehículo en estado DISPONIBLE
- Vehículo disponible para nuevas reservas

---

## 3. Casos de Uso - VENDEDOR

### CU-007: Ver Reservas de Mi Sucursal

**Actor:** Vendedor autenticado

**Precondiciones:**

- Vendedor con sesión activa
- Vendedor asignado a una sucursal

**Flujo Principal:**

1. Vendedor accede a `/vendedor/reservas`
2. Sistema obtiene id_usuario del vendedor autenticado
3. Sistema busca empleado:
   ```java
   Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(idVendedor).orElseThrow();
   Integer idSucursal = empleado.getSucursal().getIdSucursal();
   ```
4. Sistema obtiene reservas:
   ```java
   List<Reserva> reservas = reservaRepository.findBySucursalRetiro_IdSucursal(idSucursal);
   ```
5. Sistema muestra tabla con:
   - ID, Cliente (nombre), Vehículo
   - Fechas, Precio, Estado
   - Acciones:
     - Si PENDIENTE → Botones "Aceptar" y "Rechazar"
     - Si ACEPTADA → Botón "Cancelar"
     - Si CANCELADA → Sin acciones

**Flujos Alternativos:**

**FA-001: Vendedor sin sucursal asignada**

- 3a. No existe empleado para el usuario
- 3b. Sistema muestra: "Error: No está asignado a ninguna sucursal"
- 3c. Termina caso de uso

**FA-002: Sin reservas en sucursal**

- 4a. Query retorna lista vacía
- 4b. Sistema muestra: "No hay reservas para tu sucursal"

**Postcondiciones:**

- Vendedor ve SOLO reservas de su sucursal
- Vendedor puede gestionar reservas pendientes

---

### CU-008: Aceptar Reserva

**Actor:** Vendedor autenticado

**Precondiciones:**

- Reserva en estado PENDIENTE
- Reserva pertenece a sucursal del vendedor

**Flujo Principal:**

1. Vendedor hace clic en "Aceptar" (id_reserva=42)
2. Sistema verifica:
   - Reserva existe y está PENDIENTE
   - Sucursal de retiro coincide con sucursal del vendedor
3. Sistema actualiza:
   ```java
   reserva.setEstado(EstadoReserva.ACEPTADA);
   reservaRepository.save(reserva);
   ```
4. Trigger `after_reserva_update` cambia vehículo a ENTREGADO
5. Sistema recarga página
6. Sistema muestra: "Reserva aceptada, vehículo entregado al cliente"

**Flujos Alternativos:**

**FA-001: Reserva de otra sucursal**

- 2a. Sucursal de retiro != sucursal del vendedor
- 2b. Sistema muestra: "No autorizado"
- 2c. Termina caso de uso

**FA-002: Reserva ya aceptada/cancelada**

- 2a. Estado != PENDIENTE
- 2b. Sistema muestra: "La reserva ya fue procesada"

**Postcondiciones:**

- Reserva en estado ACEPTADA
- Vehículo en estado ENTREGADO
- Cliente puede usar el vehículo

---

### CU-009: Rechazar/Cancelar Reserva

**Actor:** Vendedor autenticado

**Precondiciones:**

- Reserva en estado PENDIENTE o ACEPTADA
- Reserva de la sucursal del vendedor

**Flujo Principal:**

1. Vendedor hace clic en "Rechazar" o "Cancelar"
2. Sistema muestra modal: "¿Motivo de cancelación?" (textarea)
3. Vendedor escribe motivo y confirma
4. Sistema verifica autorización
5. Sistema actualiza:
   ```java
   reserva.setEstado(EstadoReserva.CANCELADA);
   // reserva.setMotivoCancelacion(motivo); // Campo opcional
   reservaRepository.save(reserva);
   ```
6. Trigger cambia vehículo a DISPONIBLE
7. Sistema muestra: "Reserva cancelada"

**Postcondiciones:**

- Reserva cancelada
- Vehículo disponible nuevamente

---

## 4. Casos de Uso - ADMIN

### CU-010: CRUD Sucursales

#### CU-010.1: Listar Sucursales

**Flujo Principal:**

1. Admin accede a `/admin/sucursales`
2. Sistema ejecuta:
   ```java
   List<Sucursal> sucursales = sucursalRepository.findAll();
   ```
3. Sistema muestra tabla con:
   - ID, Nombre, Dirección, Estado
   - Acciones: Ver, Editar, Eliminar

#### CU-010.2: Crear Sucursal

**Flujo Principal:**

1. Admin hace clic en "Nueva Sucursal"
2. Sistema muestra formulario:
   - Nombre, Dirección, Imagen URL
3. Admin completa y envía
4. Sistema valida:
   ```java
   @NotBlank nombre, direccion
   ```
5. Sistema crea sucursal con estado=1 (activa)
6. Sistema muestra: "Sucursal creada exitosamente"

#### CU-010.3: Editar Sucursal

**Flujo Principal:**

1. Admin hace clic en "Editar" (id=1)
2. Sistema carga datos en formulario
3. Admin modifica campos
4. Admin envía
5. Sistema actualiza BD
6. Sistema muestra: "Sucursal actualizada"

#### CU-010.4: Eliminar Sucursal

**Flujo Principal:**

1. Admin hace clic en "Eliminar"
2. Sistema verifica que no tenga:
   - Vehículos asignados
   - Empleados asignados
   - Reservas activas
3. Si pasa validación:
   - Sistema hace soft-delete: `estado = 0`
   - O hard-delete si no hay referencias

**Flujo Alternativo:**

- 2a. Tiene referencias → Sistema muestra: "No se puede eliminar, tiene vehículos/empleados/reservas"

---

### CU-011: CRUD Vehículos

#### CU-011.1: Listar Vehículos

**Flujo Principal:**

1. Admin accede a `/admin/vehiculos`
2. Sistema muestra tabla con:
   - Patente, Marca/Modelo, Estado, Sucursal
   - Filtros: Por sucursal, por estado
   - Acciones: Ver, Editar, Eliminar

#### CU-011.2: Crear Vehículo

**Flujo Principal:**

1. Admin hace clic en "Nuevo Vehículo"
2. Sistema muestra formulario:
   - Patente, Marca, Modelo, Color
   - Cantidad puertas, Descripción
   - Precio diario, Imagen URL
   - Motor (dropdown), Tipo Vehículo (dropdown), Sucursal (dropdown)
3. Admin completa datos
4. Sistema valida:
   - Patente única
   - Precio > 0
   - Motor, TipoVehiculo, Sucursal existen
5. Sistema crea vehículo con estado=DISPONIBLE
6. Sistema muestra: "Vehículo creado"

**Validaciones:**

```java
@NotBlank patente, marca, modelo
@Positive precioDiario
@Pattern(regexp="[A-Z]{3}[0-9]{3}") patente
```

#### CU-011.3: Cambiar Vehículo de Sucursal

**Flujo Principal:**

1. Admin edita vehículo
2. Admin cambia Sucursal de "Buenos Aires" a "Córdoba"
3. Sistema valida que vehículo esté DISPONIBLE
4. Sistema actualiza `id_sucursal`
5. Sistema muestra: "Vehículo movido a nueva sucursal"

**Flujo Alternativo:**

- 3a. Vehículo RESERVADO/ENTREGADO → Error: "No se puede mover vehículo con reservas activas"

---

### CU-012: CRUD Empleados (Vendedores)

#### CU-012.1: Listar Empleados

**Flujo Principal:**

1. Admin accede a `/admin/empleados`
2. Sistema ejecuta:
   ```java
   List<Empleado> empleados = empleadoRepository.findAll();
   ```
3. Sistema muestra tabla:
   - Nombre del usuario, Email
   - Sucursal asignada, Estado
   - Acciones: Editar, Desactivar

#### CU-012.2: Crear Empleado

**Flujo Principal:**

1. Admin hace clic en "Nuevo Empleado"
2. Sistema muestra formulario:
   - Usuario (dropdown de usuarios con rol VENDEDOR sin empleado)
   - Sucursal (dropdown)
3. Admin selecciona:
   - Usuario: Juan Pérez (id=10)
   - Sucursal: Córdoba (id=2)
4. Sistema valida:
   - Usuario tiene rol VENDEDOR
   - Usuario no es empleado ya
   - Sucursal existe
5. Sistema crea empleado
6. Sistema muestra: "Empleado asignado a sucursal"

**Nota:** Para crear vendedor desde cero:

1. Admin primero crea usuario con rol VENDEDOR
2. Luego crea empleado vinculándolo a sucursal

#### CU-012.3: Cambiar Empleado de Sucursal

**Flujo Principal:**

1. Admin edita empleado
2. Admin cambia sucursal
3. Sistema actualiza `id_sucursal`
4. Sistema muestra: "Empleado reasignado"

---

### CU-013: Ver Todas las Reservas

**Actor:** Admin

**Flujo Principal:**

1. Admin accede a `/admin/reservas`
2. Sistema ejecuta:
   ```java
   List<Reserva> reservas = reservaRepository.findAllOrderByFechaDesc();
   ```
3. Sistema muestra tabla con TODAS las reservas:
   - Cliente, Vehículo, Sucursal retiro
   - Fechas, Estado, Precio
   - Filtros: Por estado, por sucursal, por fecha

**Diferencia con vendedor:**

- Admin ve TODAS las sucursales
- Vendedor solo ve SU sucursal

---

---

## 6. Diagramas de Flujo Clave

### 6.1 Flujo de Reserva Completo

```
[CLIENTE]
   │
   ├─> Buscar vehículos (fecha + sucursal)
   │      │
   │      ├─> Sistema: Query vehículos disponibles
   │      │      │
   │      │      └─> Excluye vehículos con reservas en rango
   │      │
   │      └─> Muestra lista de disponibles
   │
   ├─> Seleccionar vehículo
   │
   ├─> Completar formulario reserva
   │
   ├─> Confirmar reserva
   │      │
   │      ├─> Sistema: Validar disponibilidad
   │      │
   │      ├─> Sistema: Crear reserva PENDIENTE
   │      │
   │      └─> TRIGGER: Vehículo → RESERVADO
   │
   └─> Ver en "Mis Reservas" (estado: PENDIENTE)
          │
          │
[VENDEDOR de la sucursal]
   │
   ├─> Ve reserva pendiente
   │
   ├─> Decide: Aceptar o Rechazar
   │      │
   │      ├─ Aceptar
   │      │    │
   │      │    ├─> Reserva → ACEPTADA
   │      │    │
   │      │    └─> TRIGGER: Vehículo → ENTREGADO
   │      │
   │      └─ Rechazar
   │           │
   │           ├─> Reserva → CANCELADA
   │           │
   │           └─> TRIGGER: Vehículo → DISPONIBLE
   │
   │
[Al cumplirse fecha_fin]
   │
   └─> TRIGGER automático:
       │
       ├─> Vehículo → DISPONIBLE
       │
       └─> Vehículo movido a sucursal_devolucion
```

---

**Documento creado:** 03_casos_uso_detallados/CASOS_USO.md
**Fecha:** 2025-11-14
**Versión:** 1.0
