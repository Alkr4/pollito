<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

// Leer de la nueva tabla control_barrera
$sql = "SELECT estado_actual, ultimo_comando, fecha_ultimo_cambio 
        FROM control_barrera WHERE id=1";

$result = mysqli_query($cont, $sql);

if($row = mysqli_fetch_assoc($result)){
    echo json_encode([
        'status' => 'success',
        'estado' => $row['estado_actual'],
        'ultimo_comando' => $row['ultimo_comando'],
        'fecha' => $row['fecha_ultimo_cambio']
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'No se pudo leer estado de barrera'
    ]);
}

mysqli_close($cont);
?>