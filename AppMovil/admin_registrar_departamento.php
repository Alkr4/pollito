<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$numero = $_GET['numero']; // e.g. 101
$torre = $_GET['torre'];   // e.g. A
$piso = $_GET['piso'];     // e.g. 1

$sql = "INSERT INTO departamentos (numero, torre, piso, condominio) VALUES ('$numero', '$torre', '$piso', 'Condominio 1')";

if(mysqli_query($cont, $sql)){
    echo json_encode(['status'=>'success']);
} else {
    echo json_encode(['status'=>'error', 'message'=>mysqli_error($cont)]);
}
mysqli_close($cont);
?>