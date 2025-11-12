-- Migración 003: Agregar campo id_sucursal a tabla usuarios
-- Para asignar vendedores a sucursales específicas

-- Agregar columna id_sucursal a usuarios (nullable porque no todos los usuarios son vendedores)
ALTER TABLE usuarios 
ADD COLUMN id_sucursal INTEGER;

-- Agregar foreign key constraint
ALTER TABLE usuarios 
ADD CONSTRAINT fk_usuario_sucursal 
FOREIGN KEY (id_sucursal) 
REFERENCES sucursales(id_sucursal);

-- Crear índice para mejorar performance en queries por sucursal
CREATE INDEX idx_usuario_sucursal ON usuarios(id_sucursal);

-- NOTAS:
-- 1. Este campo es necesario solo para usuarios con rol VENDEDOR
-- 2. Los CLIENTES y ADMINISTRADORES pueden tener este campo en NULL
-- 3. Al crear un vendedor, se debe asignar la sucursal donde trabajará
-- 4. Un vendedor puede estar asignado a una sola sucursal
-- 5. Una sucursal puede tener múltiples vendedores asignados
