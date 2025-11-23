<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$nombre = $_GET['nombre'];
$email = $_GET['email'];
$password = password_hash($_GET['password'], PASSWORD_DEFAULT);
$rut = $_GET['rut'];
$telefono = $_GET['telefono'];
$id_dept = $_GET['id_departamento'];
$rol = $_GET['rol'];
$estado = isset($_GET['estado']) ? $_GET['estado'] : 'ACTIVO';

$id_admin = mysqli_real_escape_string($cont, $_GET['id_admin']);
$check_admin = "SELECT privilegios FROM usuarios WHERE id='$id_admin' AND privilegios='administrador'";
if(mysqli_num_rows(mysqli_query($cont, $check_admin)) == 0){
    echo json_encode(['status'=>'error', 'message'=>'Sin permisos']);
    exit();
}

$check = mysqli_query($cont, "SELECT id FROM usuarios WHERE email='$email' OR rut='$rut'");
if(mysqli_num_rows($check) > 0){
    echo json_encode(['status'=>'error', 'message'=>'El Correo o RUT ya existe']);
    exit();
}

// 2. Insert
$sql = "INSERT INTO usuarios (nombre, email, password_hash, rut, telefono, id_departamento, privilegios, estado) 
        VALUES ('$nombre', '$email', '$password', '$rut', '$telefono', '$id_dept', '$rol', 'ACTIVO')";

if(mysqli_query($cont, $sql)){
    echo json_encode(['status'=>'success']);
} else {
    echo json_encode(['status'=>'error', 'message'=>mysqli_error($cont)]);
}
mysqli_close($cont);
?>