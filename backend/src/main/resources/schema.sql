-- 1. Tabla de Roles
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Tabla Intermedia
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 4. Sucursales
CREATE TABLE IF NOT EXISTS sucursales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(50),
    porcentaje_comision DOUBLE DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE
);

-- 4b. Especialidades
CREATE TABLE IF NOT EXISTS especialidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE
);

-- 5. Obras Sociales
CREATE TABLE IF NOT EXISTS obras_sociales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(50),
    telefono VARCHAR(50),
    email VARCHAR(150),
    is_active BOOLEAN DEFAULT TRUE
);

-- 6. Planes de Obras Sociales
CREATE TABLE IF NOT EXISTS planes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    obra_social_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_plan_os FOREIGN KEY (obra_social_id) REFERENCES obras_sociales(id) ON DELETE CASCADE
);

-- 7. Estudios
CREATE TABLE IF NOT EXISTS estudios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    codigo VARCHAR(50),
    descripcion VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE
);

-- 8. Pacientes
CREATE TABLE IF NOT EXISTS pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE,
    sexo VARCHAR(20),
    telefono VARCHAR(50),
    email VARCHAR(150),
    direccion VARCHAR(255),
    obra_social_id BIGINT,
    plan_id BIGINT,
    numero_afiliado VARCHAR(50),
    observaciones VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pac_os FOREIGN KEY (obra_social_id) REFERENCES obras_sociales(id) ON DELETE SET NULL,
    CONSTRAINT fk_pac_plan FOREIGN KEY (plan_id) REFERENCES planes(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_pacientes_dni ON pacientes(dni);
CREATE INDEX IF NOT EXISTS idx_pacientes_apellido ON pacientes(apellido);

-- 9. Profesionales
CREATE TABLE IF NOT EXISTS profesionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20),
    matricula VARCHAR(50),
    telefono VARCHAR(50),
    email VARCHAR(150),
    user_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prof_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 10. Relaciones Many-to-Many de Profesionales
CREATE TABLE IF NOT EXISTS profesional_especialidades (
    profesional_id BIGINT NOT NULL,
    especialidad_id BIGINT NOT NULL,
    PRIMARY KEY (profesional_id, especialidad_id),
    CONSTRAINT fk_pesp_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CONSTRAINT fk_pesp_esp FOREIGN KEY (especialidad_id) REFERENCES especialidades(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS profesional_obras_sociales (
    profesional_id BIGINT NOT NULL,
    obra_social_id BIGINT NOT NULL,
    PRIMARY KEY (profesional_id, obra_social_id),
    CONSTRAINT fk_pos_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CONSTRAINT fk_pos_os FOREIGN KEY (obra_social_id) REFERENCES obras_sociales(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS profesional_estudios (
    profesional_id BIGINT NOT NULL,
    estudio_id BIGINT NOT NULL,
    PRIMARY KEY (profesional_id, estudio_id),
    CONSTRAINT fk_pe_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CONSTRAINT fk_pe_est FOREIGN KEY (estudio_id) REFERENCES estudios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS profesional_sucursales (
    profesional_id BIGINT NOT NULL,
    sucursal_id BIGINT NOT NULL,
    PRIMARY KEY (profesional_id, sucursal_id),
    CONSTRAINT fk_ps_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_suc FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE
);

-- 11. Agenda Médica
CREATE TABLE IF NOT EXISTS agenda_medica (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profesional_id BIGINT NOT NULL,
    sucursal_id BIGINT NOT NULL,
    dia_semana INT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    duracion_turno_minutos INT NOT NULL DEFAULT 30,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_ag_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CONSTRAINT fk_ag_suc FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE
);

-- 12. Turnos
CREATE TABLE IF NOT EXISTS turnos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    profesional_id BIGINT NOT NULL,
    sucursal_id BIGINT NOT NULL,
    estudio_id BIGINT,
    obra_social_id BIGINT,
    plan_id BIGINT,
    fecha_hora DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    observaciones VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_turno_pac FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    CONSTRAINT fk_turno_prof FOREIGN KEY (profesional_id) REFERENCES profesionales(id),
    CONSTRAINT fk_turno_suc FOREIGN KEY (sucursal_id) REFERENCES sucursales(id),
    CONSTRAINT fk_turno_est FOREIGN KEY (estudio_id) REFERENCES estudios(id) ON DELETE SET NULL,
    CONSTRAINT fk_turno_os FOREIGN KEY (obra_social_id) REFERENCES obras_sociales(id) ON DELETE SET NULL,
    CONSTRAINT fk_turno_plan FOREIGN KEY (plan_id) REFERENCES planes(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_turnos_fecha ON turnos(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_turnos_estado ON turnos(estado);