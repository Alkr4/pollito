<?php
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$id = $_GET['id_usuario'];
$nombre = $_GET['nombre'];
$apellido = $_GET['apellido'];
$email = $_GET['email'];
$telefono = $_GET['telefono'];
$rut = $_GET['rut'];

$sql = "UPDATE usuarios SET nombre='$nombre', apellido='$apellido', email='$email', telefono='$telefono', rut='$rut' WHERE id='$id'";   

if (mysqli_query($cont, $sql)) {
    echo "Modificación exitosa";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($cont);
}

mysqli_close($cont);
?>
