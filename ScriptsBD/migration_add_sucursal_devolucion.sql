-- Migración: Agregar campo id_sucursal_devolucion a la tabla reservas
-- Fecha: 2025-11-12
-- Descripción: Permite que las reservas tengan diferentes sucursales de retiro y devolución

-- Agregar columna id_sucursal_devolucion
ALTER TABLE reservas 
ADD COLUMN id_sucursal_devolucion INT NOT NULL DEFAULT 1;

-- Agregar clave foránea
ALTER TABLE reservas
ADD CONSTRAINT fk_reservas_sucursal_devolucion 
FOREIGN KEY (id_sucursal_devolucion) REFERENCES sucursales(id_sucursal);

-- Por defecto, las reservas existentes tendrán la misma sucursal de retiro y devolución
UPDATE reservas 
SET id_sucursal_devolucion = id_sucursal 
WHERE id_sucursal_devolucion = 1;

-- Comentario de verificación
-- Para verificar que la migración fue exitosa, ejecuta:
-- SELECT id_reserva, id_sucursal, id_sucursal_devolucion FROM reservas;
