drop database if exists novaticket;
create database novaticket;
use novaticket;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('cliente','admin')
);

CREATE TABLE lugar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL
);


CREATE TABLE evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha DATE NOT NULL,
    aforo_maximo INT NOT NULL,
    tipo_evento ENUM('concierto','museo','teatro') NOT NULL,
    id_lugar INT NOT NULL  ,
    ruta_imagen VARCHAR(255)

    ,FOREIGN KEY (id_lugar) REFERENCES lugar(id) ON DELETE CASCADE
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

    UNIQUE(id_lugar, fila, numero_asiento),
    FOREIGN KEY (id_lugar) REFERENCES lugar(id)
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
    id_compra INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,

    UNIQUE(id_evento, id_asiento),
    FOREIGN KEY (id_evento) REFERENCES evento(id),
    FOREIGN KEY (id_asiento) REFERENCES asiento(id),
    FOREIGN KEY (id_compra) REFERENCES compra(id) ON DELETE CASCADE
);