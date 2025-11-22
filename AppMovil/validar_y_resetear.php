<?php

$db_host = 'localhost';
$db_user = 'chris';
$db_pass = 'Admin123';
$db_name = 'AppMovil';


header('Content-Type: application/json');
$conexion = mysqli_connect($db_host, $db_user, $db_pass, $db_name);

if (!$conexion) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión.']);
    exit();
}

if (!isset($_GET['email']) || !isset($_GET['codigo']) || !isset($_GET['password'])) {
    echo json_encode(['status' => 'error', 'message' => 'Datos incompletos.']);
    exit();
}

$email = mysqli_real_escape_string($conexion, $_GET['email']);
$codigo = mysqli_real_escape_string($conexion, $_GET['codigo']);
$password = mysqli_real_escape_string($conexion, $_GET['password']);
$current_time = time();

$sql_check = "SELECT * FROM password_resets WHERE email = '$email' AND token = '$codigo' AND estado = 'NO USADO'";
$result = mysqli_query($conexion, $sql_check);

if (mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    if ($current_time > $row['expires_at']) {
        echo json_encode(['status' => 'error', 'message' => 'El código ha expirado.']);
        exit();
    }

    $sql_update = "UPDATE usuarios SET password = '$password' WHERE email = '$email'";
    if (mysqli_query($conexion, $sql_update)) {
        $sql_delete = "DELETE FROM password_resets WHERE email = '$email'";
        mysqli_query($conexion, $sql_delete);
        echo json_encode(['status' => 'success', 'message' => '¡Contraseña actualizada con éxito!']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Error al actualizar la contraseña.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Código incorrecto.']);
}
mysqli_close($conexion);
?>
