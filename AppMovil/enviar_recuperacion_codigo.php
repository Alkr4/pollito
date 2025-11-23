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

header('Content-Type: application/json');
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
    $codigo_numerico = rand(10000, 99999);
    $expires = time() + 60;
    mysqli_query($conexion, "DELETE FROM password_resets WHERE email = '$email'");
    $sql_insert = "INSERT INTO password_resets (email, token, expires_at) VALUES ('$email', '$codigo_numerico', '$expires')";

    if (mysqli_query($conexion, $sql_insert)) {
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
            $mail->Subject = 'Tu codigo de recuperacion - PenkaApp';
            $mail->Body    = "Hola,<br><br>Tu codigo para restablecer la contrasena es: <h2><b>$codigo_numerico</b></h2><br>Este codigo es valido solo por 1 minuto.";
            $mail->send();
            echo json_encode(['status' => 'success', 'message' => 'Codigo de recuperacion enviado.']);
        } catch (Exception $e) {
            echo json_encode(['status' => 'error', 'message' => "El correo no pudo ser enviado. Mailer Error: {$mail->ErrorInfo}"]);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Error al generar el codigo.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'El correo no se encuentra registrado.']);
}
mysqli_close($conexion);
?>
