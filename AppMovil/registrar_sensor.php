<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$id_usuario = $_GET['id_usuario'];
$id_departamento = $_GET['id_departamento'];
$codigo_sensor = mysqli_real_escape_string($cont, $_GET['codigo_sensor']);
$tipo = $_GET['tipo'];

$check = "SELECT id FROM sensores WHERE codigo_sensor = '$codigo_sensor'";
if(mysqli_num_rows(mysqli_query($cont, $check)) > 0){
    echo json_encode(['status'=>'error', 'message'=>'Sensor ya existe']);
    exit();
}

$fecha = date('Y-m-d');
$sql = "INSERT INTO sensores (id_usuario, id_departamento, codigo_sensor, tipo, estado, fecha_alta) 
        VALUES ('$id_usuario', '$id_departamento', '$codigo_sensor', '$tipo', 'ACTIVO', '$fecha')";

if (mysqli_query($cont, $sql)) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'error', 'message' => mysqli_error($cont)]);
}
mysqli_close($cont);
?>