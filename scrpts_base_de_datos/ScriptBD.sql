DROP DATABASE IF EXISTS reserva_vehiculos;
CREATE DATABASE reserva_vehiculos
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;
USE reserva_vehiculos;
-- CREACION DE TABLAS
CREATE TABLE usuarios (
	id_usuario INT  PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    contra VARCHAR(255) NOT NULL,
    dni VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(50) NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    id_rol INT NOT NULL
);

CREATE TABLE roles (
	id_rol INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(30) NOT NULL,
    descripcion VARCHAR(50) NOT NULL
);


CREATE TABLE vehiculos (
	id_vehiculo INT  PRIMARY KEY AUTO_INCREMENT,
    patente VARCHAR(50) NOT NULL UNIQUE,
    modelo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    color VARCHAR(50) NOT NULL,
    estado ENUM('RESERVADO', 'ENTREGADO', 'DISPONIBLE', 'DESCOMPUESTO') NOT NULL DEFAULT 'DISPONIBLE',
    cant_puertas INT NOT NULL,
    descripcion VARCHAR(150) NOT NULL,
    imagen_url VARCHAR(255),
    precio_diario DECIMAL(10,2) NOT NULL,
    id_motor INT NOT NULL,
    id_tipo_vehiculo INT NOT NULL,
    id_sucursal INT NOT NULL
);

CREATE TABLE motores (
	id_motor INT  PRIMARY KEY AUTO_INCREMENT,
    cilindrada DECIMAL(3,2) NOT NULL,
    caballos_de_fuerza INT NOT NULL,
    tipo_combustible ENUM('NAFTA', 'DIESEL', 'GNC') NOT NULL,
    tipo_motor ENUM('MANUAL', 'HIBRIDO', 'ELECTRICO','AUTOMATICO') NOT NULL
);

CREATE TABLE tipo_vehiculo (
	id_tipo_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    caracteristicas VARCHAR(150) NOT NULL
);

CREATE TABLE reservas (
	id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('TRANSFERENCIA', 'TARJETA', 'EFECTIVO') NOT NULL,
    estado ENUM('ACEPTADA' ,'PENDIENTE', 'CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
    id_usuario INT NOT NULL,
    id_sucursal_retiro INT NOT NULL,
    id_sucursal_devolucion INT NOT NULL,
    id_vehiculo INT NOT NULL

);

CREATE TABLE sucursales (
	id_sucursal INT  PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    imagen_url VARCHAR(255),
    estado TINYINT(1) NOT NULL DEFAULT 1
);
CREATE TABLE empleados (
    id_empleado INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_sucursal INT NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1
    );


-- RELACIONES
ALTER TABLE usuarios
ADD FOREIGN KEY (id_rol) REFERENCES roles(id_rol);

ALTER TABLE vehiculos
ADD FOREIGN KEY (id_motor) REFERENCES motores(id_motor),
ADD FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipo_vehiculo(id_tipo_vehiculo),
ADD FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal);

ALTER TABLE reservas
ADD FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
ADD FOREIGN KEY (id_sucursal_retiro) REFERENCES sucursales(id_sucursal),
ADD FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
ADD FOREIGN KEY (id_sucursal_devolucion) REFERENCES sucursales(id_sucursal);

ALTER TABLE empleados
    ADD FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    ADD FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal);

