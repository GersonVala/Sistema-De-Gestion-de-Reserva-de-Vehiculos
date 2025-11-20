-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 20-11-2025 a las 20:01:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `reserva_vehiculos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleados`
--

CREATE TABLE `empleados` (
  `id_empleado` bigint(20) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_sucursal` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `empleados`
--

INSERT INTO `empleados` (`id_empleado`, `id_usuario`, `id_sucursal`, `estado`) VALUES
(1, 3, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `motores`
--

CREATE TABLE `motores` (
  `id_motor` int(11) NOT NULL,
  `cilindrada` decimal(3,2) NOT NULL,
  `caballos_de_fuerza` int(11) NOT NULL,
  `tipo_combustible` enum('NAFTA','DIESEL','GNC') NOT NULL,
  `tipo_motor` enum('MANUAL','HIBRIDO','ELECTRICO','AUTOMATICO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `motores`
--

INSERT INTO `motores` (`id_motor`, `cilindrada`, `caballos_de_fuerza`, `tipo_combustible`, `tipo_motor`) VALUES
(1, 1.60, 110, 'NAFTA', 'MANUAL'),
(2, 2.00, 150, 'NAFTA', 'AUTOMATICO'),
(3, 2.50, 200, 'NAFTA', 'AUTOMATICO'),
(4, 1.20, 90, 'NAFTA', 'MANUAL'),
(5, 3.00, 240, 'DIESEL', 'AUTOMATICO'),
(6, 0.00, 0, 'GNC', 'MANUAL');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `id_reserva` bigint(20) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `metodo_pago` enum('TRANSFERENCIA','TARJETA','EFECTIVO') NOT NULL,
  `estado` enum('ACEPTADA','PENDIENTE','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  `id_usuario` int(11) NOT NULL,
  `id_sucursal_retiro` int(11) NOT NULL,
  `id_sucursal_devolucion` int(11) NOT NULL,
  `id_vehiculo` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`id_reserva`, `fecha_inicio`, `fecha_fin`, `precio`, `metodo_pago`, `estado`, `id_usuario`, `id_sucursal_retiro`, `id_sucursal_devolucion`, `id_vehiculo`) VALUES
(1, '2025-11-21', '2025-11-23', 220.00, 'TRANSFERENCIA', 'CANCELADA', 2, 1, 1, 20),
(2, '2025-11-21', '2025-11-23', 220.00, 'TRANSFERENCIA', 'CANCELADA', 2, 1, 1, 20),
(3, '2025-11-20', '2025-11-22', 90.00, 'TRANSFERENCIA', 'PENDIENTE', 2, 1, 1, 1);

--
-- Disparadores `reservas`
--
DELIMITER $$
CREATE TRIGGER `trg_reservas_after_insert` AFTER INSERT ON `reservas` FOR EACH ROW BEGIN
    -- Petición del cliente: reserva PENDIENTE → vehículo pasa a RESERVADO
    IF NEW.estado = 'PENDIENTE' THEN
        UPDATE vehiculos SET estado = 'RESERVADO' WHERE id_vehiculo = NEW.id_vehiculo;
    END IF;

    -- Si por algún motivo se inserta ya aceptada, marcar ENTREGADO
    IF NEW.estado = 'ACEPTADA' THEN
        UPDATE vehiculos SET estado = 'ENTREGADO' WHERE id_vehiculo = NEW.id_vehiculo;
    END IF;

    -- Si se inserta como CANCELADA, asegurar vehículo DISPONIBLE
    IF NEW.estado = 'CANCELADA' THEN
        UPDATE vehiculos SET estado = 'DISPONIBLE' WHERE id_vehiculo = NEW.id_vehiculo;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_reservas_after_update` AFTER UPDATE ON `reservas` FOR EACH ROW BEGIN
    -- Cambio de estado: aplicar la regla correspondiente
    IF NEW.estado <> OLD.estado THEN
        IF NEW.estado = 'ACEPTADA' THEN
            -- vendedor acepta → vehículo ENTREGADO (cliente retira)
            UPDATE vehiculos SET estado = 'ENTREGADO' WHERE id_vehiculo = NEW.id_vehiculo;
        ELSEIF NEW.estado = 'CANCELADA' THEN
            -- vendedor rechaza o cancelación → vehículo LIBERADO
            UPDATE vehiculos SET estado = 'DISPONIBLE' WHERE id_vehiculo = NEW.id_vehiculo;
        ELSEIF NEW.estado = 'PENDIENTE' THEN
            -- volver a petición pendiente → reservar el vehículo
            UPDATE vehiculos SET estado = 'RESERVADO' WHERE id_vehiculo = NEW.id_vehiculo;
        END IF;
    END IF;

    -- Devolución física: si cambió la sucursal de devolución y la fecha_fin ya pasó o es la actualidad,
    -- mover el vehículo a la sucursal de devolución y dejarlo DISPONIBLE.
    IF NEW.id_sucursal_devolucion <> OLD.id_sucursal_devolucion
       AND NEW.fecha_fin <= CURDATE() THEN
        UPDATE vehiculos
        SET id_sucursal = NEW.id_sucursal_devolucion,
            estado = 'DISPONIBLE'
        WHERE id_vehiculo = NEW.id_vehiculo;
    END IF;

    -- Si la reserva cambió de vehículo, liberar el vehículo antiguo y reservar el nuevo si está PENDIENTE
    IF OLD.id_vehiculo IS NOT NULL AND OLD.id_vehiculo <> NEW.id_vehiculo THEN
        UPDATE vehiculos SET estado = 'DISPONIBLE' WHERE id_vehiculo = OLD.id_vehiculo;
        IF NEW.estado = 'PENDIENTE' THEN
            UPDATE vehiculos SET estado = 'RESERVADO' WHERE id_vehiculo = NEW.id_vehiculo;
        END IF;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre`, `descripcion`) VALUES
(1, 'ADMIN', 'Gestiona sucursales y autos'),
(2, 'VENDEDOR', 'Gestiona reservas'),
(3, 'CLIENTE', 'Puede reservar');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sucursales`
--

CREATE TABLE `sucursales` (
  `id_sucursal` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `direccion` varchar(50) NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `sucursales`
--

INSERT INTO `sucursales` (`id_sucursal`, `nombre`, `direccion`, `imagen_url`, `estado`) VALUES
(1, 'Sucursal Centro', 'Av. Principal 123', 'https://media.ford.com/content/fordmedia/fsa/ar/es/news/2025/4/ford-inaugura-en-necochea-nueva-sucursal-de-balcarce-autos/jcr:content/image.img.881.495.jpg/1746035071749.jpg', 1),
(2, 'Sucursal Norte', 'Calle Norte 45', 'https://autoexecutive.com.ar/wp-content/uploads/elementor/thumbs/tapa-pb9valcmvvidxtt77ytfi44gdtavqcdn6kg4kdtaq0.jpeg', 1),
(3, 'Sucursal Sur', 'Boulevard Sur 789', 'https://ss-static-01.esmsv.com/id/159329/galeriaimagenes/obtenerimagen/?id=67&tipoEscala=stretch&width=977&height=440', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_vehiculo`
--

CREATE TABLE `tipo_vehiculo` (
  `id_tipo_vehiculo` int(11) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `caracteristicas` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `tipo_vehiculo`
--

INSERT INTO `tipo_vehiculo` (`id_tipo_vehiculo`, `tipo`, `caracteristicas`) VALUES
(1, 'Sedan', '4 puertas, cómodo para ciudad'),
(2, 'SUV', 'Más espacio, ideal familia'),
(3, 'Hatchback', 'Compacto y económico'),
(4, 'Camioneta', 'Carga y tracción'),
(5, 'Coupé', '2 puertas, deportivo'),
(6, 'Convertible', 'Techo retráctil');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `contra` varchar(255) NOT NULL,
  `dni` varchar(9) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `direccion` varchar(50) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `id_rol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `email`, `contra`, `dni`, `telefono`, `direccion`, `estado`, `id_rol`) VALUES
(1, 'Gerson', 'Valashek', 'admin@gmail.com', '$2a$10$FBc77Tyb98z.zf5Ea2qUwOMJuFTlO3j1okYbR..7JqxFhtR/.B75a', '45222333', '3644787878', 'Avenida Córdoba 6472', 1, 1),
(2, 'Tomas', 'Contreras', 'cliente@gmail.com', '$2a$10$rsZ5ZXQzDrqJ14ehoxpJKOCiHigwohd9NLVr9h1aPm2g/gAdBpWB2', '44222888', '3725888888', 'Avenida Córdoba 6475', 1, 3),
(3, 'Maxi', 'Peralta', 'vendedor@gmail.com', '$2a$10$rZ/ZwLew00q1kwgjCSChHOAUCRZuxsM8XKc2Dk3w6Id6tjau74G0y', '42888999', '3644888999', 'Avenida Córdoba 6450', 1, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculos`
--

CREATE TABLE `vehiculos` (
  `id_vehiculo` int(11) NOT NULL,
  `patente` varchar(50) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `color` varchar(50) NOT NULL,
  `estado` enum('RESERVADO','ENTREGADO','DISPONIBLE','DESCOMPUESTO') NOT NULL DEFAULT 'DISPONIBLE',
  `cant_puertas` int(11) NOT NULL,
  `descripcion` varchar(150) NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `precio_diario` decimal(10,2) NOT NULL,
  `id_motor` int(11) NOT NULL,
  `id_tipo_vehiculo` int(11) NOT NULL,
  `id_sucursal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `vehiculos`
--

INSERT INTO `vehiculos` (`id_vehiculo`, `patente`, `modelo`, `marca`, `color`, `estado`, `cant_puertas`, `descripcion`, `imagen_url`, `precio_diario`, `id_motor`, `id_tipo_vehiculo`, `id_sucursal`) VALUES
(1, 'ABC123', 'Corolla', 'Toyota', 'Blanco', 'RESERVADO', 4, 'Sedan económico', 'https://resizer.iproimg.com/unsafe/1280x/filters:format(webp):quality(75):max_bytes(102400)/https://assets.iprofesional.com/assets/jpg/2023/09/560295.jpg', 45.00, 1, 1, 1),
(3, 'GHI345', 'Cruze', 'Chevrolet', 'Gris', 'DISPONIBLE', 4, 'Sedan cómodo', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTh_KH9L2clGg4_LPVDePkiDW6zdxOk7_WEdA&s', 42.00, 1, 1, 2),
(4, 'JKL456', 'RAV4', 'Toyota', 'Azul', 'DISPONIBLE', 5, 'SUV mediana', 'https://resizer.iproimg.com/unsafe/1280x/filters:format(webp):quality(75):max_bytes(102400)/https://assets.iprofesional.com/assets/jpg/2024/10/585903.jpg', 75.00, 3, 2, 1),
(5, 'MNO567', 'Captiva', 'Chevrolet', 'Rojo', 'DISPONIBLE', 5, 'SUV familiar', NULL, 70.00, 3, 2, 2),
(6, 'PQR678', 'Golf', 'Volkswagen', 'Blanco', 'DISPONIBLE', 3, 'Hatchback práctico', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8IxVgp0t91YZemCIlr11KslrhTnsWxD4Rdw&s', 40.00, 4, 3, 3),
(7, 'STU789', 'Fiesta', 'Ford', 'Amarillo', 'DISPONIBLE', 3, 'Hatch pequeño', NULL, 35.00, 4, 3, 3),
(8, 'VWX890', 'Hilux', 'Toyota', 'Plata', 'DISPONIBLE', 4, 'Camioneta robusta', NULL, 95.00, 5, 4, 2),
(9, 'YZA901', 'Amarok', 'Volkswagen', 'Negro', 'DESCOMPUESTO', 4, 'Camioneta potente', NULL, 98.00, 5, 4, 1),
(11, 'EFG123', 'MX-5', 'Mazda', 'Rojo', 'DISPONIBLE', 2, 'Convertible deportivo', NULL, 130.00, 2, 6, 3),
(12, 'HIJ234', 'Leaf', 'Nissan', 'Blanco', 'ENTREGADO', 4, 'Eléctrico compacto', NULL, 80.00, 6, 1, 2),
(13, 'KLM345', 'Corolla H', 'Toyota', 'Gris', 'DISPONIBLE', 4, 'Híbrido', NULL, 65.00, 2, 1, 1),
(14, 'NOP456', 'T-Cross', 'Volkswagen', 'Azul', 'DISPONIBLE', 5, 'SUV compacto', NULL, 60.00, 2, 2, 3),
(15, 'QRS567', 'Sandero', 'Renault', 'Blanco', 'DISPONIBLE', 4, 'Económico familiar', NULL, 30.00, 4, 1, 3),
(16, 'TUV678', 'Kicks', 'Nissan', 'Negro', 'DISPONIBLE', 5, 'SUV compacto', NULL, 55.00, 2, 2, 2),
(17, 'WXY789', 'F-150', 'Ford', 'Plata', 'DESCOMPUESTO', 4, 'Camioneta full-size', NULL, 120.00, 5, 4, 1),
(18, 'ZAB890', 'C4', 'Citroen', 'Gris', 'DISPONIBLE', 4, 'Sedan compacto', NULL, 38.00, 1, 1, 2),
(19, 'CDE901', 'Onix', 'Chevrolet', 'Negro', 'DISPONIBLE', 4, 'Compacto económico', NULL, 33.00, 4, 3, 3),
(20, 'FGH012', 'A3', 'Audi', 'Blanco', 'DISPONIBLE', 4, 'Premium compacto', NULL, 110.00, 3, 1, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `empleados`
--
ALTER TABLE `empleados`
  ADD PRIMARY KEY (`id_empleado`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_sucursal` (`id_sucursal`);

--
-- Indices de la tabla `motores`
--
ALTER TABLE `motores`
  ADD PRIMARY KEY (`id_motor`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id_reserva`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_sucursal_retiro` (`id_sucursal_retiro`),
  ADD KEY `id_vehiculo` (`id_vehiculo`),
  ADD KEY `id_sucursal_devolucion` (`id_sucursal_devolucion`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_rol`);

--
-- Indices de la tabla `sucursales`
--
ALTER TABLE `sucursales`
  ADD PRIMARY KEY (`id_sucursal`);

--
-- Indices de la tabla `tipo_vehiculo`
--
ALTER TABLE `tipo_vehiculo`
  ADD PRIMARY KEY (`id_tipo_vehiculo`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD KEY `id_rol` (`id_rol`);

--
-- Indices de la tabla `vehiculos`
--
ALTER TABLE `vehiculos`
  ADD PRIMARY KEY (`id_vehiculo`),
  ADD UNIQUE KEY `patente` (`patente`),
  ADD KEY `id_motor` (`id_motor`),
  ADD KEY `id_tipo_vehiculo` (`id_tipo_vehiculo`),
  ADD KEY `id_sucursal` (`id_sucursal`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `empleados`
--
ALTER TABLE `empleados`
  MODIFY `id_empleado` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `motores`
--
ALTER TABLE `motores`
  MODIFY `id_motor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id_reserva` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `sucursales`
--
ALTER TABLE `sucursales`
  MODIFY `id_sucursal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tipo_vehiculo`
--
ALTER TABLE `tipo_vehiculo`
  MODIFY `id_tipo_vehiculo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `vehiculos`
--
ALTER TABLE `vehiculos`
  MODIFY `id_vehiculo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `empleados`
--
ALTER TABLE `empleados`
  ADD CONSTRAINT `empleados_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `empleados_ibfk_2` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`);

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`id_sucursal_retiro`) REFERENCES `sucursales` (`id_sucursal`),
  ADD CONSTRAINT `reservas_ibfk_3` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculos` (`id_vehiculo`),
  ADD CONSTRAINT `reservas_ibfk_4` FOREIGN KEY (`id_sucursal_devolucion`) REFERENCES `sucursales` (`id_sucursal`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`);

--
-- Filtros para la tabla `vehiculos`
--
ALTER TABLE `vehiculos`
  ADD CONSTRAINT `vehiculos_ibfk_1` FOREIGN KEY (`id_motor`) REFERENCES `motores` (`id_motor`),
  ADD CONSTRAINT `vehiculos_ibfk_2` FOREIGN KEY (`id_tipo_vehiculo`) REFERENCES `tipo_vehiculo` (`id_tipo_vehiculo`),
  ADD CONSTRAINT `vehiculos_ibfk_3` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
