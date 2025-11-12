# 🔄 FLUJO DE ESTADOS DE RESERVAS

## 📊 Diagrama de Estados

```
                    ┌─────────────┐
                    │  PENDIENTE  │ ← Cliente crea reserva
                    └──────┬──────┘
                           │
                ┌──────────┼──────────┐
                │          │          │
                │          │          │
         [Cliente]    [Vendedor]  [Vendedor]
         cancela      aprueba     rechaza
                │          │          │
                ↓          ↓          ↓
         ┌──────────┐ ┌──────────┐ ┌──────────┐
         │CANCELADA │ │CONFIRMADA│ │RECHAZADA │
         └──────────┘ └────┬─────┘ └──────────┘
                           │
                      [Vendedor]
                   registra retiro
                           │
                           ↓
                    ┌──────────┐
                    │ALQUILADO │ ← Cliente tiene el auto
                    └────┬─────┘
                         │
                    [Vendedor]
                 registra devolución
                         │
                         ↓
                   ┌──────────┐
                   │COMPLETADA│
                   └──────────┘
```

## 🎯 Descripción de Estados

### 1. **PENDIENTE** 
- **Descripción**: Reserva solicitada por el cliente
- **Quién lo establece**: Sistema (al crear reserva)
- **Vendedor asignado**: NO (null)
- **Siguiente estado posible**: CONFIRMADA, RECHAZADA, CANCELADA

### 2. **CONFIRMADA**
- **Descripción**: Vendedor aprobó la solicitud
- **Quién lo establece**: Vendedor
- **Vendedor asignado**: SÍ
- **Siguiente estado posible**: ALQUILADO, CANCELADA (con penalización)

### 3. **RECHAZADA**
- **Descripción**: Vendedor rechazó la solicitud
- **Quién lo establece**: Vendedor
- **Vendedor asignado**: SÍ
- **Siguiente estado posible**: Ninguno (estado final)

### 4. **ALQUILADO**
- **Descripción**: Cliente retiró el vehículo
- **Quién lo establece**: Vendedor
- **Vendedor asignado**: SÍ
- **Siguiente estado posible**: COMPLETADA

### 5. **COMPLETADA**
- **Descripción**: Cliente devolvió el vehículo
- **Quién lo establece**: Vendedor
- **Vendedor asignado**: SÍ
- **Siguiente estado posible**: Ninguno (estado final)

### 6. **CANCELADA**
- **Descripción**: Reserva cancelada
- **Quién lo establece**: Cliente (si está PENDIENTE) o Sistema
- **Vendedor asignado**: Depende (puede tener o no)
- **Siguiente estado posible**: Ninguno (estado final)

---

## 🔒 Restricciones de Transición

### **Desde PENDIENTE puede ir a:**
- ✅ CONFIRMADA (por vendedor)
- ✅ RECHAZADA (por vendedor)
- ✅ CANCELADA (por cliente)

### **Desde CONFIRMADA puede ir a:**
- ✅ ALQUILADO (por vendedor)
- ⚠️ CANCELADA (por cliente - puede aplicar penalización)

### **Desde ALQUILADO puede ir a:**
- ✅ COMPLETADA (por vendedor)

### **Estados Finales (no cambian):**
- 🔴 RECHAZADA
- 🔴 CANCELADA
- 🔴 COMPLETADA

---

## 👥 Permisos por Actor

### **CLIENTE**
| Acción | Estado Inicial | Estado Final | Método |
|--------|---------------|--------------|--------|
| Crear reserva | - | PENDIENTE | `crear()` |
| Cancelar reserva | PENDIENTE | CANCELADA | `cancelarPorCliente()` |
| Ver mis reservas | - | - | `obtenerPorUsuario()` |

### **VENDEDOR**
| Acción | Estado Inicial | Estado Final | Método |
|--------|---------------|--------------|--------|
| Aprobar solicitud | PENDIENTE | CONFIRMADA | `aprobarReserva()` |
| Rechazar solicitud | PENDIENTE | RECHAZADA | `rechazarReserva()` |
| Registrar retiro | CONFIRMADA | ALQUILADO | `iniciarAlquiler()` |
| Registrar devolución | ALQUILADO | COMPLETADA | `completarAlquiler()` |
| Ver reservas de su sucursal | - | - | `obtenerPendientesPorSucursal()` |

### **ADMINISTRADOR**
- Todos los permisos de VENDEDOR
- Puede ver TODAS las reservas del sistema
- Puede forzar cambios de estado si es necesario

---

## 🔄 Sincronización con Vehículos

| Estado Reserva | Estado Vehículo | Acción |
|----------------|-----------------|--------|
| PENDIENTE | DISPONIBLE | Sin cambio |
| CONFIRMADA | RESERVADO | Al aprobar |
| RECHAZADA | DISPONIBLE | Mantener |
| ALQUILADO | ALQUILADO | Al iniciar alquiler |
| COMPLETADA | DISPONIBLE | Al completar |
| CANCELADA | DISPONIBLE | Liberar si estaba reservado |

---

## 📋 Validaciones Implementadas

### **En `cancelarPorCliente()`**
```java
✅ Verifica que la reserva pertenezca al cliente
✅ Solo permite cancelar si está en PENDIENTE
❌ No permite cancelar si ya está CONFIRMADA o posterior
```

### **En `aprobarReserva()`**
```java
✅ Verifica que esté en PENDIENTE
✅ Asigna el vendedor a la reserva
✅ Cambia estado a CONFIRMADA
⚠️ TODO: Cambiar vehículo a RESERVADO
```

### **En `rechazarReserva()`**
```java
✅ Verifica que esté en PENDIENTE
✅ Asigna el vendedor a la reserva
✅ Cambia estado a RECHAZADA
⚠️ TODO: Guardar motivo del rechazo
```

### **En `iniciarAlquiler()`**
```java
✅ Verifica que esté en CONFIRMADA
✅ Verifica que el vendedor sea el asignado
✅ Cambia estado a ALQUILADO
⚠️ TODO: Cambiar vehículo a ALQUILADO
```

### **En `completarAlquiler()`**
```java
✅ Verifica que esté en ALQUILADO
✅ Verifica que el vendedor sea el asignado
✅ Cambia estado a COMPLETADA
⚠️ TODO: Cambiar vehículo a DISPONIBLE
```

---

## 🎨 Códigos de Color Sugeridos (UI)

```css
PENDIENTE   → 🟡 Amarillo (#FFC107)  - Esperando acción
CONFIRMADA  → 🔵 Azul (#2196F3)      - Aprobada
RECHAZADA   → 🔴 Rojo (#F44336)      - Rechazada
ALQUILADO   → 🟢 Verde (#4CAF50)     - En curso
COMPLETADA  → ⚫ Gris (#9E9E9E)      - Finalizada
CANCELADA   → 🔴 Rojo claro (#FF5722) - Cancelada
```

---

## 📊 Métricas Útiles

### **Conversión de Reservas**
```
Tasa de Aprobación = CONFIRMADAS / PENDIENTES
Tasa de Rechazo = RECHAZADAS / PENDIENTES
Tasa de Cancelación = CANCELADAS / TOTAL
Tasa de Completación = COMPLETADAS / CONFIRMADAS
```

### **KPIs de Vendedores**
```
Reservas Aprobadas por Vendedor
Tiempo promedio de aprobación
Reservas activas (ALQUILADO)
Reservas completadas con éxito
```
