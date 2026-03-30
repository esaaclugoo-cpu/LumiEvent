drop database if exists novaticket;
create database novaticket;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('cliente','admin')
);


CREATE TABLE evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha DATE NOT NULL,
    aforo_maximo INT NOT NULL,
    tipo_evento ENUM('concierto','museo','teatro') NOT NULL,
    id_lugar INT NOT NULL,
    nombre_lugar VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    ruta_imagen VARCHAR(255)
    
);

CREATE TABLE concierto (
    id_evento INT PRIMARY KEY,
    artista_principal VARCHAR(150) NOT NULL,
    genero_musical VARCHAR(100) NOT NULL,
    duracion_minutos INT NOT NULL,
    
    FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE
);

CREATE TABLE teatro (
    id_evento INT PRIMARY KEY,
    obra VARCHAR(150) NOT NULL,
    director VARCHAR(150) NOT NULL,
    
    FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE
);

CREATE TABLE museo (
    id_evento INT PRIMARY KEY,
    nombre_exposicion VARCHAR(150) NOT NULL,
    tipo_exposicion VARCHAR(100) NOT NULL,
    fecha_fin DATE,
    
    FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE
);

CREATE TABLE asiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_lugar INT NOT NULL,
    fila VARCHAR(10) NOT NULL,
    numero_asiento INT NOT NULL,
    zona VARCHAR(50) NOT NULL,

    UNIQUE(id_lugar, fila, numero_asiento)
    
);



CREATE TABLE compra (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha DATETIME NOT NULL,
    total DECIMAL(10,2),
    
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);



CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_asiento INT,
    tipo VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    id_compra INT NOT NULL,
    id_ticket INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    
    FOREIGN KEY (id_evento) REFERENCES evento(id),
    FOREIGN KEY (id_asiento) REFERENCES asiento(id),
    FOREIGN KEY (id_compra) REFERENCES compra(id) ON DELETE CASCADE
);





--DEMO DATA--


INSERT INTO usuario (nombre, email, password, tipo_usuario) VALUES
('Juan', 'juan@mail.com', '123', 'cliente'),
('Ana', 'ana@mail.com', '123', 'cliente'),
('Admin', 'admin@mail.com', 'admin', 'admin');



INSERT INTO evento (nombre, descripcion, fecha, aforo_maximo, tipo_evento, id_lugar, nombre_lugar, direccion, ciudad) VALUES
('Rock Fest', 'Concierto de rock', '2026-05-10', 1000, 'concierto', 1, 'Arena', 'Calle 1', 'Madrid'),
('Hamlet', 'Obra clásica', '2026-06-01', 300, 'teatro', 2, 'Teatro Central', 'Calle 2', 'Madrid'),
('Expo Arte', 'Exposición moderna', '2026-07-01', 200, 'museo', 3, 'Museo Nacional', 'Calle 3', 'Barcelona');



INSERT INTO concierto VALUES
(1, 'Metallica Fake', 'Rock', 120);


INSERT INTO teatro VALUES
(2, 'Hamlet', 'Director X');



INSERT INTO museo VALUES
(3, 'Arte Moderno', 'Pintura', '2026-08-01');



INSERT INTO asiento (id_lugar, fila, numero_asiento, zona) VALUES
(1, 'A', 1, 'VIP'),
(1, 'A', 2, 'VIP'),
(2, 'B', 10, 'General');


INSERT INTO compra (id_usuario, fecha, total) VALUES
(1, '2026-03-01 10:00:00', 100.00),
(2, '2026-03-02 12:00:00', 50.00);


INSERT INTO ticket (id_evento, id_asiento, tipo, precio, id_compra, id_ticket, cantidad, precio_unitario) VALUES
(1, 1, 'General', 50.00, 1, 1, 2, 25.00),
(2, 2, 'VIP', 50.00, 2, 2, 1, 50.00);







