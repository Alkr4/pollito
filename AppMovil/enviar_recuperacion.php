<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';


$db_host = 'localhost';
$db_user = 'chris';
$db_pass = 'Admin123';
$db_name = 'AppMovil';

$smtp_user = 'soportepenkavc@gmail.com'; 
$smtp_pass = 'iogt ecka hbix oqot';
$reset_page_url = 'http://3.208.0.144/reset_password.php'; 

$conexion = mysqli_connect($db_host, $db_user, $db_pass, $db_name);

if (!$conexion) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit();
}

if (!isset($_GET['email'])) {
    echo json_encode(['status' => 'error', 'message' => 'No se proporcionó un email.']);
    exit();
}

$email = mysqli_real_escape_string($conexion, $_GET['email']);

$sql_check_user = "SELECT id FROM usuarios WHERE email = '$email'";
$result_check_user = mysqli_query($conexion, $sql_check_user);

if (mysqli_num_rows($result_check_user) > 0) {

    $token = bin2hex(random_bytes(32));
    $expires = time() + 3600; 

    $sql_insert_token = "INSERT INTO password_resets (email, token, expires_at, estado) VALUES ('$email', '$token', '$expires','NO USADO')";
    
    if (mysqli_query($conexion, $sql_insert_token)) {
        $mail = new PHPMailer(true);
        try {

            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            $mail->Username   = $smtp_user;
            $mail->Password   = $smtp_pass;
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port       = 587;

            $mail->setFrom($smtp_user, 'Soporte PenkaApp');
            $mail->addAddress($email);

            $mail->isHTML(true);
            $mail->Subject = 'Recuperacion de Contrasena - PenkaApp';
            $mail->Body    = "Hola,<br><br>Para restablecer tu contraseña, haz clic en el siguiente enlace:<br><a href='$reset_page_url?token=$token'>Restablecer Contraseña</a><br><br>El enlace expirará en 1 hora.";
            
            $mail->send();
            echo json_encode(['status' => 'success', 'message' => 'Correo de recuperación enviado.']);
        } catch (Exception $e) {
            echo json_encode(['status' => 'error', 'message' => "El correo no pudo ser enviado. Mailer Error: {$mail->ErrorInfo}"]);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Error al generar el token.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'El correo no se encuentra registrado.']);
}

mysqli_close($conexion);
?>
