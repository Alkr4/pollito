<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['acceso' => 'DENEGADO', 'motivo' => 'Error de BD']);
    exit();
}

$codigo_sensor = mysqli_real_escape_string($cont, $_GET['codigo_sensor']);

$id_sensor_log = null;
$id_usuario_log = null;
$tipo_evento = 'ACCESO_RECHAZADO';
$resultado_log = 'DENEGADO';

$sql_check = "SELECT id, id_usuario, estado FROM SENSORES WHERE id = '$codigo_sensor'";
$result_check = mysqli_query($cont, $sql_check);

if (mysqli_num_rows($result_check) > 0) {
    $sensor = mysqli_fetch_assoc($result_check);
    $id_sensor_log = $sensor['id'];
    $id_usuario_log = $sensor['id_usuario'];
    $estado_sensor = $sensor['estado'];

    if ($estado_sensor == 'ACTIVO') {
        $tipo_evento = 'ACCESO_VALIDO';
        $resultado_log = 'PERMITIDO';
        echo json_encode([
            'acceso' => 'PERMITIDO',
            'id' => $id_sensor_log
        ]);
    } else {
        $resultado_log = 'DENEGADO (Sensor ' . $estado_sensor . ')';
        echo json_encode([
            'acceso' => 'DENEGADO', 
            'motivo' => 'Sensor ' . $estado_sensor,
            'id' => $id_sensor_log
        ]);
    }

} else {
    $resultado_log = 'DENEGADO (Sensor no registrado)';
    echo json_encode([
        'acceso' => 'DENEGADO',
        'motivo' => 'Sensor no registrado',
        'id' => null
    ]);
}

$fecha_hora = date('Y-m-d H:i:s');
$sql_evento = "INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, fecha_hora, autorizado) 
               VALUES ('$id_sensor_log', '$id_usuario_log', '$tipo_evento', '$fecha_hora', '$resultado_log')";

mysqli_query($cont, $sql_evento);

mysqli_close($cont);
?>