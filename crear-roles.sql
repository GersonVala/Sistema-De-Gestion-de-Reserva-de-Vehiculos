-- Script para crear roles manualmente
INSERT INTO ROLES (NOMBRE_ROL, DESCRIPCION_ROL) VALUES ('ADMINISTRADOR', 'Administrador del sistema');
INSERT INTO ROLES (NOMBRE_ROL, DESCRIPCION_ROL) VALUES ('CLIENTE', 'Cliente del sistema');
INSERT INTO ROLES (NOMBRE_ROL, DESCRIPCION_ROL) VALUES ('VENDEDOR', 'Vendedor de sucursal');

-- Verificar
SELECT * FROM ROLES;
