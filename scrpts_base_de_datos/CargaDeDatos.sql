-- ...existing code...

/* ROLES */
INSERT INTO roles (id_rol, nombre, descripcion) VALUES
(1, 'ADMINISTRADOR', 'Gestiona sucursales y autos'),
(2, 'VENDEDOR', 'Gestiona reservas'),
(3, 'CLIENTE', 'Puede reservar');



/* TIPOS DE VEHICULO */
INSERT INTO tipo_vehiculo (id_tipo_vehiculo, tipo, caracteristicas) VALUES
(1,'Sedan','4 puertas, cómodo para ciudad'),
(2,'SUV','Más espacio, ideal familia'),
(3,'Hatchback','Compacto y económico'),
(4,'Camioneta','Carga y tracción'),
(5,'Coupé','2 puertas, deportivo'),
(6,'Convertible','Techo retráctil');

/* MOTORES */
INSERT INTO motores (id_motor, cilindrada, caballos_de_fuerza, tipo_combustible, tipo_motor) VALUES
(1,1.60,110,'NAFTA','MANUAL'),
(2,2.00,150,'NAFTA','AUTOMATICO'),
(3,2.50,200,'NAFTA','AUTOMATICO'),
(4,1.20,90,'NAFTA','MANUAL'),
(5,3.00,240,'DIESEL','AUTOMATICO'),
(6,0.00,0,'GNC','MANUAL');

/* SUCURSALES (3) */
INSERT INTO sucursales (id_sucursal, nombre, direccion, estado) VALUES
(1,'Sucursal Centro','Av. Principal 123',1),
(2,'Sucursal Norte','Calle Norte 45',1),
(3,'Sucursal Sur','Boulevard Sur 789',1);

/* USUARIOS (10: Gerson = ADMIN; varios VENDEDORES y CLIENTES)
   contraseña = Nombre + '123' según pedido */
INSERT INTO usuarios (id_usuario, nombre, apellido, email, contra, dni, telefono, direccion, estado, id_rol) VALUES
(1,'Gerson','Gonzalez','gerson@example.com','Gerson123','DNI0001','111-0001','Av. Admin 1',1,1), -- ADMIN
(2,'Maria','Perez','maria@example.com','Maria123','DNI0002','111-0002','Calle 2',1,3),
(3,'Juan','Lopez','juan@example.com','Juan123','DNI0003','111-0003','Calle 3',1,2), -- VENDEDOR
(4,'Lucia','Martinez','lucia@example.com','Lucia123','DNI0004','111-0004','Calle 4',1,2), -- VENDEDOR
(5,'Pedro','Ramirez','pedro@example.com','Pedro123','DNI0005','111-0005','Calle 5',1,3),
(6,'Ana','Torres','ana@example.com','Ana123','DNI0006','111-0006','Calle 6',1,3),
(7,'Carlos','Diaz','carlos@example.com','Carlos123','DNI0007','111-0007','Calle 7',1,3),
(8,'Sofia','Gomez','sofia@example.com','Sofia123','DNI0008','111-0008','Calle 8',1,3),
(9,'Diego','Vega','diego@example.com','Diego123','DNI0009','111-0009','Calle 9',1,2), -- VENDEDOR
(10,'Marta','Ortiz','marta@example.com','Marta123','DNI0010','111-0010','Calle 10',1,3);

/* EMPLEADOS: asigno Gerson (admin) + vendedores por sucursal */
INSERT INTO empleados (id_empleado, id_usuario, id_sucursal, estado) VALUES
(1,1,1,1),  -- Gerson en Sucursal Centro (admin)
(2,3,1,1),  -- Juan (vendedor) en Centro
(3,4,2,1),  -- Lucia (vendedora) en Norte
(4,9,3,1);  -- Diego (vendedor) en Sur

/* VEHICULOS (20) - estados iniciales coherentes con reservas abajo */
INSERT INTO vehiculos (id_vehiculo, patente, modelo, marca, color, estado, cant_puertas, descripcion, precio_diario, id_motor, id_tipo_vehiculo, id_sucursal) VALUES
(1,'ABC123','Corolla','Toyota','Blanco','DISPONIBLE',4,'Sedan económico',45.00,1,1,1),
(2,'DEF234','Civic','Honda','Negro','RESERVADO',4,'Confort y eficiencia',50.00,2,1,1),  -- reservado por una petición pendiente
(3,'GHI345','Cruze','Chevrolet','Gris','DISPONIBLE',4,'Sedan cómodo',42.00,1,1,2),
(4,'JKL456','RAV4','Toyota','Azul','DISPONIBLE',5,'SUV mediana',75.00,3,2,1),
(5,'MNO567','Captiva','Chevrolet','Rojo','DISPONIBLE',5,'SUV familiar',70.00,3,2,2),
(6,'PQR678','Golf','Volkswagen','Blanco','DISPONIBLE',3,'Hatchback práctico',40.00,4,3,3),
(7,'STU789','Fiesta','Ford','Amarillo','DISPONIBLE',3,'Hatch pequeño',35.00,4,3,3),
(8,'VWX890','Hilux','Toyota','Plata','DISPONIBLE',4,'Camioneta robusta',95.00,5,4,2),
(9,'YZA901','Amarok','Volkswagen','Negro','DESCOMPUESTO',4,'Camioneta potente',98.00,5,4,1),
(10,'BCD012','Mustang','Ford','Rojo','ENTREGADO',2,'Coupé deportivo',150.00,3,5,1), -- entregado a cliente en reserva aceptada
(11,'EFG123','MX-5','Mazda','Rojo','DISPONIBLE',2,'Convertible deportivo',130.00,2,6,3),
(12,'HIJ234','Leaf','Nissan','Blanco','ENTREGADO',4,'Eléctrico compacto',80.00,6,1,2), -- entregado (reserva aceptada)
(13,'KLM345','Corolla H','Toyota','Gris','DISPONIBLE',4,'Híbrido',65.00,2,1,1),
(14,'NOP456','T-Cross','Volkswagen','Azul','DISPONIBLE',5,'SUV compacto',60.00,2,2,3),
(15,'QRS567','Sandero','Renault','Blanco','DISPONIBLE',4,'Económico familiar',30.00,4,1,3),
(16,'TUV678','Kicks','Nissan','Negro','DISPONIBLE',5,'SUV compacto',55.00,2,2,2),
(17,'WXY789','F-150','Ford','Plata','DESCOMPUESTO',4,'Camioneta full-size',120.00,5,4,1),
(18,'ZAB890','C4','Citroen','Gris','DISPONIBLE',4,'Sedan compacto',38.00,1,1,2),
(19,'CDE901','Onix','Chevrolet','Negro','DISPONIBLE',4,'Compacto económico',33.00,4,3,3),
(20,'FGH012','A3','Audi','Blanco','DISPONIBLE',4,'Premium compacto',110.00,3,1,1);

/* RESERVAS (ejemplos)
   - Reserva 1: petición pendiente de Maria (id_usuario=2) por vehiculo 2 -> vehiculo ya marcado RESERVADO arriba.
   - Reserva 2: aceptada y vehiculo entregado (ejemplo para cliente Pedro id=5, vehiculo 12)
   - Reserva 3: petición que fue cancelada
*/
INSERT INTO reservas (id_reserva, fecha_inicio, fecha_fin, precio, metodo_pago, estado, id_usuario, id_sucursal_retiro, id_sucursal_devolucion, id_vehiculo) VALUES
(1,'2025-12-01','2025-12-05',50.00*4,'TARJETA','PENDIENTE',2,1,2,2),
(2,'2025-11-20','2025-11-22',80.00*2,'TRANSFERENCIA','ACEPTADA',5,2,2,12),
(3,'2025-11-25','2025-11-27',70.00*2,'EFECTIVO','CANCELADA',6,3,3,5);

-- (Opcional) ajustar estados coherentes si no existe trigger
UPDATE vehiculos SET estado = 'RESERVADO' WHERE id_vehiculo = 2;
UPDATE vehiculos SET estado = 'ENTREGADO' WHERE id_vehiculo = 12;

-- FIN POBLADO