<?php
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');
$inputJSON = file_get_contents('php://input');

$input = json_decode($inputJSON, true);

if (isset($input['uid'])) {
    $uid = $input['uid'];
    $sql = "select * from sensores where mac_uid = '$uid' and activo = 1";
    file_put_contents('uids.txt', $uid . PHP_EOL, FILE_APPEND);
    $result = mysqli_query($cont, $sql);
    $datos = mysqli_fetch_assoc($result);
    if ($datos != null){
        echo json_encode(['status' => 'ok', 'uid' => $uid]);
    }
    else {
        echo json_encode(['status' => 'error', 'message' => 'El sensor no existe, o está bloqueado']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'UID no recibido']);
}
?>
