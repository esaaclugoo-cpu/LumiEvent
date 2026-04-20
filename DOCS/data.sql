-- ============================================================
-- DATOS DE DEMOSTRACIÓN ADICIONALES PARA NOVATICKET
-- ============================================================

-- USUARIOS ADICIONALES
INSERT INTO usuario (nombre, email, password, tipo_usuario) VALUES
('Carlos García', 'carlos@mail.com', '123', 'cliente'),
('María López', 'maria@mail.com', '123', 'cliente'),
('Pedro Martínez', 'pedro@mail.com', '123', 'cliente'),
('Laura Fernández', 'laura@mail.com', '123', 'cliente'),
('SuperAdmin', 'superadmin@mail.com', 'admin123', 'admin');

-- LUGARES ADICIONALES
INSERT INTO lugar (nombre, direccion, ciudad) VALUES
('Palacio de la Música', 'Avenida Principal 500', 'Valencia'),
('Auditorio Nacional', 'Paseo de la Castellana 210', 'Madrid'),
('Teatre Nacional', 'Gran Vía 150', 'Barcelona');

-- EVENTOS ADICIONALES
INSERT INTO evento (nombre, descripcion, fecha, aforo_maximo, tipo_evento, id_lugar, nombre_lugar, direccion, ciudad) VALUES
('Jazz Night 2026', 'Festival internacional de jazz con los mejores artistas', '2026-08-15', 800, 'concierto', 1, 'Arena', 'Calle 1', 'Madrid'),
('Romeo y Julieta', 'Adaptación moderna de la tragedia de Shakespeare', '2026-09-20', 400, 'teatro', 2, 'Teatro Central', 'Calle 2', 'Madrid'),
('Sinfónica Primavera', 'Concierto de orquesta sinfónica', '2026-06-15', 1500, 'concierto', 4, 'Palacio de la Música', 'Avenida Principal 500', 'Valencia'),
('El Quijote en Escena', 'Comedia teatral basada en la novela de Cervantes', '2026-07-10', 350, 'teatro', 5, 'Auditorio Nacional', 'Paseo de la Castellana 210', 'Madrid'),
('Picasso Moderno', 'Exposición retrospectiva del artista español', '2026-08-01', 600, 'museo', 3, 'Museo Nacional', 'Calle 3', 'Barcelona'),
('Arte Digital 2026', 'Instalaciones interactivas de arte contemporáneo', '2026-09-15', 500, 'museo', 6, 'Teatre Nacional', 'Gran Vía 150', 'Barcelona'),
('Metal en Vivo', 'Festival de bandas de heavy metal internacionales', '2026-10-20', 2000, 'concierto', 1, 'Arena', 'Calle 1', 'Madrid');

-- ASIENTOS ADICIONALES PARA LOS NUEVOS EVENTOS
INSERT INTO asiento (id_lugar, fila, numero_asiento, zona) VALUES
(4, 'A', 1, 'VIP'), (4, 'A', 2, 'VIP'), (4, 'A', 3, 'VIP'),
(4, 'B', 1, 'General'), (4, 'B', 2, 'General'), (4, 'B', 3, 'General'),
(4, 'C', 1, 'Premium'), (4, 'C', 2, 'Premium'),
(5, 'A', 1, 'VIP'), (5, 'A', 2, 'VIP'),
(5, 'B', 1, 'General'), (5, 'B', 2, 'General'), (5, 'B', 3, 'General'),
(5, 'C', 1, 'Premium'),
(6, 'A', 1, 'VIP'), (6, 'A', 2, 'VIP'), (6, 'A', 3, 'VIP'),
(6, 'B', 1, 'General'), (6, 'B', 2, 'General'),
(6, 'C', 1, 'Premium'), (6, 'C', 2, 'Premium'), (6, 'C', 3, 'Premium');

-- COMPRAS ADICIONALES
INSERT INTO compra (id_usuario, fecha, total) VALUES
(3, '2026-04-01 15:30:00', 200.00),
(4, '2026-04-05 10:15:00', 75.00),
(5, '2026-04-10 18:45:00', 320.00),
(1, '2026-04-12 14:20:00', 150.00),
(2, '2026-04-15 11:00:00', 225.00);

-- TICKETS ADICIONALES
INSERT INTO ticket (id_evento, id_asiento, tipo, id_compra, cantidad, precio_unitario) VALUES
(4, 5, 'general', 4, 2, 25.00),
(5, 6, 'vip', 5, 1, 75.00),
(4, 7, 'premium', 6, 2, 80.00),
(6, 8, 'general', 6, 1, 35.00),
(7, NULL, 'general', 7, 3, 50.00),
(10, 13, 'vip', 8, 1, 100.00),
(10, 14, 'general', 8, 2, 45.00);

-- CONCIERTOS ADICIONALES
INSERT INTO concierto VALUES
(4, 'Artists Variados', 'Jazz', 180),
(7, 'Bandas Internacionales', 'Heavy Metal', 240),
(9, 'Orquesta Sinfónica Nacional', 'Clásica', 120);

-- TEATROS ADICIONALES
INSERT INTO teatro VALUES
(5, 'Romeo y Julieta', 'Director Moderno'),
(8, 'El Quijote', 'Compañía Nacional de Teatro');

-- MUSEOS ADICIONALES
INSERT INTO museo VALUES
(6, 'Picasso: Su Vida y Obra', 'Retrospectiva', '2026-08-30'),
(9, 'Convergencia Digital', 'Arte Contemporáneo', '2026-10-01');
