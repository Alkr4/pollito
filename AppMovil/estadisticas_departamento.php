<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);

// Total usuarios
$sql_users = "SELECT COUNT(*) as total FROM usuarios WHERE id_departamento='$id_departamento'";
$total_users = mysqli_fetch_assoc(mysqli_query($cont, $sql_users))['total'];

// Total sensores activos
$sql_sensors = "SELECT COUNT(*) as total FROM sensores 
                WHERE id_departamento='$id_departamento' AND estado='ACTIVO'";
$total_sensors = mysqli_fetch_assoc(mysqli_query($cont, $sql_sensors))['total'];

// Accesos hoy
$sql_accesos = "SELECT COUNT(*) as total FROM auditoria a
                INNER JOIN sensores s ON a.id_sensor=s.id
                WHERE s.id_departamento='$id_departamento' 
                AND DATE(a.fecha_hora) = CURDATE()";
$total_accesos = mysqli_fetch_assoc(mysqli_query($cont, $sql_accesos))['total'];

echo json_encode([
    'status'=>'success',
    'usuarios'=>$total_users,
    'sensores'=>$total_sensors,
    'accesos_hoy'=>$total_accesos
]);

mysqli_close($cont);
?>