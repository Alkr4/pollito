<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit();
}
$numero = mysqli_real_escape_string($cont, $_GET['numero']);
$torre = mysqli_real_escape_string($cont, $_GET['torre']);
$piso = mysqli_real_escape_string($cont, $_GET['piso']);
$condominio = mysqli_real_escape_string($cont, $_GET['condominio']);    
    $sql_insert = "INSERT INTO departamentos (numero, torre, piso, condominio) 
                   VALUES ('$numero', '$torre', '$piso', '$condominio')";

    if (mysqli_query($cont, $sql_insert)) {
        echo json_encode(['status' => 'success', 'message' => 'Departamento registrado correctamente.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Error al registrar el departamento: ' . mysqli_error($cont)]);
    }


mysqli_close($cont);
?>