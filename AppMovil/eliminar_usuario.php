<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$id_usuario_eliminar = mysqli_real_escape_string($cont, $_GET['id_usuario']);
$id_admin = mysqli_real_escape_string($cont, $_GET['id_admin']);

// Validar que quien ejecuta sea admin
$sql_check = "SELECT privilegios FROM usuarios WHERE id='$id_admin'";
$result = mysqli_query($cont, $sql_check);
$admin = mysqli_fetch_assoc($result);

if($admin['privilegios'] != 'administrador'){
    echo json_encode(['status'=>'error', 'message'=>'Sin permisos de administrador']);
    exit();
}

// No permitir que el admin se elimine a sí mismo
if($id_admin == $id_usuario_eliminar){
    echo json_encode(['status'=>'error', 'message'=>'No puedes eliminarte a ti mismo']);
    exit();
}

// Eliminar (CASCADE eliminará sensores asociados)
$sql_delete = "DELETE FROM usuarios WHERE id='$id_usuario_eliminar'";

if(mysqli_query($cont, $sql_delete)){
    if(mysqli_affected_rows($cont) > 0){
        echo json_encode(['status'=>'success', 'message'=>'Usuario eliminado']);
    } else {
        echo json_encode(['status'=>'error', 'message'=>'Usuario no encontrado']);
    }
} else {
    echo json_encode(['status'=>'error', 'message'=>'Error: '.mysqli_error($cont)]);
}

mysqli_close($cont);
?>