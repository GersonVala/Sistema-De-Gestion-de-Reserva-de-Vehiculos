DROP DATABASE IF EXISTS reserva_vehiculos;
CREATE DATABASE reserva_vehiculos;
USE reserva_vehiculos;

CREATE TABLE usuarios (
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(30) NOT NULL,
    apellido_usuario VARCHAR(30) NOT NULL,
    email_usuario VARCHAR(30) NOT NULL,
    contraseña VARCHAR(30) NOT NULL,
    dni_usuario VARCHAR(30) NOT NULL,
    telefono_usuario VARCHAR(30) NOT NULL,
	id_direccion INT NOT NULL
);

CREATE TABLE usuario_roles (
	id_user_rol INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL
);

CREATE TABLE roles (
	id_rol INT AUTO_INCREMENT PRIMARY KEY,
	nombre_rol ENUM('administrador', 'cliente', 'vendedor') NOT NULL,
    descripcion_rol VARCHAR(30) NOT NULL
);

CREATE TABLE direcciones (
	id_direccion INT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(40) NOT NULL,
    numero_calle INT NOT NULL,
    id_ciudad INT NOT NULL
);

CREATE TABLE ciudades (
	id_ciudad INT AUTO_INCREMENT PRIMARY KEY,
	nombre_ciudad VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE vehiculos (
	id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    patente VARCHAR(10) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    marca VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    estado ENUM('RESERVADO', 'ENTREGADO', 'DISPONIBLE', 'DESCOMPUESTO') NOT NULL,
    cant_puertas INT NOT NULL,
    id_motor INT NOT NULL,
    id_tipo_vehiculo INT NOT NULL
);

CREATE TABLE motores (
	id_motor INT AUTO_INCREMENT PRIMARY KEY,
    cilindrada DECIMAL(3,2) NOT NULL,
    caballos_de_fuerza INT NOT NULL,
    tipo_combustible ENUM('nafta', 'diesel', 'GNC') NOT NULL,
    tipo_motor ENUM('manual', 'hibrido', 'electrico') NOT NULL
);

CREATE TABLE tipo_vehiculo (
	id_tipo_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_vehiculo VARCHAR(30) NOT NULL,
    descripcion_vehiculo VARCHAR(50) NOT NULL
);

CREATE TABLE reservas (
	id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('ENTREGADO', 'RESERVADO', 'DISPONIBLE') NOT NULL,
    precio_reserva DECIMAL(10,2) NOT NULL,
    id_usuario INT NOT NULL,
    id_sucursal INT NOT NULL
);

CREATE TABLE detalle_reserva (
	id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    observaciones VARCHAR(50) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    id_vehiculo INT NOT NULL,
    id_reserva INT NOT NULL
);

CREATE TABLE sucursales (
	id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    telefono_sucursal VARCHAR(30) NOT NULL,
    id_direccion INT NOT NULL,
    id_vendedor INT NOT NULL
);