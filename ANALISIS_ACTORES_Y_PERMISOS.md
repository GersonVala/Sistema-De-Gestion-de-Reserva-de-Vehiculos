# 📊 ANÁLISIS DE ACTORES Y PERMISOS DEL SISTEMA

## 🎭 ACTORES DEL SISTEMA

### 1. **CLIENTE** (Usuario registrado)
- **Objetivo**: Alquilar vehículos
- **Acceso**: Registro público + Login

### 2. **VENDEDOR** (Empleado de sucursal)
- **Objetivo**: Gestionar reservas de su sucursal
- **Acceso**: Creado por Administrador

### 3. **ADMINISTRADOR** (Super usuario)
- **Objetivo**: Gestionar todo el sistema
- **Acceso**: Root del sistema

---

## 🔍 ESTADO ACTUAL DEL SISTEMA

### ✅ **LO QUE YA EXISTE EN EL BACKEND:**

#### **Entidades:**
- ✅ `RolEnum`: ADMINISTRADOR, CLIENTE, VENDEDOR
- ✅ `EstadoReservaEnum`: PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
- ✅ `Usuario_roles`: Tabla intermedia para asignar múltiples roles
- ✅ `Sucursales`: Cada sucursal tiene un `id_vendedor` asociado

#### **Servicios de Reserva:**
- ✅ `crear()`: Crea reserva con estado PENDIENTE
- ✅ `confirmar()`: Cambia estado a CONFIRMADA
- ✅ `cancelar()`: Cambia estado a CANCELADA
- ✅ `completar()`: Cambia estado a COMPLETADA
- ✅ `obtenerPorEstado()`: Filtra reservas por estado
- ✅ `obtenerPorUsuario()`: Obtiene reservas de un cliente específico

---

## ⚠️ PROBLEMAS IDENTIFICADOS

### 1. **FLUJO DE RESERVAS INCORRECTO**
**❌ Problema actual:**
- Cliente crea reserva → Estado: PENDIENTE ✅
- **PERO**: La reserva NO queda en espera de aprobación del vendedor
- El cliente puede "confirmar" directamente su propia reserva ❌

**✅ Flujo correcto debería ser:**
```
1. Cliente solicita reserva → Estado: PENDIENTE
2. Vendedor revisa → Aprueba o Rechaza
3. Si aprueba → Estado: CONFIRMADA
4. Cliente retira vehículo → Estado: ALQUILADO (nuevo estado necesario)
5. Cliente devuelve vehículo → Estado: COMPLETADA
```

### 2. **FALTA CONTROL DE ROLES EN BACKEND**
**❌ Problema actual:**
- NO hay validación de roles en los controladores
- Un CLIENTE podría llamar endpoints de VENDEDOR
- NO hay @PreAuthorize ni filtros por rol

### 3. **FALTA ASIGNACIÓN DE VENDEDOR A RESERVA**
**❌ Problema actual:**
- Las reservas NO tienen campo `id_vendedor`
- NO se sabe qué vendedor gestiona cada reserva
- NO se puede filtrar reservas por vendedor

### 4. **ESTADOS DE VEHÍCULOS NO SE ACTUALIZAN**
**❌ Problema actual:**
- Al confirmar reserva, el vehículo NO cambia a RESERVADO
- Al iniciar alquiler, el vehículo NO cambia a ALQUILADO
- Al devolver, el vehículo NO vuelve a DISPONIBLE

---

## 📋 ACCIONES POR ACTOR (DISEÑO CORRECTO)

### 👤 **CLIENTE (ROL: CLIENTE)**

#### **Gestión de Reservas:**
| Acción | Endpoint | Estado Inicial | Estado Final | Implementado |
|--------|----------|----------------|--------------|--------------|
| Solicitar reserva | POST `/reservas/crear` | - | PENDIENTE | ✅ Sí |
| Ver mis reservas | GET `/dashboard/mis-reservas` | - | - | ✅ Sí |
| Cancelar mi reserva | POST `/reservas/{id}/cancelar` | PENDIENTE | CANCELADA | ❌ No validado |
| Ver detalle de reserva | GET `/reservas/{id}` | - | - | ⚠️ Sin restricción |

#### **Restricciones:**
- ❌ NO puede confirmar reservas (solo vendedor)
- ❌ NO puede ver reservas de otros clientes
- ❌ NO puede modificar estado a CONFIRMADA
- ❌ NO puede eliminar reservas (solo cancelar)
- ❌ NO puede ver panel de vendedor/admin

---

### 👨‍💼 **VENDEDOR (ROL: VENDEDOR)**

#### **Gestión de Reservas (de su sucursal):**
| Acción | Endpoint | Estado Inicial | Estado Final | Implementado |
|--------|----------|----------------|--------------|--------------|
| Ver reservas pendientes | GET `/vendedor/reservas/pendientes` | PENDIENTE | - | ❌ NO |
| Aprobar reserva | POST `/vendedor/reservas/{id}/aprobar` | PENDIENTE | CONFIRMADA | ❌ NO |
| Rechazar reserva | POST `/vendedor/reservas/{id}/rechazar` | PENDIENTE | CANCELADA | ❌ NO |
| Registrar retiro vehículo | POST `/vendedor/reservas/{id}/iniciar` | CONFIRMADA | ALQUILADO | ❌ NO |
| Registrar devolución | POST `/vendedor/reservas/{id}/completar` | ALQUILADO | COMPLETADA | ❌ NO |
| Ver todas sus reservas | GET `/vendedor/reservas` | - | - | ❌ NO |

#### **Gestión de Vehículos (de su sucursal):**
| Acción | Endpoint | Implementado |
|--------|----------|--------------|
| Ver vehículos de su sucursal | GET `/vendedor/vehiculos` | ❌ NO |
| Marcar vehículo descompuesto | POST `/vendedor/vehiculos/{id}/descomponer` | ❌ NO |
| Reparar vehículo | POST `/vendedor/vehiculos/{id}/reparar` | ❌ NO |

#### **Restricciones:**
- ❌ SOLO puede gestionar reservas de SU sucursal
- ❌ NO puede ver/modificar otras sucursales
- ❌ NO puede crear/eliminar usuarios
- ❌ NO puede modificar precios
- ❌ NO puede acceder a panel de administrador

---

### 👨‍💻 **ADMINISTRADOR (ROL: ADMINISTRADOR)**

#### **Gestión Total del Sistema:**
| Categoría | Acciones Permitidas | Implementado |
|-----------|---------------------|--------------|
| **Usuarios** | CRUD completo de todos los usuarios | ✅ Parcial (API) |
| **Vendedores** | Crear vendedores y asignarlos a sucursales | ❌ NO |
| **Sucursales** | CRUD completo de sucursales | ✅ Parcial (API) |
| **Vehículos** | CRUD completo de todos los vehículos | ✅ Parcial (API) |
| **Reservas** | Ver TODAS las reservas del sistema | ✅ Sí |
| **Reservas** | Cancelar cualquier reserva | ⚠️ Sin restricción |
| **Reportes** | Dashboard con estadísticas completas | ✅ Sí |
| **Roles** | Asignar/modificar roles de usuarios | ❌ NO |

#### **Sin Restricciones:**
- ✅ Acceso total a todas las funcionalidades
- ✅ Puede actuar como vendedor en cualquier sucursal
- ✅ Puede gestionar todo el sistema

---

## 🔧 CAMBIOS NECESARIOS EN EL BACKEND

### 1. **Agregar Campo `id_vendedor` a Reservas**

**Base de Datos:**
```sql
ALTER TABLE reservas 
ADD COLUMN id_vendedor INT NULL;

ALTER TABLE reservas
ADD CONSTRAINT fk_reservas_vendedor 
FOREIGN KEY (id_vendedor) REFERENCES usuarios(id_usuario);
```

**Entidad:**
```java
@ManyToOne
@JoinColumn(name = "id_vendedor", referencedColumnName = "id_usuario")
private UsuariosEntity vendedor; // Vendedor que gestionó la reserva
```

### 2. **Agregar Estado ALQUILADO a Reservas**

```java
public enum EstadoReservaEnum {
    PENDIENTE("Pendiente"),        // Cliente solicitó, esperando vendedor
    CONFIRMADA("Confirmada"),      // Vendedor aprobó, esperando retiro
    ALQUILADO("Alquilado"),        // ⭐ NUEVO - Cliente retiró el vehículo
    COMPLETADA("Completada"),      // Cliente devolvió el vehículo
    CANCELADA("Cancelada"),        // Rechazada por vendedor o cancelada por cliente
    RECHAZADA("Rechazada");        // ⭐ NUEVO - Vendedor rechazó explícitamente
}
```

### 3. **Implementar Control de Roles**

**Crear servicio de autorización:**
```java
@Service
public class AuthorizationService {
    
    public boolean isCliente(HttpSession session) {
        return hasRole(session, RolEnum.CLIENTE);
    }
    
    public boolean isVendedor(HttpSession session) {
        return hasRole(session, RolEnum.VENDEDOR);
    }
    
    public boolean isAdministrador(HttpSession session) {
        return hasRole(session, RolEnum.ADMINISTRADOR);
    }
    
    public boolean canAccessReserva(HttpSession session, Integer reservaId) {
        // Lógica para validar acceso a reserva específica
    }
}
```

### 4. **Crear Controlador para Vendedores**

```java
@Controller
@RequestMapping("/vendedor")
@RequiredArgsConstructor
public class VendedorController {
    
    private final ReservaService reservaService;
    private final AuthorizationService authService;
    
    @GetMapping("/reservas/pendientes")
    public String verReservasPendientes(Model model, HttpSession session) {
        // Solo vendedores de la sucursal pueden ver
        if (!authService.isVendedor(session)) {
            return "redirect:/login";
        }
        
        Integer vendedorId = (Integer) session.getAttribute("usuarioId");
        List<ReservaResponse> reservas = reservaService.obtenerPendientesPorVendedor(vendedorId);
        model.addAttribute("reservas", reservas);
        return "vendedor/reservas-pendientes";
    }
    
    @PostMapping("/reservas/{id}/aprobar")
    public String aprobarReserva(@PathVariable Integer id, 
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!authService.isVendedor(session)) {
            return "redirect:/login";
        }
        
        Integer vendedorId = (Integer) session.getAttribute("usuarioId");
        reservaService.aprobarReserva(id, vendedorId);
        
        redirectAttributes.addFlashAttribute("mensaje", "Reserva aprobada exitosamente");
        return "redirect:/vendedor/reservas/pendientes";
    }
}
```

### 5. **Actualizar Flujo de Reservas en `ReservaService`**

```java
@Transactional
public ReservaResponse aprobarReserva(Integer reservaId, Integer vendedorId) {
    ReservasEntity reserva = reservaRepository.findById(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
    
    // Validar que esté PENDIENTE
    if (reserva.getEstado() != EstadoReservaEnum.PENDIENTE) {
        throw new IllegalStateException("Solo se pueden aprobar reservas pendientes");
    }
    
    // Validar que el vendedor sea de la sucursal correcta
    UsuariosEntity vendedor = usuarioRepository.findById(vendedorId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Vendedor no encontrado"));
    
    // Cambiar estado
    reserva.setEstado(EstadoReservaEnum.CONFIRMADA);
    reserva.setVendedor(vendedor);
    
    // Actualizar estado del vehículo a RESERVADO
    // (requiere agregar id_vehiculo a reservas o obtenerlo de detalle_reserva)
    
    ReservasEntity reservaActualizada = reservaRepository.save(reserva);
    return convertirAResponse(reservaActualizada);
}

@Transactional
public ReservaResponse iniciarAlquiler(Integer reservaId, Integer vendedorId) {
    ReservasEntity reserva = reservaRepository.findById(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
    
    if (reserva.getEstado() != EstadoReservaEnum.CONFIRMADA) {
        throw new IllegalStateException("Solo se pueden iniciar reservas confirmadas");
    }
    
    reserva.setEstado(EstadoReservaEnum.ALQUILADO);
    
    // Actualizar vehículo a ALQUILADO
    
    return convertirAResponse(reservaRepository.save(reserva));
}
```

---

## 🎯 RESUMEN DE IMPLEMENTACIONES NECESARIAS

### **CRÍTICO (Funcionalidad básica):**
1. ✅ Agregar `id_vendedor` a tabla `reservas`
2. ✅ Agregar estados ALQUILADO y RECHAZADA
3. ✅ Implementar servicio de autorización por roles
4. ✅ Crear métodos `aprobarReserva()` y `rechazarReserva()` en ReservaService
5. ✅ Validar que cliente SOLO pueda cancelar reservas PENDIENTES

### **IMPORTANTE (Gestión de vendedores):**
6. ✅ Crear VendedorController con vistas específicas
7. ✅ Implementar filtro de reservas por sucursal del vendedor
8. ✅ Crear método `iniciarAlquiler()` (cuando cliente retira)
9. ✅ Sincronizar estados de vehículos con estados de reservas

### **OPCIONAL (Mejoras):**
10. ⭐ Dashboard diferenciado por rol
11. ⭐ Notificaciones a clientes cuando vendedor aprueba/rechaza
12. ⭐ Historial de cambios de estado en reservas
13. ⭐ Reportes por vendedor/sucursal

---

## 📌 PRÓXIMOS PASOS SUGERIDOS

**Orden de implementación:**
1. Modificar BD y entidad Reservas (agregar vendedor)
2. Actualizar EstadoReservaEnum
3. Crear AuthorizationService
4. Modificar ReservaService con nuevos métodos
5. Crear VendedorController
6. Crear vistas para vendedores
7. Implementar interceptor de roles
8. Testear flujo completo

