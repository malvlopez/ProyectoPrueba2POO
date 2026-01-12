-- Tabla de conductores
CREATE TABLE conductores (
    id SERIAL PRIMARY KEY,
    cedula VARCHAR(10) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(10),
    email VARCHAR(100),
    tipo_sangre VARCHAR(5),
    documentos_validados BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de usuarios (Para el nuevo Login de 2 roles)
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'ANALISTA')),
    nombre_completo VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de pruebas psicométricas
CREATE TABLE pruebas_psicometricas (
    id SERIAL PRIMARY KEY,
    conductor_id INTEGER NOT NULL REFERENCES conductores(id),
    fecha_realizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nota_reaccion DECIMAL(5,2),
    nota_atencion DECIMAL(5,2),
    nota_coordinacion DECIMAL(5,2),
    nota_percepcion DECIMAL(5,2),
    nota_psicologica DECIMAL(5,2),
    observaciones TEXT
);

-- Tabla de licencias
CREATE TABLE licencias (
    id SERIAL PRIMARY KEY,
    conductor_id INTEGER NOT NULL REFERENCES conductores(id),
    numero_licencia VARCHAR(20) UNIQUE NOT NULL,
    tipo_licencia VARCHAR(5) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    prueba_psicometrica_id INTEGER REFERENCES pruebas_psicometricas(id),
    observaciones TEXT
);

-- Insertar el Administrador inicial para poder entrar al sistema
INSERT INTO usuarios (username, password_hash, rol, nombre_completo) 
<<<<<<< HEAD
VALUES ('admin_root', 'admin123', 'ADMINISTRADOR', 'Super Administrador');


-- Controladores para conductor

ALTER TABLE conductores ADD CONSTRAINT chk_cedula CHECK (cedula ~ '^[0-9]{10}$');

-- Cédula: exactamente 10 dígitos
ALTER TABLE conductores ADD CONSTRAINT chk_conductores_cedula CHECK (cedula ~ '^[0-9]{10}$');

-- Teléfono: 7 a 10 dígitos
ALTER TABLE conductores ADD CONSTRAINT chk_conductores_telefono CHECK (telefono IS NULL OR telefono ~ '^[0-9]{7,10}$');

-- Email válido
ALTER TABLE conductores ADD CONSTRAINT chk_conductores_email
CHECK (
    email IS NULL OR
    email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'
);

-- Fecha de nacimiento no futura
ALTER TABLE conductores ADD CONSTRAINT chk_conductores_fecha_nacimiento CHECK (fecha_nacimiento <= CURRENT_DATE);



--CONTROLADORES PARA USUARIO


ALTER TABLE usuarios ADD CONSTRAINT chk_usuarios_nombre_completo
CHECK (
    nombre_completo ~ '^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$'
);

=======
VALUES ('admin_root', 'admin123', 'ADMINISTRADOR', 'Super Administrador');
>>>>>>> 4dbdb83454ec1ccf9674d8f79359a39eb97c6982
