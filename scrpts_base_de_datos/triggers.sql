-- ...existing code...

DELIMITER $$

CREATE TRIGGER trg_reservas_after_insert
AFTER INSERT ON reservas
FOR EACH ROW
BEGIN
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
END$$

CREATE TRIGGER trg_reservas_after_update
AFTER UPDATE ON reservas
FOR EACH ROW
BEGIN
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
END$$

DELIMITER ;