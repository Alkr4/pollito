<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit();
}

$id_sensor = mysqli_real_escape_string($cont, $_GET['id_sensor']);
$nuevo_estado = mysqli_real_escape_string($cont, $_GET['nuevo_estado']);
$id_usuario = mysqli_real_escape_string($cont, $_GET['id_usuario']);

$estados_validos = ['ACTIVO', 'INACTIVO', 'PERDIDO', 'BLOQUEADO'];
if (!in_array($nuevo_estado, $estados_validos)) {
    echo json_encode(['status' => 'error', 'message' => 'Estado no válido']);
    exit();
}

$check_admin = "SELECT privilegios FROM usuarios WHERE id='$id_usuario'";
$result = mysqli_query($cont, $check_admin);
$user = mysqli_fetch_assoc($result);

if ($user['privilegios'] != 'administrador') {
    echo json_encode(['status' => 'error', 'message' => 'Sin permisos']);
    exit();
}

$sql_update = "UPDATE sensores SET estado = '$nuevo_estado' WHERE id = '$id_sensor'";

if (mysqli_query($cont, $sql_update)) {
    if (mysqli_affected_rows($cont) > 0) {
        $sql_audit = "INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles) 
                      VALUES ('$id_sensor', '$id_usuario', 'SENSOR_DESACTIVADO', 'PERMITIDO', 
                      'Cambio de estado a $nuevo_estado')";
        mysqli_query($cont, $sql_audit);
        
        echo json_encode(['status' => 'success', 'message' => 'Estado del sensor actualizado.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'No se encontró el sensor con ese ID.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error al actualizar el estado: ' . mysqli_error($cont)]);
}

mysqli_close($cont);
?>