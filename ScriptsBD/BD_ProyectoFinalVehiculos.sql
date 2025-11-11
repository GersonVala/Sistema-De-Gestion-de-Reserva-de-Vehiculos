DROP DATABASE IF EXISTS reserva_vehiculos;
CREATE DATABASE reserva_vehiculos;
USE reserva_vehiculos;

CREATE TABLE ciudades (
	id_ciudad INT AUTO_INCREMENT PRIMARY KEY,
	nombre_ciudad VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE direcciones (
	id_direccion INT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(40) NOT NULL,
    numero_calle INT NOT NULL,
    id_ciudad INT NOT NULL,
    FOREIGN KEY (id_ciudad) REFERENCES ciudades(id_ciudad)
);

CREATE TABLE usuarios (
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(30) NOT NULL,
    apellido_usuario VARCHAR(30) NOT NULL,
    email_usuario VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL,
    dni_usuario VARCHAR(30) NOT NULL UNIQUE,
    telefono_usuario VARCHAR(20) NOT NULL,
	id_direccion INT,
    FOREIGN KEY (id_direccion) REFERENCES direcciones(id_direccion)
);

CREATE TABLE roles (
	id_rol INT AUTO_INCREMENT PRIMARY KEY,
	nombre_rol ENUM('ADMINISTRADOR', 'CLIENTE', 'VENDEDOR') NOT NULL,
    descripcion_rol VARCHAR(30) NOT NULL
);

CREATE TABLE usuario_roles (
	id_user_rol INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE sucursales (
	id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    telefono_sucursal VARCHAR(30) NOT NULL,
    id_direccion INT NOT NULL,
    id_vendedor INT NOT NULL,
    FOREIGN KEY (id_direccion) REFERENCES direcciones(id_direccion),
    FOREIGN KEY (id_vendedor) REFERENCES usuarios(id_usuario)
);

CREATE TABLE motores (
	id_motor INT AUTO_INCREMENT PRIMARY KEY,
    cilindrada DECIMAL(3,2) NOT NULL,
    caballos_de_fuerza INT NOT NULL,
    tipo_combustible ENUM('NAFTA', 'DIESEL', 'GNC') NOT NULL,
    tipo_motor ENUM('MANUAL', 'HIBRIDO', 'ELECTRICO') NOT NULL
);

CREATE TABLE tipos_de_vehiculo (
	id_tipo_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_vehiculo VARCHAR(30) NOT NULL,
    descripcion_vehiculo VARCHAR(50) NOT NULL
);

CREATE TABLE vehiculos (
	id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    patente VARCHAR(30) NOT NULL UNIQUE,
    modelo VARCHAR(30) NOT NULL,
    marca VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    estado ENUM('RESERVADO', 'ALQUILADO', 'DISPONIBLE', 'DESCOMPUESTO') NOT NULL,
    cant_puertas INT NOT NULL,
    id_motor INT NOT NULL,
    id_tipo_vehiculo INT NOT NULL,
    id_sucursal INT NOT NULL,
    FOREIGN KEY (id_motor) REFERENCES motores(id_motor),
    FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipos_de_vehiculo(id_tipo_vehiculo),
    FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
);

CREATE TABLE reservas (
	id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA') NOT NULL,
    precio_reserva DECIMAL(10,2) NOT NULL,
    id_usuario INT NOT NULL,
    id_sucursal INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
);

CREATE TABLE detalle_reserva (
	id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    observaciones VARCHAR(255) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    id_vehiculo INT NOT NULL,
    id_reserva INT NOT NULL,
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
);