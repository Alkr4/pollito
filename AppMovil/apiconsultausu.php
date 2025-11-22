<?php
header('Content-Type: application/json');

// 1. Connect
$cont = mysqli_connect('localhost', 'chris', 'Admin123', 'AppMovil');

if (!$cont) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión DB']);
    exit();
}

// 2. Get Data
$email = mysqli_real_escape_string($cont, $_GET['email']);
$password = $_GET['password']; // Plain text from the App

// 3. Find user by Email ONLY (Don't check password in SQL yet)
$sql = "SELECT * FROM usuarios WHERE email = '$email'";
$result = mysqli_query($cont, $sql);

if (mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    
    // 4. Verify the Password Hash
    // Note: column name must match what you used in registro.php ('password_hash')
    if (password_verify($password, $row['password_hash'])) {
        
        // Success! Send the data your Kotlin expects
        $datosUsuario = [
            'id' => $row['id'],
            'privilegios' => $row['privilegios'],      // 'administrador' or 'operador'
            'id_departamento' => $row['id_departamento']
        ];
        
        echo json_encode([
            'status' => 'success',
            'usuario' => $datosUsuario
        ]);
        
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Contraseña incorrecta']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Correo no registrado']);
}

mysqli_close($cont);
?>