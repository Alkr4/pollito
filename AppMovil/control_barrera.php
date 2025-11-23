<?php
header('Content-Type: application/json');
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if(!$cont){
    echo json_encode(['status'=>'error', 'message'=>'Error de conexión']);
    exit();
}

$accion = mysqli_real_escape_string($cont, $_GET['accion']); // ABRIR o CERRAR
$id_usuario = mysqli_real_escape_string($cont, $_GET['id_usuario']);
$fecha = date('Y-m-d H:i:s');

// Validar acción
if(!in_array($accion, ['ABRIR', 'CERRAR'])){
    echo json_encode(['status'=>'error', 'message'=>'Acción no válida']);
    exit();
}

// Mapear acción
$tipo_uso = ($accion == "ABRIR") ? "APERTURA_MANUAL" : "CIERRE_MANUAL";
$estado_barrera = ($accion == "ABRIR") ? "ABIERTA" : "CERRADA";

// 1. Actualizar tabla control_barrera
$sql_barrera = "UPDATE control_barrera 
                SET estado_actual='$estado_barrera', 
                    ultimo_comando='$accion', 
                    id_usuario_comando='$id_usuario' 
                WHERE id=1";

if(!mysqli_query($cont, $sql_barrera)){
    echo json_encode(['status'=>'error', 'message'=>'Error al actualizar barrera']);
    exit();
}

// 2. Registrar en auditoría
$sql_audit = "INSERT INTO auditoria (id_sensor, id_usuario, tipo_uso, autorizado, detalles) 
              VALUES (NULL, '$id_usuario', '$tipo_uso', 'PERMITIDO', 'Control manual desde app')";

if(mysqli_query($cont, $sql_audit)){
    echo json_encode([
        'status'=>'success', 
        'accion'=>$accion,
        'estado_barrera'=>$estado_barrera
    ]);
} else {
    echo json_encode(['status'=>'error', 'message'=>mysqli_error($cont)]);
}

mysqli_close($cont);
?>