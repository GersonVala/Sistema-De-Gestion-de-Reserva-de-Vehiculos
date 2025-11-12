# Implementación Punto 3: Servicio de Autorización

## 📋 Resumen
Se ha creado el servicio centralizado `AuthorizationService` que maneja toda la lógica de control de acceso basado en roles (RBAC) del sistema. Además, se agregó la relación vendedor-sucursal necesaria para el control de permisos.

---

## ✅ Archivos Creados

### 1. AuthorizationService.java
**Ubicación:** `src/main/java/.../service/AuthorizationService.java`

**Métodos principales:**

#### Verificación de Roles:
- `hasRole(HttpSession, RolEnum)` - Verifica si usuario tiene un rol específico
- `isCliente(HttpSession)` - Verifica si usuario es CLIENTE
- `isVendedor(HttpSession)` - Verifica si usuario es VENDEDOR
- `isAdministrador(HttpSession)` - Verifica si usuario es ADMINISTRADOR
- `hasAnyRole(HttpSession, RolEnum...)` - Verifica si tiene al menos uno de varios roles
- `getUserRoles(HttpSession)` - Obtiene todos los roles del usuario

#### Validación de Permisos:
- `canClienteAccessReserva(session, reservaId)` - Valida que cliente solo acceda a sus propias reservas
- `canVendedorManageReserva(session, reservaId)` - Valida que vendedor pueda gestionar reserva:
  - Si la reserva ya está asignada al vendedor
  - Si la reserva está PENDIENTE y pertenece a su sucursal
- `canAccessReserva(session, reservaId)` - Validación unificada:
  - ADMINISTRADOR → Acceso total
  - CLIENTE → Solo sus propias reservas
  - VENDEDOR → Reservas de su sucursal o asignadas

#### Utilidades:
- `isAuthenticated(HttpSession)` - Verifica si usuario está logueado
- `getVendedorSucursal(Integer vendedorId)` - Obtiene sucursal asignada al vendedor
- `getUserIdFromSession(HttpSession)` - Obtiene ID de usuario desde sesión (privado)
- `getRolIdByEnum(RolEnum)` - Obtiene ID de rol desde BD (privado)

**Características:**
- ✅ Logs detallados con emojis para debugging
- ✅ Manejo seguro de nulls
- ✅ Compatibilidad con múltiples atributos de sesión (userId/usuarioId)
- ✅ Lógica centralizada para reutilizar en controllers/services
- ✅ Validaciones de negocio encapsuladas

---

## 🔧 Archivos Modificados

### 1. UsuariosEntity.java
**Cambio:** Agregado campo `sucursal`

```java
// 🔗 Relación MUCHOS a UNO con sucursales (para vendedores asignados a sucursal)
@ManyToOne
@JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal")
private SucursalesEntity sucursal;
```

**Propósito:** 
- Asignar vendedores a sucursales específicas
- Necesario para validar que vendedores solo gestionen reservas de su sucursal
- Campo nullable porque solo VENDEDORES lo necesitan (CLIENTES y ADMINS pueden tener NULL)

---

### 2. UsuarioResponse.java
**Cambio:** Agregados campos de sucursal

```java
// Sucursal asignada (solo para vendedores)
private Integer idSucursal;
private String nombreSucursal;
```

**Propósito:** 
- Incluir información de sucursal en respuestas de API
- Útil para mostrar en interfaces de administración
- Permite identificar rápidamente qué sucursal maneja cada vendedor

---

### 3. UsuarioService.java
**Cambio:** Actualizado método `convertirAResponse()`

```java
// Información de sucursal (solo para vendedores)
Integer idSucursal = null;
String nombreSucursal = null;
if (usuario.getSucursal() != null) {
    idSucursal = usuario.getSucursal().getId_sucursal();
    nombreSucursal = "Sucursal #" + idSucursal + " - " + usuario.getSucursal().getTelefono_sucursal();
}

response.setIdSucursal(idSucursal);
response.setNombreSucursal(nombreSucursal);
```

**Propósito:**
- Incluir sucursal en conversión de entity a DTO
- Formato descriptivo usando ID y teléfono (ya que SucursalesEntity no tiene campo nombre)

---

### 4. migration_003_add_sucursal_to_usuarios.sql
**Script SQL de migración:**

```sql
-- Agregar columna id_sucursal a usuarios (nullable)
ALTER TABLE usuarios 
ADD COLUMN id_sucursal INTEGER;

-- Agregar foreign key constraint
ALTER TABLE usuarios 
ADD CONSTRAINT fk_usuario_sucursal 
FOREIGN KEY (id_sucursal) 
REFERENCES sucursales(id_sucursal);

-- Crear índice para performance
CREATE INDEX idx_usuario_sucursal ON usuarios(id_sucursal);
```

**Notas:**
- Campo nullable porque solo vendedores lo necesitan
- FK constraint mantiene integridad referencial
- Índice mejora performance de queries por sucursal

---

## 🎯 Casos de Uso

### Caso 1: Validar que cliente solo cancele sus propias reservas
```java
@Autowired
private AuthorizationService authService;

public void cancelarReserva(HttpSession session, Integer reservaId) {
    if (!authService.canClienteAccessReserva(session, reservaId)) {
        throw new ForbiddenException("No tienes permiso para cancelar esta reserva");
    }
    // Proceder con cancelación...
}
```

### Caso 2: Validar que vendedor apruebe reservas de su sucursal
```java
public void aprobarReserva(HttpSession session, Integer reservaId) {
    if (!authService.canVendedorManageReserva(session, reservaId)) {
        throw new ForbiddenException("No puedes gestionar reservas de otra sucursal");
    }
    // Proceder con aprobación...
}
```

### Caso 3: Proteger endpoint solo para administradores
```java
@GetMapping("/admin/reportes")
public String reportes(HttpSession session) {
    if (!authService.isAdministrador(session)) {
        throw new ForbiddenException("Solo administradores pueden acceder a reportes");
    }
    // Mostrar reportes...
}
```

### Caso 4: Validar acceso multi-rol
```java
public void verDetalleReserva(HttpSession session, Integer reservaId) {
    if (!authService.canAccessReserva(session, reservaId)) {
        throw new ForbiddenException("No tienes permiso para ver esta reserva");
    }
    // Mostrar detalle...
}
```

---

## 🔍 Lógica de Permisos Implementada

### CLIENTE
- ✅ Solo puede ver y cancelar sus propias reservas
- ✅ No puede ver reservas de otros clientes
- ✅ No puede aprobar/rechazar reservas
- ✅ Solo puede cancelar reservas en estado PENDIENTE

### VENDEDOR
- ✅ Solo puede gestionar reservas de su sucursal asignada
- ✅ Puede aprobar/rechazar reservas PENDIENTES de su sucursal
- ✅ Solo puede avanzar estados de reservas que tiene asignadas
- ✅ No puede gestionar reservas de otras sucursales
- ✅ Debe tener sucursal asignada en su usuario

### ADMINISTRADOR
- ✅ Acceso total sin restricciones
- ✅ Puede gestionar cualquier reserva
- ✅ Puede ver todos los usuarios y sucursales
- ✅ Bypasses todas las validaciones de permisos

---

## 🚀 Próximos Pasos

### Punto 4: VendedorController
Crear controlador con endpoints protegidos:
- GET `/vendedor/reservas/pendientes` - Listar reservas pendientes de su sucursal
- POST `/vendedor/reservas/{id}/aprobar` - Aprobar reserva
- POST `/vendedor/reservas/{id}/rechazar` - Rechazar reserva
- POST `/vendedor/reservas/{id}/iniciar` - Iniciar alquiler (pickup)
- POST `/vendedor/reservas/{id}/completar` - Completar alquiler (return)

Cada endpoint debe:
1. Usar `authService.isVendedor(session)` para validar rol
2. Usar `authService.canVendedorManageReserva(session, id)` para validar permisos
3. Llamar al método correspondiente de `ReservaService`
4. Retornar respuesta apropiada

### Punto 5: Vistas de Vendedor
Crear templates Thymeleaf:
- `vendedor-dashboard.html` - Dashboard con métricas de sucursal
- `vendedor-reservas-pendientes.html` - Lista de reservas pendientes
- `vendedor-reserva-detalle.html` - Detalle con botones aprobar/rechazar/iniciar/completar

### Punto 6: Role-Based Interceptor
Mejorar `AuthInterceptor` para:
- Proteger rutas `/vendedor/*` solo para VENDEDOR
- Proteger rutas `/admin/*` solo para ADMINISTRADOR
- Redirigir a error 403 si permisos insuficientes
- Integrar con `AuthorizationService`

---

## ⚠️ Consideraciones Importantes

1. **Sucursal Obligatoria para Vendedores:**
   - Al crear/registrar un vendedor, SE DEBE asignar una sucursal
   - Si un vendedor no tiene sucursal, `getVendedorSucursal()` retorna null
   - Los métodos de validación fallarán si vendedor no tiene sucursal

2. **Campo Sucursal en SucursalesEntity:**
   - Actualmente no tiene campo `nombre_sucursal`
   - Se usa formato "Sucursal #ID - Teléfono" para descripción
   - Considerar agregar campo nombre en el futuro

3. **Sesión Multi-Atributo:**
   - Service verifica tanto `SESSION_USER_ID` como `"usuarioId"`
   - Mantiene compatibilidad con código existente

4. **Base de Datos H2:**
   - ddl-auto=create-drop recrea schema en cada reinicio
   - El campo `id_sucursal` se agregará automáticamente
   - DataInitializer debe actualizarse para asignar sucursales a vendedores

5. **Testing:**
   - Probar todos los escenarios de permisos
   - Validar que clientes no accedan a reservas ajenas
   - Validar que vendedores solo gestionen su sucursal
   - Validar que admins tengan acceso total

---

## 📊 Estado del Proyecto

### ✅ COMPLETADO (Puntos 1-3)
- ✅ Punto 1: Campo vendedor en reservas
- ✅ Punto 2: Estados ALQUILADO y RECHAZADA con transiciones
- ✅ Punto 3: AuthorizationService con validación de permisos
- ✅ Documentación completa de flujos y estados

### 🔄 EN PROGRESO
- Ninguno actualmente

### ⏳ PENDIENTE (Puntos 4-6+)
- ⏳ Punto 4: VendedorController con endpoints
- ⏳ Punto 5: Vistas para vendedores
- ⏳ Punto 6: Role-based interceptor
- ⏳ Sincronizar estados de vehículos con reservas
- ⏳ Agregar campo motivo_rechazo a reservas
- ⏳ Actualizar DataInitializer para asignar sucursales
- ⏳ Testing integral del sistema de permisos

---

## 🎓 Aprendizajes Clave

1. **Centralización de Autorización:**
   - Un servicio centralizado facilita mantenimiento
   - Evita duplicación de lógica de permisos
   - Logs detallados ayudan en debugging

2. **Relaciones de Entidades:**
   - Vendedor-Sucursal es crucial para control de acceso
   - Relaciones nullable permiten flexibilidad (no todos los usuarios son vendedores)

3. **Validación en Capas:**
   - Servicio valida permisos
   - Controllers verifican roles
   - Interceptors protegen rutas
   - Triple defensa contra accesos no autorizados

4. **Sesión Compatible:**
   - Verificar múltiples atributos mantiene compatibilidad
   - Importante cuando código legacy existe
