<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit();
}

$id_sensor = mysqli_real_escape_string($cont, $_GET['id_sensor']);
$nuevo_estado = mysqli_real_escape_string($cont, $_GET['nuevo_estado']);

$sql_update = "UPDATE SENSORES SET estado = '$nuevo_estado' WHERE id_sensor = '$id_sensor'";

if (mysqli_query($cont, $sql_update)) {
    if (mysqli_affected_rows($cont) > 0) {
        echo json_encode(['status' => 'success', 'message' => 'Estado del sensor actualizado.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'No se encontró el sensor con ese ID.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error al actualizar el estado: ' . mysqli_error($cont)]);
}

mysqli_close($cont);
?>