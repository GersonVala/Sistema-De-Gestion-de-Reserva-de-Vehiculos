USE reserva_vehiculos;

ALTER TABLE usuarios 
ADD FOREIGN KEY (id_direccion) REFERENCES direcciones(id_direccion);

ALTER TABLE usuario_roles
ADD FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario);

ALTER TABLE usuario_roles
ADD FOREIGN KEY (id_rol) REFERENCES roles(id_rol);

ALTER TABLE vehiculos
ADD FOREIGN KEY (id_motor) REFERENCES motores(id_motor);

ALTER TABLE vehiculos
ADD FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipo_vehiculo(id_tipo_vehiculo);

ALTER TABLE detalle_reserva
ADD FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo);

ALTER TABLE detalle_reserva
ADD FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva);

ALTER TABLE reservas
ADD FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario);

ALTER TABLE reservas
ADD FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal);

ALTER TABLE sucursales
ADD FOREIGN KEY (id_direccion) REFERENCES direcciones(id_direccion);

ALTER TABLE sucursales
ADD FOREIGN KEY (id_vendedor) REFERENCES usuarios(id_usuario);

ALTER TABLE direcciones
ADD FOREIGN KEY (id_ciudad) REFERENCES ciudades(id_ciudad);
