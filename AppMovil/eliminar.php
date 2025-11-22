<?php
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

$id = $_GET['id'];

$sql = "DELETE FROM usuarios WHERE id='$id'";

if (mysqli_query($cont, $sql)) {
    echo "Eliminación exitosa";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($cont);
}

mysqli_close($cont);
?>
