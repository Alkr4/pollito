<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión']);
    exit();
}

$id_sensor = mysqli_real_escape_string($cont, $_GET['id_sensor']);
$id_usuario = mysqli_real_escape_string($cont, $_GET['id_usuario']);

// Verificar permisos de administrador
$check_admin = "SELECT privilegios FROM usuarios WHERE id='$id_usuario'";
$result = mysqli_query($cont, $check_admin);
$user = mysqli_fetch_assoc($result);

if ($user['privilegios'] != 'administrador') {
    echo json_encode(['status' => 'error', 'message' => 'Sin permisos de administrador']);
    exit();
}

// Obtener info del sensor antes de eliminar
$sql_info = "SELECT codigo_sensor, tipo FROM sensores WHERE id='$id_sensor'";
$result_info = mysqli_query($cont, $sql_info);
$sensor_info = mysqli_fetch_assoc($result_info);

if (!$sensor_info) {
    echo json_encode(['status' => 'error', 'message' => 'Sensor no encontrado']);
    exit();
}

// Eliminar sensor
$sql_delete = "DELETE FROM sensores WHERE id='$id_sensor'";

if (mysqli_query($cont, $sql_delete)) {
    // Registrar en auditoría
    $sql_audit = "INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles) 
                  VALUES (NULL, '$id_usuario', 'SENSOR_DESACTIVADO', 'PERMITIDO', 
                  'Sensor eliminado: {$sensor_info['codigo_sensor']} ({$sensor_info['tipo']})')";
    mysqli_query($cont, $sql_audit);
    
    echo json_encode([
        'status' => 'success', 
        'message' => 'Sensor eliminado correctamente'
    ]);
} else {
    echo json_encode([
        'status' => 'error', 
        'message' => 'Error al eliminar: ' . mysqli_error($cont)
    ]);
}

mysqli_close($cont);
?>