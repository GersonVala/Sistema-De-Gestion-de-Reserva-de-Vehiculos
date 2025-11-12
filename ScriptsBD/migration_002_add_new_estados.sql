-- =====================================================
-- Migración: Agregar nuevos estados a reservas
-- Fecha: 2025-11-12
-- Descripción: Agregar estados ALQUILADO y RECHAZADA
--              para un flujo completo de gestión
-- =====================================================

-- Paso 1: Modificar el ENUM de estado (MySQL)
-- NOTA: En H2 con JPA esto se maneja automáticamente
ALTER TABLE reservas 
MODIFY COLUMN estado ENUM(
    'PENDIENTE',    -- Cliente solicitó, esperando vendedor
    'CONFIRMADA',   -- Vendedor aprobó, esperando retiro
    'RECHAZADA',    -- Vendedor rechazó la solicitud
    'ALQUILADO',    -- Cliente retiró el vehículo
    'COMPLETADA',   -- Cliente devolvió el vehículo
    'CANCELADA'     -- Cliente canceló antes de confirmar
) NOT NULL;

-- =====================================================
-- Flujo de Estados
-- =====================================================
/*
FLUJO NORMAL:
1. PENDIENTE    → Cliente crea reserva, esperando aprobación
2. CONFIRMADA   → Vendedor aprueba, esperando retiro del cliente
3. ALQUILADO    → Cliente retiró vehículo, está en uso
4. COMPLETADA   → Cliente devolvió vehículo

FLUJOS ALTERNATIVOS:
- PENDIENTE → CANCELADA   (Cliente cancela antes de aprobación)
- PENDIENTE → RECHAZADA   (Vendedor rechaza la solicitud)
- CONFIRMADA → CANCELADA  (Cliente cancela después de confirmada - puede aplicar penalización)

RESTRICCIONES:
- Solo PENDIENTE puede pasar a CONFIRMADA o RECHAZADA
- Solo CONFIRMADA puede pasar a ALQUILADO
- Solo ALQUILADO puede pasar a COMPLETADA
- Cliente solo puede cancelar si está en PENDIENTE
*/

-- =====================================================
-- Consultas de verificación
-- =====================================================

-- Ver distribución de reservas por estado
-- SELECT estado, COUNT(*) as cantidad
-- FROM reservas
-- GROUP BY estado;

-- Ver reservas en cada etapa del flujo
-- SELECT id_reserva, estado, fecha_inicio, fecha_fin, id_vendedor
-- FROM reservas
-- ORDER BY estado, fecha_inicio;

-- Reservas que están actualmente alquiladas
-- SELECT r.id_reserva, r.fecha_inicio, r.fecha_fin, 
--        u.nombre_usuario, u.apellido_usuario
-- FROM reservas r
-- JOIN usuarios u ON r.id_usuario = u.id_usuario
-- WHERE r.estado = 'ALQUILADO';

-- Reservas pendientes de aprobación (sin vendedor asignado)
-- SELECT r.id_reserva, r.fecha_inicio, r.fecha_fin,
--        u.nombre_usuario, u.email_usuario,
--        s.id_sucursal
-- FROM reservas r
-- JOIN usuarios u ON r.id_usuario = u.id_usuario
-- JOIN sucursales s ON r.id_sucursal = s.id_sucursal
-- WHERE r.estado = 'PENDIENTE' AND r.id_vendedor IS NULL;

-- =====================================================
-- Validaciones de negocio sugeridas
-- =====================================================

-- No permitir dos reservas ALQUILADO del mismo vehículo simultáneamente
-- (requiere relación con tabla detalle_reserva)

-- Alertar reservas ALQUILADO que exceden la fecha_fin
-- SELECT r.id_reserva, r.fecha_fin, 
--        DATEDIFF(CURRENT_DATE, r.fecha_fin) as dias_retraso
-- FROM reservas r
-- WHERE r.estado = 'ALQUILADO' 
--   AND r.fecha_fin < CURRENT_DATE;
