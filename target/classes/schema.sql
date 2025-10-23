DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS cargas;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS tracking;

CREATE TABLE roles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT 1
);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE cargas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descripcion TEXT NOT NULL,
    peso REAL NOT NULL,
    origen TEXT NOT NULL,
    destino TEXT NOT NULL,
    fecha DATE NOT NULL
);

CREATE TABLE shipments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    carga_id INTEGER NOT NULL,
    fecha_envio DATE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    FOREIGN KEY (carga_id) REFERENCES cargas(id)
);

CREATE TABLE tracking (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shipment_id INTEGER NOT NULL,
    ubicacion TEXT NOT NULL,
    fecha DATE NOT NULL,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (email, password, active) 
VALUES ('admin@logiaduana.local', 
'$2a$10$uXCsDnmlZb9Up4kE8wP0C.RdZ7Q/d2MW2IN/ujqX9lX9fgFihA6wW', 1);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
