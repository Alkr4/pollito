<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!isset($_GET['id_departamento'])) {
    echo json_encode([]);
    exit();
}

$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);

// This query tries to link events to the sensors of a specific department
$sql = "SELECT h.fecha_hora, h.tipo_uso, h.autorizado 
        FROM historial_sensores h
        LEFT JOIN sensores s ON h.id_sensor = s.id
        WHERE s.id_departamento = '$id_departamento' OR h.id_sensor IS NULL
        ORDER BY h.fecha_hora DESC LIMIT 20";

$result = mysqli_query($cont, $sql);
$arr = array();
while ($datos = mysqli_fetch_assoc($result)) {
    $arr[] = $datos;
}
echo json_encode($arr);
mysqli_close($cont);
?>