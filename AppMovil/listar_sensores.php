<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if(!isset($_GET['id_departamento'])){
    echo json_encode(['status'=>'error', 'message'=>'Falta id_departamento']);
    exit();
}

$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);

// Usar la vista
$sql = "SELECT * FROM v_sensores_activos WHERE id_departamento='$id_departamento'";
$result = mysqli_query($cont, $sql);

$arr = array();
while($datos = mysqli_fetch_assoc($result)){
    $arr[] = $datos;
}

echo json_encode(['status'=>'success', 'sensores'=>$arr]);
mysqli_close($cont);
?>