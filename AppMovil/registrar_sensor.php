<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

// Receive parameters
$id_usuario = $_GET['id_usuario']; // Logged admin ID
$id_departamento = $_GET['id_departamento'];
$mac = $_GET['MAC']; // The UID read from card
$tipo = $_GET['tipo']; // 'Llavero' or 'Tarjeta'

// Check duplicate
$check = "SELECT id FROM sensores WHERE MAC_UID = '$mac'";
if(mysqli_num_rows(mysqli_query($cont, $check)) > 0){
    echo json_encode(['status'=>'error', 'message'=>'Sensor ya existe']);
    exit();
}

$fecha = date('Y-m-d');
$sql = "INSERT INTO sensores (id_usuario, id_departamento, MAC_UID, tipo, activo, fecha_alta) 
        VALUES ('$id_usuario', '$id_departamento', '$mac', '$tipo', 'ACTIVO', '$fecha')";

if (mysqli_query($cont, $sql)) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'error', 'message' => mysqli_error($cont)]);
}
mysqli_close($cont);
?>