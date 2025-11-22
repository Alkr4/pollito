<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$sql = "SELECT id, nombre, rut FROM usuarios WHERE estado='ACTIVO'";
$result = mysqli_query($cont, $sql);

$data = [];
while($row = mysqli_fetch_assoc($result)){
    $data[] = $row;
}
echo json_encode($data);
mysqli_close($cont);
?>