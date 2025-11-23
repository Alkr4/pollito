<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$sql = "SELECT id, numero, torre, piso, condominio FROM departamentos ORDER BY torre, numero";
$result = mysqli_query($cont, $sql);

$departamentos = array();
while($row = mysqli_fetch_assoc($result)){
    $departamentos[] = $row;
}

echo json_encode(['status'=>'success', 'departamentos'=>$departamentos]);
mysqli_close($cont);
?>