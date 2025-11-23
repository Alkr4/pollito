<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!isset($_GET['id_departamento'])) {
    echo json_encode(['status' => 'error', 'message' => 'Se requiere id_departamento.']);
    exit();
}

$id_departamento = mysqli_real_escape_string($cont, $_GET['id_departamento']);
$id_usuario_solicita = mysqli_real_escape_string($cont, $_GET['id_usuario']);

// VERIFICAR que el usuario pertenece al departamento o es admin
$sql_check = "SELECT id, privilegios, id_departamento FROM usuarios WHERE id='$id_usuario_solicita'";
$result_check = mysqli_query($cont, $sql_check);
$usuario = mysqli_fetch_assoc($result_check);

if(!$usuario){
    echo json_encode(['status' => 'error', 'message' => 'Usuario no válido']);
    exit();
}

// Si no es admin y pide otro departamento, denegar
if($usuario['privilegios'] != 'administrador' && $usuario['id_departamento'] != $id_departamento){
    echo json_encode(['status' => 'error', 'message' => 'Sin permisos']);
    exit();
}

// Usar la vista creada en el SQL mejorado
$sql = "SELECT * FROM v_usuarios_departamento WHERE id_departamento = '$id_departamento'";
$result = mysqli_query($cont, $sql);

$arr = array();
while ($datos = mysqli_fetch_assoc($result)) {
    $arr[] = $datos;
}

echo json_encode(['status'=>'success', 'usuarios'=>$arr]);
mysqli_close($cont);
?>