<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$accion = $_GET['accion'];
$id_usuario = $_GET['id_usuario']; 
$fecha = date('Y-m-d H:i:s');

// Map action to 'tipo_uso'
$tipo_uso = ($accion == "ABRIR") ? "APERTURA_MANUAL" : "CIERRE_MANUAL";
$resultado = "PERMITIDO";

// Note: historial_sensores in your SQL does NOT have 'id_usuario'. 
// It only has: id, id_sensor, fecha_hora, tipo_uso, autorizado.
// We will insert NULL for id_sensor since this is an App event.

$sql = "INSERT INTO historial_sensores (id_sensor, fecha_hora, tipo_uso, autorizado) 
        VALUES (NULL, '$fecha', '$tipo_uso', '$resultado')";

if (mysqli_query($cont, $sql)) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'error', 'message' => mysqli_error($cont)]);
}
mysqli_close($cont);
?>