<?php

$cont=mysqli_connect('localhost','chris','Admin123','AppMovil');

$sql="select * from sensores";
$result=mysqli_query($cont,$sql);
$arr = array();
while($datos=mysqli_fetch_array($result))
{
$arr[] = $datos;
}
echo json_encode($arr);
?>