<?php
header('Content-Type: text/plain');

// 1. Connect to DB
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

// 2. Get the LAST command sent by the App (Manual Open or Close)
// We filter only for "APERTURA_MANUAL" or "CIERRE_MANUAL"
$sql = "SELECT tipo_uso FROM historial_sensores 
        WHERE tipo_uso IN ('APERTURA_MANUAL', 'CIERRE_MANUAL') 
        ORDER BY id DESC LIMIT 1";

$result = mysqli_query($cont, $sql);

if ($row = mysqli_fetch_assoc($result)) {
    // This will print "APERTURA_MANUAL" or "CIERRE_MANUAL"
    echo $row['tipo_uso']; 
} else {
    echo "ESPERA";
}

mysqli_close($cont);
?>