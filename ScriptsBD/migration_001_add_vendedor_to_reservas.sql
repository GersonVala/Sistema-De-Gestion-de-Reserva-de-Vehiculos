-- =====================================================
-- Migración: Agregar vendedor a reservas
-- Fecha: 2025-11-12
-- Descripción: Permite asignar un vendedor a cada reserva
--              para gestionar el flujo de aprobación
-- =====================================================

-- Paso 1: Agregar columna id_vendedor (puede ser NULL inicialmente)
ALTER TABLE reservas 
ADD COLUMN id_vendedor INT NULL
COMMENT 'Vendedor que gestiona/aprueba la reserva';

-- Paso 2: Agregar clave foránea
ALTER TABLE reservas
ADD CONSTRAINT fk_reservas_vendedor 
FOREIGN KEY (id_vendedor) REFERENCES usuarios(id_usuario)
ON DELETE SET NULL  -- Si se elimina el vendedor, la reserva se mantiene pero sin vendedor asignado
ON UPDATE CASCADE;

-- Paso 3 (Opcional): Crear índice para mejorar consultas por vendedor
CREATE INDEX idx_reservas_vendedor ON reservas(id_vendedor);

-- Paso 4 (Opcional): Crear índice compuesto para consultas frecuentes
CREATE INDEX idx_reservas_vendedor_estado ON reservas(id_vendedor, estado);

-- =====================================================
-- Consultas de verificación
-- =====================================================

-- Verificar que la columna se agregó correctamente
-- DESCRIBE reservas;

-- Ver reservas con y sin vendedor asignado
-- SELECT id_reserva, estado, id_usuario, id_vendedor 
-- FROM reservas;

-- Contar reservas pendientes sin vendedor asignado
-- SELECT COUNT(*) as reservas_pendientes_sin_vendedor
-- FROM reservas 
-- WHERE estado = 'PENDIENTE' AND id_vendedor IS NULL;

-- =====================================================
-- Notas importantes
-- =====================================================
-- 1. Las reservas existentes tendrán id_vendedor = NULL
-- 2. Cuando un vendedor aprueba una reserva, se debe asignar su ID
-- 3. El campo es nullable porque las reservas PENDIENTES no tienen vendedor aún
-- 4. Solo reservas CONFIRMADAS, ALQUILADO o COMPLETADA deben tener vendedor
