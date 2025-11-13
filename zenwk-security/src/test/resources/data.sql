INSERT INTO seg_usuario (id_usuario, nombre, apellido, email, username, password, estado, fecha_creacion, fecha_modificacion, usuario_creacion, usuario_modificacion)
VALUES (1, 'Carlos', 'Gómez', 'carlos.gomez@example.com', 'cgomez', 'Pass123!', 'ACTIVO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');

INSERT INTO seg_usuario (id_usuario, nombre, apellido, email, username, password, estado, fecha_creacion, fecha_modificacion, usuario_creacion, usuario_modificacion)
VALUES (2, 'Ana', 'Pérez', 'ana.perez@example.com', 'aperez', 'SecurePass456!', 'INACTIVO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'supervisor', 'admin');

INSERT INTO seg_usuario (id_usuario, nombre, apellido, email, username, password, estado, fecha_creacion, fecha_modificacion, usuario_creacion, usuario_modificacion)
VALUES (3, 'Luis', 'Martínez', 'luis.martinez@example.com', 'lmartinez', 'Luis#Pass789', 'ACTIVO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'supervisor');