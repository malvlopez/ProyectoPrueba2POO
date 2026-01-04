USE sistema_licencias;

CREATE DATABASE sistema_licencias;
-- Tabla de conductores
CREATE TABLE conductores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de licencias
CREATE TABLE licencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conductor_id BIGINT NOT NULL,
    numero_licencia VARCHAR(20) UNIQUE NOT NULL,
    tipo_licencia VARCHAR(5) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    prueba_psicometrica_id BIGINT,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conductor_id) REFERENCES conductores(id)
);

-- Tabla de pruebas psicométricas
CREATE TABLE pruebas_psicometricas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conductor_id BIGINT NOT NULL,
    fecha_realizacion DATE NOT NULL,
    nota_reaccion DECIMAL(5,2),
    nota_atencion DECIMAL(5,2),
    nota_coordinacion DECIMAL(5,2),
    nota_percepcion DECIMAL(5,2),
    nota_psicologica DECIMAL(5,2),
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conductor_id) REFERENCES conductores(id)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_conductor_cedula ON conductores(cedula);
CREATE INDEX idx_licencia_numero ON licencias(numero_licencia);
CREATE INDEX idx_licencia_conductor ON licencias(conductor_id);
CREATE INDEX idx_prueba_conductor ON pruebas_psicometricas(conductor_id);
