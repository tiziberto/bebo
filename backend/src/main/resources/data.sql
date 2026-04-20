-- Roles
INSERT IGNORE INTO roles (id, name, description) VALUES
(1, 'ROLE_ADMIN',       'Administrador total del sistema'),
(2, 'ROLE_DOCTOR',      'Profesional médico de la clínica'),
(3, 'ROLE_RECEPCION',   'Personal de recepción y secretaría'),
(4, 'ROLE_FACTURACION', 'Personal de facturación y obras sociales'),
(5, 'ROLE_PACIENTE',    'Paciente con acceso a portal'),
(6, 'ROLE_DERIVADOR',   'Médico derivador externo que solicita estudios');

-- Sucursales de la clínica
INSERT IGNORE INTO sucursales (id, nombre, direccion, telefono, porcentaje_comision) VALUES
(1, 'ECOMED',       'Dirección ECOMED',       '011-0000-0001', 0.0),
(2, 'GESTAR',       'Dirección GESTAR',       '011-0000-0002', 0.0),
(3, 'GESTAR OESTE', 'Dirección GESTAR OESTE', '011-0000-0003', 0.0);

-- Especialidades (del Excel)
INSERT IGNORE INTO especialidades (id, nombre, descripcion) VALUES
(1, 'Generalista',              'Ecografías generales, abdominales, partes blandas, Doppler'),
(2, 'Ginecología y Obstetricia','Ecografías obstétricas, ginecológicas, scan fetal, translucencia nucal'),
(3, 'Pediatría',                'Mismas que generalista + caderas, lumbosacra, cerebral/transfontanelar'),
(4, 'Cardiología Obstétrica',   'Ecocardiograma fetal Doppler, Doppler cardiaco fetal'),
(5, 'Mastología',               'Ecografía mamaria bilateral, con Doppler, punción mamaria'),
(6, 'Oftalmología',             'Ecografía ocular');

-- Obras Sociales (37 reales del Excel)
INSERT IGNORE INTO obras_sociales (id, nombre, codigo) VALUES
(1,  'AAPM - Asoc. Agentes de Propaganda Médica', 'AAPM'),
(2,  'Andar Visitar',           'ANDAR'),
(3,  'APSOT',                   'APSOT'),
(4,  'ATSA - Sanidad',          'ATSA'),
(5,  'Avalian',                 'AVALI'),
(6,  'Elevar',                  'ELEVA'),
(7,  'FATFA - Personal de Farmacia', 'FATFA'),
(8,  'Federada Salud',          'FEDER'),
(9,  'Galeno',                  'GALEN'),
(10, 'GIL S.A. Vida Integral',  'GIL'),
(11, 'ISSN',                    'ISSN'),
(12, 'Jerarquicos Salud',       'JERAR'),
(13, 'Medicus',                 'MEDIC'),
(14, 'Medifé',                  'MEDIF'),
(15, 'OSPTV - Obra Social Personal TV', 'OSPTV'),
(16, 'OMINT',                   'OMINT'),
(17, 'OSDE',                    'OSDE'),
(18, 'Osdepym',                 'OSDEP'),
(19, 'Osdipp',                  'OSDIP'),
(20, 'Osdop',                   'OSDOP'),
(21, 'Osfatlyf',                'OSFAT'),
(22, 'Osmata',                  'OSMAT'),
(23, 'Ospatca',                 'OSPAT'),
(24, 'Ospe - Red Omip',         'OSPE'),
(25, 'Ospedyc',                 'OSPEDY'),
(26, 'Ospepri',                 'OSPEPR'),
(27, 'Ospim - Industria Maderera', 'OSPIM'),
(28, 'Osseg',                   'OSSEG'),
(29, 'Poder Judicial',          'PODJU'),
(30, 'Policía Federal',         'POLFE'),
(31, 'Prevención Salud',        'PREVE'),
(32, 'Sancor Salud',            'SANCO'),
(33, 'SCIS',                    'SCIS'),
(34, 'Servicio Penitenciario',  'SERPE'),
(35, 'Sosunc',                  'SOSUN'),
(36, 'Swiss Medical',           'SWISS'),
(37, 'Unión Personal',          'UNPRS'),
(38, 'Particular',              'PART');

-- Planes básicos por obra social
INSERT IGNORE INTO planes (id, nombre, obra_social_id) VALUES
(1,  'Plan General',  17),  -- OSDE
(2,  'OSDE 210',      17),
(3,  'OSDE 310',      17),
(4,  'OSDE 410',      17),
(5,  'Plan General',  36),  -- Swiss Medical
(6,  'SMG20',         36),
(7,  'SMG30',         36),
(8,  'Plan General',  9),   -- Galeno
(9,  'Azul',          9),
(10, 'Plata',         9),
(11, 'Oro',           9),
(12, 'Plan General',  14),  -- Medifé
(13, 'Plan General',  13),  -- Medicus
(14, 'Plan General',  16),  -- OMINT
(15, 'Plan General',  38),  -- Particular
(16, 'Plan General',  5),   -- Avalian
(17, 'Plan General',  32);  -- Sancor Salud

-- ─────────────────────────────────────────────────────────────────────────────
-- USUARIOS DE PRUEBA  (password = "ecomed2025" hasheado con BCrypt)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO users (id, username, password, email, is_active) VALUES
(10, 'admin',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@ecomed.com',          TRUE),
(11, 'recepcion',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'recepcion@ecomed.com',      TRUE),
(12, 'dra.garcia', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'l.garcia@ecomed.com',       TRUE),
(13, 'dr.rodriguez','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','c.rodriguez@ecomed.com',    TRUE),
(14, 'dra.martinez','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','a.martinez@ecomed.com',     TRUE),
(15, 'dr.pereira', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'r.pereira@gestar.com',      TRUE);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(10, 1),  -- admin       → ROLE_ADMIN
(11, 3),  -- recepcion   → ROLE_RECEPCION
(12, 2),  -- dra.garcia  → ROLE_DOCTOR
(13, 2),  -- dr.rodriguez→ ROLE_DOCTOR
(14, 2),  -- dra.martinez→ ROLE_DOCTOR
(15, 2);  -- dr.pereira  → ROLE_DOCTOR

-- ─────────────────────────────────────────────────────────────────────────────
-- PROFESIONALES DE PRUEBA
-- ─────────────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO profesionales (id, nombre, apellido, dni, matricula, telefono, email, user_id, is_active) VALUES
(1, 'Laura',  'García',    '25111001', 'MN-45210', '11-4111-2001', 'l.garcia@ecomed.com',    12, TRUE),
(2, 'Carlos', 'Rodríguez', '28222002', 'MN-38770', '11-4222-3002', 'c.rodriguez@ecomed.com', 13, TRUE),
(3, 'Ana',    'Martínez',  '30333003', 'MN-51830', '11-4333-4003', 'a.martinez@ecomed.com',  14, TRUE),
(4, 'Ricardo','Pereira',   '22444004', 'MN-29940', '11-4444-5004', 'r.pereira@gestar.com',   15, TRUE);

-- Especialidades por profesional
INSERT IGNORE INTO profesional_especialidades (profesional_id, especialidad_id) VALUES
(1, 2), (1, 4),     -- García: Ginecología y Obstetricia + Cardiología Obstétrica
(2, 1), (2, 3),     -- Rodríguez: Generalista + Pediatría
(3, 5), (3, 1),     -- Martínez: Mastología + Generalista
(4, 2), (4, 3);     -- Pereira: Ginecología y Obstetricia + Pediatría

-- Sucursales por profesional
INSERT IGNORE INTO profesional_sucursales (profesional_id, sucursal_id) VALUES
(1, 2),             -- García → GESTAR
(2, 1), (2, 3),     -- Rodríguez → ECOMED + GESTAR OESTE
(3, 1),             -- Martínez → ECOMED
(4, 2), (4, 3);     -- Pereira → GESTAR + GESTAR OESTE

-- Obras sociales por profesional (principales)
INSERT IGNORE INTO profesional_obras_sociales (profesional_id, obra_social_id) VALUES
(1,17),(1,36),(1,9),(1,14),(1,38),(1,32),(1,16),
(2,17),(2,36),(2,9),(2,13),(2,38),(2,5),(2,11),
(3,17),(3,36),(3,16),(3,14),(3,38),
(4,17),(4,36),(4,9),(4,14),(4,38),(4,32);

-- Estudios por profesional
INSERT IGNORE INTO profesional_estudios (profesional_id, estudio_id) VALUES
-- García (ginecología/obstetricia)
(1,19),(1,20),(1,24),(1,25),(1,26),(1,27),(1,31),(1,35),
-- Rodríguez (generalista/pediatría)
(2,1),(2,2),(2,3),(2,8),(2,9),(2,10),(2,13),(2,17),(2,21),(2,22),(2,23),(2,28),(2,29),(2,30),(2,33),(2,34),
-- Martínez (mastología/generalista)
(3,1),(3,3),(3,8),(3,15),(3,16),(3,17),(3,18),(3,32),
-- Pereira (ginecología/obstetricia/pediatría)
(4,19),(4,20),(4,24),(4,25),(4,26),(4,27),(4,28),(4,29),(4,30),(4,31),(4,35);

-- Agenda médica (horarios de atención)
INSERT IGNORE INTO agenda_medica (id, profesional_id, sucursal_id, dia_semana, hora_inicio, hora_fin, duracion_turno_minutos) VALUES
-- García: Lun-Vie en GESTAR
(1, 1, 2, 1, '08:00', '13:00', 30),
(2, 1, 2, 2, '08:00', '13:00', 30),
(3, 1, 2, 3, '08:00', '13:00', 30),
(4, 1, 2, 4, '08:00', '13:00', 30),
(5, 1, 2, 5, '08:00', '12:00', 30),
-- Rodríguez: Lun-Mié en ECOMED, Jue-Vie en GESTAR OESTE
(6,  2, 1, 1, '09:00', '14:00', 20),
(7,  2, 1, 2, '09:00', '14:00', 20),
(8,  2, 1, 3, '09:00', '14:00', 20),
(9,  2, 3, 4, '09:00', '14:00', 20),
(10, 2, 3, 5, '09:00', '13:00', 20),
-- Martínez: Mar-Jue en ECOMED
(11, 3, 1, 2, '10:00', '16:00', 30),
(12, 3, 1, 3, '10:00', '16:00', 30),
(13, 3, 1, 4, '10:00', '16:00', 30),
-- Pereira: Lun/Mié en GESTAR, Mar/Jue en GESTAR OESTE
(14, 4, 2, 1, '08:30', '13:30', 30),
(15, 4, 3, 2, '08:30', '13:30', 30),
(16, 4, 2, 3, '08:30', '13:30', 30),
(17, 4, 3, 4, '08:30', '13:30', 30);

-- ─────────────────────────────────────────────────────────────────────────────
-- PACIENTES DE PRUEBA
-- ─────────────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO pacientes (id, nombre, apellido, dni, fecha_nacimiento, sexo, telefono, email, direccion, obra_social_id, plan_id, numero_afiliado, observaciones, is_active) VALUES
(1,  'María',    'González',  '30100001', '1990-03-15', 'F', '11-5101-0001', 'mgonzalez@mail.com',  'Av. Corrientes 1234, CABA',   17, 2,  'OSDE-001234', NULL,                  TRUE),
(2,  'Juan',     'Pérez',     '25200002', '1985-07-22', 'M', '11-5202-0002', 'jperez@mail.com',     'Av. Santa Fe 567, CABA',      36, 6,  'SMG-002345',  NULL,                  TRUE),
(3,  'Claudia',  'López',     '33300003', '1992-11-08', 'F', '11-5303-0003', 'clopez@mail.com',     'Maipú 890, CABA',             9,  9,  'GAL-003456',  'Alérgica a ibuprofeno',TRUE),
(4,  'Roberto',  'Díaz',      '28400004', '1988-05-30', 'M', '11-5404-0004', 'rdiaz@mail.com',      'Av. de Mayo 321, CABA',       38, 15, 'PART-004',    NULL,                  TRUE),
(5,  'Sofía',    'Ramírez',   '35500005', '1995-09-14', 'F', '11-5505-0005', 'sramirez@mail.com',   'Palermo 456, CABA',           14, 12, 'MF-005678',   'Embarazada 20 semanas',TRUE),
(6,  'Miguel',   'Torres',    '22600006', '1980-01-25', 'M', '11-5606-0006', 'mtorres@mail.com',    'Belgrano 789, CABA',          17, 3,  'OSDE-006789', NULL,                  TRUE),
(7,  'Patricia', 'Jiménez',   '26700007', '1983-06-12', 'F', '11-5707-0007', 'pjimenez@mail.com',   'Recoleta 012, CABA',          16, 16, 'AVA-007890',  NULL,                  TRUE),
(8,  'Diego',    'Morales',   '31800008', '1991-04-03', 'M', '11-5808-0008', 'dmorales@mail.com',   'San Telmo 345, CABA',         36, 7,  'SMG-008901',  NULL,                  TRUE),
(9,  'Ana',      'Fernández', '27900009', '1987-12-20', 'F', '11-5909-0009', 'afernandez@mail.com', 'Flores 678, CABA',            9,  10, 'GAL-009012',  NULL,                  TRUE),
(10, 'Lucas',    'Herrera',   '32000010', '1993-08-17', 'M', '11-5010-0010', 'lherrera@mail.com',   'Caballito 901, CABA',         32, 17, 'SAN-010123',  NULL,                  TRUE),
(11, 'Valentina','Suárez',    '36100011', '1998-02-28', 'F', '11-5111-0011', 'vsuarez@mail.com',    'Villa Crespo 234, CABA',      17, 4,  'OSDE-011234', 'Embarazada 32 semanas',TRUE),
(12, 'Martín',   'Álvarez',   '24200012', '1982-10-05', 'M', '11-5212-0012', 'malvarez@mail.com',   'Almagro 567, CABA',           38, 15, 'PART-012',    'Hipertenso',          TRUE),
(13, 'Carolina', 'Ruiz',      '29300013', '1989-07-16', 'F', '11-5313-0013', 'cruiz@mail.com',      'Barracas 890, CABA',          13, 13, 'MED-013456',  NULL,                  TRUE),
(14, 'Hernán',   'Castro',    '23400014', '1978-03-09', 'M', '11-5414-0014', 'hcastro@mail.com',    'Núñez 123, CABA',             16, 16, 'AVA-014567',  NULL,                  TRUE),
(15, 'Florencia','Vega',      '34500015', '1994-11-22', 'F', '11-5515-0015', 'fvega@mail.com',      'Palermo Soho 456, CABA',      17, 1,  'OSDE-015678', 'Embarazada 8 semanas', TRUE);

-- ─────────────────────────────────────────────────────────────────────────────
-- TURNOS DE PRUEBA  (semana actual 31/03–05/04/2026 y próxima 06–11/04/2026)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO turnos (id, paciente_id, profesional_id, sucursal_id, estudio_id, obra_social_id, plan_id, fecha_hora, estado, observaciones) VALUES
-- Lunes 31/03
(1,  1,  1, 2, 24, 17, 2,  '2026-03-31 08:00:00', 'ATENDIDO',  'Ecografía obstétrica semana 20'),
(2,  5,  1, 2, 27, 14, 12, '2026-03-31 08:30:00', 'ATENDIDO',  'Translucencia nucal 1er trimestre'),
(3,  2,  2, 1,  1, 36, 6,  '2026-03-31 09:00:00', 'ATENDIDO',  NULL),
(4,  4,  2, 1,  8, 38, 15, '2026-03-31 09:20:00', 'ATENDIDO',  'Control rutina'),
(5,  6,  2, 1, 13, 17, 3,  '2026-03-31 09:40:00', 'CANCELADO', 'Paciente avisó que no podía'),
-- Martes 01/04
(6,  3,  3, 1, 15, 9,  9,  '2026-04-01 10:00:00', 'ATENDIDO',  NULL),
(7,  9,  3, 1, 15, 9, 10,  '2026-04-01 10:30:00', 'ATENDIDO',  'Seguimiento mamario'),
(8,  7,  1, 2, 19, 16, 16, '2026-04-01 08:00:00', 'ATENDIDO',  'Ginecológica de rutina'),
(9, 11,  1, 2, 24, 17, 4,  '2026-04-01 08:30:00', 'ATENDIDO',  'Obs semana 32 - control'),
(10,12,  2, 1,  9, 38, 15, '2026-04-01 09:00:00', 'ATENDIDO',  'Próstata. Paciente hipertenso'),
-- Miércoles 02/04
(11, 8,  4, 2, 25, 36, 7,  '2026-04-02 08:30:00', 'ATENDIDO',  'Scan fetal morfológico'),
(12,13,  3, 1, 16, 13, 13, '2026-04-02 10:00:00', 'ATENDIDO',  NULL),
(13, 1,  2, 1,  1, 17, 2,  '2026-04-02 09:00:00', 'AUSENTE',   'No se presentó'),
-- Jueves 03/04
(14, 5,  1, 2, 26, 14, 12, '2026-04-03 08:00:00', 'ATENDIDO',  'Doppler uterino sem 20'),
(15,15,  1, 2, 27, 17, 1,  '2026-04-03 08:30:00', 'ATENDIDO',  'TN + screening 1er trim'),
(16,14,  3, 1, 17, 16, 16, '2026-04-03 10:00:00', 'ATENDIDO',  NULL),
(17,10,  4, 3, 24, 32, 17, '2026-04-03 08:30:00', 'ATENDIDO',  NULL),
-- Viernes 04/04 (hoy)
(18, 3,  1, 2, 20, 9,  9,  '2026-04-04 08:00:00', 'CONFIRMADO','Transvaginal'),
(19,11,  1, 2, 35, 17, 4,  '2026-04-04 08:30:00', 'CONFIRMADO','Doppler obstétrico sem 32'),
(20, 2,  2, 1,  3, 36, 6,  '2026-04-04 09:00:00', 'PENDIENTE', NULL),
(21, 6,  2, 1, 22, 17, 3,  '2026-04-04 09:20:00', 'PENDIENTE', 'Doppler vasos de cuello'),
(22,13,  3, 1, 15, 13, 13, '2026-04-04 10:00:00', 'PENDIENTE', NULL),
(23, 7,  3, 1, 16, 16, 16, '2026-04-04 10:30:00', 'PENDIENTE', NULL),
(24,15,  4, 2, 25, 17, 1,  '2026-04-04 08:30:00', 'PENDIENTE', 'Scan fetal morfológico sem 20'),
-- Semana próxima — Lunes 06/04
(25, 4,  2, 1,  1, 38, 15, '2026-04-06 09:00:00', 'PENDIENTE', NULL),
(26,12,  2, 1, 21, 38, 15, '2026-04-06 09:20:00', 'PENDIENTE', 'Doppler renal'),
(27, 9,  1, 2, 19, 9,  10, '2026-04-06 08:00:00', 'PENDIENTE', NULL),
(28,10,  4, 2, 24, 32, 17, '2026-04-06 08:30:00', 'PENDIENTE', 'Obstétrica 28 semanas'),
-- Martes 07/04
(29, 1,  3, 1, 15, 17, 2,  '2026-04-07 10:00:00', 'PENDIENTE', 'Mamaria bilateral control'),
(30, 8,  1, 2, 24, 36, 7,  '2026-04-07 08:00:00', 'PENDIENTE', NULL),
(31,14,  2, 1,  8, 16, 16, '2026-04-07 09:00:00', 'PENDIENTE', 'Renal bilateral'),
(32,15,  1, 2, 31, 17, 1,  '2026-04-07 08:30:00', 'PENDIENTE', 'Ecocardiograma fetal'),
-- Miércoles 08/04
(33, 5,  1, 2, 24, 14, 12, '2026-04-08 08:00:00', 'PENDIENTE', 'Obstétrica sem 21'),
(34,13,  3, 1, 32, 13, 13, '2026-04-08 10:00:00', 'PENDIENTE', 'Punción mamaria guiada'),
(35, 6,  2, 1, 13, 17, 3,  '2026-04-08 09:00:00', 'PENDIENTE', NULL),
(36,11,  4, 2, 35, 17, 4,  '2026-04-08 08:30:00', 'PENDIENTE', 'Doppler obstétrico sem 33'),
-- Jueves 09/04
(37, 2,  4, 3, 20, 36, 6,  '2026-04-09 08:30:00', 'PENDIENTE', NULL),
(38, 3,  3, 1, 15, 9,  9,  '2026-04-09 10:00:00', 'PENDIENTE', NULL),
(39,12,  2, 1,  9, 38, 15, '2026-04-09 09:00:00', 'PENDIENTE', NULL),
-- Viernes 10/04
(40, 7,  1, 2, 26, 16, 16, '2026-04-10 08:00:00', 'PENDIENTE', 'Doppler arterias uterinas'),
(41,14,  2, 3, 17, 16, 16, '2026-04-10 09:00:00', 'PENDIENTE', 'Tiroides'),
(42,10,  3, 1, 16, 32, 17, '2026-04-10 10:00:00', 'PENDIENTE', NULL);

-- ─────────────────────────────────────────────────────────────────────────────

-- Estudios (del Excel - listado completo de procedimientos)
INSERT IGNORE INTO estudios (id, nombre, codigo) VALUES
(1,  'Ecografía Abdominal',                         'ECO-ABD'),
(2,  'Doppler Abdominal',                           'DOP-ABD'),
(3,  'Ecografía de Partes Blandas',                 'ECO-PB'),
(4,  'Ecografía Testicular',                        'ECO-TEST'),
(5,  'Doppler Testicular',                          'DOP-TEST'),
(6,  'Ecografía de Pene / Cuerpo Cavernoso',        'ECO-PEN'),
(7,  'Doppler de Pene',                             'DOP-PEN'),
(8,  'Ecografía Renal',                             'ECO-REN'),
(9,  'Ecografía Vesicoprostática',                  'ECO-VP'),
(10, 'Ecografía Pancreática',                       'ECO-PANC'),
(11, 'Ecografía Renovesicoprostática',              'ECO-RVP'),
(12, 'Ecografía Renovesicoprostática con Jets',     'ECO-RVPJ'),
(13, 'Ecografía Hepática',                          'ECO-HEP'),
(14, 'Doppler Hepático',                            'DOP-HEP'),
(15, 'Ecografía Mamaria Bilateral',                 'ECO-MAM'),
(16, 'Ecografía Mamaria con Doppler',               'ECO-MAM-D'),
(17, 'Ecografía de Tiroides',                       'ECO-TIR'),
(18, 'Doppler de Tiroides',                         'DOP-TIR'),
(19, 'Ecografía Ginecológica / Transabdominal',     'ECO-GIN'),
(20, 'Ecografía Transvaginal / Intravaginal',       'ECO-TV'),
(21, 'Doppler Renal',                               'DOP-REN'),
(22, 'Doppler de Vaso de Cuello',                   'DOP-CUE'),
(23, 'Doppler de Miembros Inferiores',              'DOP-MMII'),
(24, 'Ecografía Obstétrica',                        'ECO-OBS'),
(25, 'Scan Fetal',                                  'SCAN-FET'),
(26, 'Doppler de Arterias Uterinas',                'DOP-UTER'),
(27, 'Translucencia Nucal / Screening 1er Trim.',   'TN-SCR'),
(28, 'Ecografía de Caderas Pediátricas',            'ECO-CAD'),
(29, 'Ecografía Lumbosacra',                        'ECO-LUMB'),
(30, 'Ecografía Cerebral / Transfontanelar',        'ECO-CER'),
(31, 'Ecocardiograma Fetal Doppler',                'ECO-CARD-F'),
(32, 'Punción Mamaria',                             'PUN-MAM'),
(33, 'Ecografía Articular',                         'ECO-ART'),
(34, 'Ecografía de Hombro (Manguito Rotador)',      'ECO-HOM'),
(35, 'Doppler Obstétrico',                          'DOP-OBS');
