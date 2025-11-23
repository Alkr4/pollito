<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!isset($_GET['id_departamento'])) {
    echo json_encode([]);
    exit();
}

$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);

$sql = "SELECT * FROM v_historial_reciente 
        WHERE depto_numero IN (
            SELECT numero FROM departamentos d 
            INNER JOIN usuarios u ON d.id=u.id_departamento 
            WHERE u.id_departamento='$id_departamento'
        )
        ORDER BY fecha_hora DESC 
        LIMIT 50";

$result = mysqli_query($cont, $sql);
$arr = array();
while ($datos = mysqli_fetch_assoc($result)) {
    $arr[] = $datos;
}
echo json_encode($arr);
mysqli_close($cont);
?>