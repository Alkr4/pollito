<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit();
}

$nombre = mysqli_real_escape_string($cont, $_GET['name']);
$apellido = mysqli_real_escape_string($cont, $_GET['lastname']);
$email = mysqli_real_escape_string($cont, $_GET['email']);
$telefono = mysqli_real_escape_string($cont, $_GET['phone']);
$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);
$estado_inicial = 'activo';
$clave_plana = mysqli_real_escape_string($cont, $_GET['password']);
$rut = mysqli_real_escape_string($cont, $_GET['rut']);
$clave_hash = password_hash($clave_plana, PASSWORD_DEFAULT);

$sql_check_email = "SELECT email FROM usuarios WHERE email = '$email'";
$result_check = mysqli_query($cont, $sql_check_email);

if (mysqli_num_rows($result_check) > 0) {
    echo json_encode(['status' => 'error', 'message' => 'El correo electrónico ya se encuentra registrado.']);
    exit();
}

$sql_check_dept = "SELECT COUNT(*) as total FROM usuarios WHERE id_departamento = '$id_departamento'";
$result_dept = mysqli_query($cont, $sql_check_dept);
$row_dept = mysqli_fetch_assoc($result_dept);
$rol_usuario = ($row_dept['total'] == 0) ? 'administrador' : 'operador';

$sql_insert = "INSERT INTO usuarios (nombre, apellido, email, telefono, password_hash, id_departamento, privilegios, estado, rut) 
               VALUES ('$nombre', '$apellido', '$email', '$telefono', '$clave_hash', '$id_departamento', '$rol_usuario', '$estado_inicial', '$rut')";

if (mysqli_query($cont, $sql_insert)) {
    echo json_encode(['status' => 'success', 'message' => 'Usuario registrado correctamente como ' . $rol_usuario]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error al registrar el usuario: ' . mysqli_error($cont)]);
}

mysqli_close($cont);
?>