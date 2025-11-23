-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 23, 2025 at 06:55 PM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `appmovil`
--

DELIMITER $$
--
-- Procedures
--
DROP PROCEDURE IF EXISTS `sp_registrar_acceso`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_acceso` (IN `p_codigo_sensor` VARCHAR(50), OUT `p_resultado` VARCHAR(20), OUT `p_id_sensor` INT)   BEGIN
  DECLARE v_id_sensor INT;
  DECLARE v_id_usuario INT;
  DECLARE v_estado VARCHAR(20);
  
  -- Buscar el sensor
  SELECT id, id_usuario, estado 
  INTO v_id_sensor, v_id_usuario, v_estado
  FROM sensores 
  WHERE codigo_sensor = p_codigo_sensor
  LIMIT 1;
  
  IF v_id_sensor IS NULL THEN
    -- Sensor no registrado
    SET p_resultado = 'DENEGADO';
    SET p_id_sensor = NULL;
    INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles)
    VALUES (NULL, NULL, 'SENSOR_NO_REGISTRADO', 'DENEGADO', CONCAT('Código: ', p_codigo_sensor));
  ELSEIF v_estado != 'ACTIVO' THEN
    -- Sensor inactivo/bloqueado
    SET p_resultado = 'DENEGADO';
    SET p_id_sensor = v_id_sensor;
    INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles)
    VALUES (v_id_sensor, v_id_usuario, 'ACCESO_RECHAZADO', 'DENEGADO', CONCAT('Estado: ', v_estado));
  ELSE
    -- Acceso permitido
    SET p_resultado = 'PERMITIDO';
    SET p_id_sensor = v_id_sensor;
    INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles)
    VALUES (v_id_sensor, v_id_usuario, 'ACCESO_VALIDO', 'PERMITIDO', 'Acceso normal');
  END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
CREATE TABLE IF NOT EXISTS `auditoria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_sensor` int DEFAULT NULL COMMENT 'NULL si es acción manual desde app',
  `id_usuario` int DEFAULT NULL,
  `tipo_uso` enum('ACCESO_VALIDO','ACCESO_RECHAZADO','APERTURA_MANUAL','CIERRE_MANUAL','SENSOR_DESACTIVADO','SENSOR_NO_REGISTRADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `autorizado` enum('PERMITIDO','DENEGADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `detalles` text COLLATE utf8mb4_unicode_ci COMMENT 'Información adicional del evento',
  PRIMARY KEY (`id`),
  KEY `idx_sensor` (`id_sensor`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_fecha` (`fecha_hora`),
  KEY `idx_tipo` (`tipo_uso`),
  KEY `idx_fecha_tipo` (`fecha_hora`,`tipo_uso`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `auditoria`
--

INSERT INTO `auditoria` (`id`, `id_sensor`, `id_usuario`, `tipo_uso`, `fecha_hora`, `autorizado`, `detalles`) VALUES
(1, 1, 1, 'ACCESO_VALIDO', '2025-11-23 16:20:59', 'PERMITIDO', 'Acceso normal'),
(2, 2, 1, 'ACCESO_VALIDO', '2025-11-23 17:20:59', 'PERMITIDO', 'Acceso normal'),
(3, NULL, 1, 'APERTURA_MANUAL', '2025-11-23 17:50:59', 'PERMITIDO', 'Apertura desde app'),
(4, 3, 2, 'ACCESO_VALIDO', '2025-11-23 18:05:59', 'PERMITIDO', 'Acceso normal');

-- --------------------------------------------------------

--
-- Table structure for table `control_barrera`
--

DROP TABLE IF EXISTS `control_barrera`;
CREATE TABLE IF NOT EXISTS `control_barrera` (
  `id` int NOT NULL AUTO_INCREMENT,
  `estado_actual` enum('ABIERTA','CERRADA','EN_MOVIMIENTO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CERRADA',
  `ultimo_comando` enum('ABRIR','CERRAR','NINGUNO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NINGUNO',
  `id_usuario_comando` int DEFAULT NULL COMMENT 'Usuario que ejecutó el último comando manual',
  `fecha_ultimo_cambio` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_barrera_usuario` (`id_usuario_comando`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `control_barrera`
--

INSERT INTO `control_barrera` (`id`, `estado_actual`, `ultimo_comando`, `id_usuario_comando`, `fecha_ultimo_cambio`) VALUES
(1, 'CERRADA', 'NINGUNO', NULL, '2025-11-23 18:20:59');

-- --------------------------------------------------------

--
-- Table structure for table `departamentos`
--

DROP TABLE IF EXISTS `departamentos`;
CREATE TABLE IF NOT EXISTS `departamentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `torre` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `piso` int NOT NULL,
  `condominio` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Condominio Principal',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_departamento` (`numero`,`torre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `departamentos`
--

INSERT INTO `departamentos` (`id`, `numero`, `torre`, `piso`, `condominio`, `fecha_registro`) VALUES
(1, '101', 'A', 1, 'Condominio Principal', '2025-11-23 18:20:59'),
(2, '102', 'A', 1, 'Condominio Principal', '2025-11-23 18:20:59'),
(3, '201', 'A', 2, 'Condominio Principal', '2025-11-23 18:20:59'),
(4, '101', 'B', 1, 'Condominio Principal', '2025-11-23 18:20:59');

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

DROP TABLE IF EXISTS `password_resets`;
CREATE TABLE IF NOT EXISTS `password_resets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` bigint NOT NULL COMMENT 'Unix timestamp',
  `estado` enum('NO_USADO','USADO','EXPIRADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NO_USADO',
  `fecha_solicitud` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`),
  KEY `idx_token` (`token`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Triggers `password_resets`
--
DROP TRIGGER IF EXISTS `tr_password_reset_usado`;
DELIMITER $$
CREATE TRIGGER `tr_password_reset_usado` AFTER UPDATE ON `password_resets` FOR EACH ROW BEGIN
  IF NEW.estado = 'USADO' THEN
    UPDATE password_resets 
    SET estado = 'EXPIRADO' 
    WHERE email = NEW.email 
      AND id != NEW.id 
      AND estado = 'NO_USADO';
  END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `sensores`
--

DROP TABLE IF EXISTS `sensores`;
CREATE TABLE IF NOT EXISTS `sensores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_sensor` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UID/MAC de la tarjeta RFID',
  `tipo` enum('Tarjeta','Llavero') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Tarjeta',
  `id_usuario` int NOT NULL COMMENT 'Usuario propietario',
  `id_departamento` int NOT NULL,
  `estado` enum('ACTIVO','INACTIVO','PERDIDO','BLOQUEADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `fecha_alta` date NOT NULL,
  `fecha_baja` date DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_sensor` (`codigo_sensor`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_departamento` (`id_departamento`),
  KEY `idx_estado` (`estado`),
  KEY `idx_codigo_estado` (`codigo_sensor`,`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sensores`
--

INSERT INTO `sensores` (`id`, `codigo_sensor`, `tipo`, `id_usuario`, `id_departamento`, `estado`, `fecha_alta`, `fecha_baja`, `fecha_registro`, `fecha_modificacion`) VALUES
(1, '0A:31:B0:1A', 'Tarjeta', 1, 1, 'ACTIVO', '2025-11-23', NULL, '2025-11-23 18:20:59', NULL),
(2, '33:52:5E:F7', 'Llavero', 1, 1, 'ACTIVO', '2025-11-23', NULL, '2025-11-23 18:20:59', NULL),
(3, 'AA:BB:CC:DD', 'Tarjeta', 2, 1, 'ACTIVO', '2025-11-23', NULL, '2025-11-23 18:20:59', NULL),
(4, '11:22:33:44', 'Llavero', 3, 2, 'ACTIVO', '2025-11-23', NULL, '2025-11-23 18:20:59', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rut` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_departamento` int NOT NULL,
  `privilegios` enum('administrador','operador') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'operador',
  `estado` enum('ACTIVO','INACTIVO','BLOQUEADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `rut` (`rut`),
  KEY `idx_departamento` (`id_departamento`),
  KEY `idx_estado` (`estado`),
  KEY `idx_dept_estado` (`id_departamento`,`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `apellido`, `email`, `password_hash`, `rut`, `telefono`, `id_departamento`, `privilegios`, `estado`, `fecha_registro`, `fecha_modificacion`) VALUES
(1, 'Juan', 'Pérez', 'juan.perez@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '12345678-9', '912345678', 1, 'administrador', 'ACTIVO', '2025-11-23 18:20:59', NULL),
(2, 'María', 'González', 'maria.gonzalez@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '98765432-1', '987654321', 1, 'operador', 'ACTIVO', '2025-11-23 18:20:59', NULL),
(3, 'Pedro', 'López', 'pedro.lopez@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11223344-5', '911223344', 2, 'administrador', 'ACTIVO', '2025-11-23 18:20:59', NULL);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_historial_reciente`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `v_historial_reciente`;
CREATE TABLE IF NOT EXISTS `v_historial_reciente` (
`autorizado` enum('PERMITIDO','DENEGADO')
,`codigo_sensor` varchar(50)
,`depto_numero` varchar(10)
,`depto_torre` varchar(10)
,`detalles` text
,`fecha_hora` datetime
,`id` int
,`sensor_tipo` enum('Tarjeta','Llavero')
,`tipo_uso` enum('ACCESO_VALIDO','ACCESO_RECHAZADO','APERTURA_MANUAL','CIERRE_MANUAL','SENSOR_DESACTIVADO','SENSOR_NO_REGISTRADO')
,`usuario_apellido` varchar(100)
,`usuario_nombre` varchar(100)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_sensores_activos`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `v_sensores_activos`;
CREATE TABLE IF NOT EXISTS `v_sensores_activos` (
`codigo_sensor` varchar(50)
,`depto_numero` varchar(10)
,`depto_torre` varchar(10)
,`estado` enum('ACTIVO','INACTIVO','PERDIDO','BLOQUEADO')
,`fecha_alta` date
,`id` int
,`tipo` enum('Tarjeta','Llavero')
,`usuario_apellido` varchar(100)
,`usuario_email` varchar(200)
,`usuario_nombre` varchar(100)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_usuarios_departamento`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `v_usuarios_departamento`;
CREATE TABLE IF NOT EXISTS `v_usuarios_departamento` (
`apellido` varchar(100)
,`depto_numero` varchar(10)
,`depto_piso` int
,`depto_torre` varchar(10)
,`email` varchar(200)
,`estado` enum('ACTIVO','INACTIVO','BLOQUEADO')
,`id` int
,`id_departamento` int
,`nombre` varchar(100)
,`privilegios` enum('administrador','operador')
,`rut` varchar(12)
,`sensores_activos` bigint
,`telefono` varchar(15)
);

-- --------------------------------------------------------

--
-- Structure for view `v_historial_reciente`
--
DROP TABLE IF EXISTS `v_historial_reciente`;

DROP VIEW IF EXISTS `v_historial_reciente`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_historial_reciente`  AS SELECT `a`.`id` AS `id`, `a`.`fecha_hora` AS `fecha_hora`, `a`.`tipo_uso` AS `tipo_uso`, `a`.`autorizado` AS `autorizado`, `a`.`detalles` AS `detalles`, `s`.`codigo_sensor` AS `codigo_sensor`, `s`.`tipo` AS `sensor_tipo`, `u`.`nombre` AS `usuario_nombre`, `u`.`apellido` AS `usuario_apellido`, `d`.`numero` AS `depto_numero`, `d`.`torre` AS `depto_torre` FROM (((`auditoria` `a` left join `sensores` `s` on((`a`.`id_sensor` = `s`.`id`))) left join `usuarios` `u` on((`a`.`id_usuario` = `u`.`id`))) left join `departamentos` `d` on((`u`.`id_departamento` = `d`.`id`))) ORDER BY `a`.`fecha_hora` DESC ;

-- --------------------------------------------------------

--
-- Structure for view `v_sensores_activos`
--
DROP TABLE IF EXISTS `v_sensores_activos`;

DROP VIEW IF EXISTS `v_sensores_activos`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_sensores_activos`  AS SELECT `s`.`id` AS `id`, `s`.`codigo_sensor` AS `codigo_sensor`, `s`.`tipo` AS `tipo`, `s`.`estado` AS `estado`, `u`.`nombre` AS `usuario_nombre`, `u`.`apellido` AS `usuario_apellido`, `u`.`email` AS `usuario_email`, `d`.`numero` AS `depto_numero`, `d`.`torre` AS `depto_torre`, `s`.`fecha_alta` AS `fecha_alta` FROM ((`sensores` `s` join `usuarios` `u` on((`s`.`id_usuario` = `u`.`id`))) join `departamentos` `d` on((`s`.`id_departamento` = `d`.`id`))) WHERE (`s`.`estado` = 'ACTIVO') ;

-- --------------------------------------------------------

--
-- Structure for view `v_usuarios_departamento`
--
DROP TABLE IF EXISTS `v_usuarios_departamento`;

DROP VIEW IF EXISTS `v_usuarios_departamento`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_usuarios_departamento`  AS SELECT `u`.`id` AS `id`, `u`.`nombre` AS `nombre`, `u`.`apellido` AS `apellido`, `u`.`email` AS `email`, `u`.`rut` AS `rut`, `u`.`telefono` AS `telefono`, `u`.`privilegios` AS `privilegios`, `u`.`estado` AS `estado`, `u`.`id_departamento` AS `id_departamento`, `d`.`numero` AS `depto_numero`, `d`.`torre` AS `depto_torre`, `d`.`piso` AS `depto_piso`, (select count(0) from `sensores` where ((`sensores`.`id_usuario` = `u`.`id`) and (`sensores`.`estado` = 'ACTIVO'))) AS `sensores_activos` FROM (`usuarios` `u` join `departamentos` `d` on((`u`.`id_departamento` = `d`.`id`))) ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `auditoria`
--
ALTER TABLE `auditoria`
  ADD CONSTRAINT `fk_auditoria_sensor` FOREIGN KEY (`id_sensor`) REFERENCES `sensores` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `control_barrera`
--
ALTER TABLE `control_barrera`
  ADD CONSTRAINT `fk_barrera_usuario` FOREIGN KEY (`id_usuario_comando`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `sensores`
--
ALTER TABLE `sensores`
  ADD CONSTRAINT `fk_sensores_departamento` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_sensores_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `fk_usuarios_departamento` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
