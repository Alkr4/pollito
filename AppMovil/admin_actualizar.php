<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión.']);
    exit();
}

$id_usuario_ejecutor = mysqli_real_escape_string($cont, $_GET['id_usuario_ejecutor']);
$id_usuario_target = mysqli_real_escape_string($cont, $_GET['id_usuario_target']);
$nuevo_estado = strtoupper(mysqli_real_escape_string($cont, $_GET['nuevo_estado']));

$check = "SELECT privilegios FROM usuarios WHERE id='$id_usuario_ejecutor'";
$res = mysqli_query($cont, $check);
$usr = mysqli_fetch_assoc($res);

if ($usr['privilegios'] != 'administrador') {
    echo json_encode(['status' => 'error', 'message' => 'Sin permisos de administrador']);
    exit();
}

$estados_validos = ['ACTIVO', 'INACTIVO', 'BLOQUEADO'];
if (!in_array($nuevo_estado, $estados_validos)) {
    echo json_encode(['status' => 'error', 'message' => 'Estado no válido']);
    exit();
}

$sql = "UPDATE usuarios SET estado = '$nuevo_estado' WHERE id = '$id_usuario_target'";

if (mysqli_query($cont, $sql)) {
    if (mysqli_affected_rows($cont) > 0) {
        echo json_encode(['status' => 'success', 'message' => 'Estado del usuario actualizado.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'No se encontró al usuario.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error al actualizar: ' . mysqli_error($cont)]);
}

mysqli_close($cont);
?>