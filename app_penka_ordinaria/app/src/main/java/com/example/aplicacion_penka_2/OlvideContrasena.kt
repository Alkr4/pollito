package com.example.aplicacion_penka_2

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class OlvideContrasena : AppCompatActivity() {
    private lateinit var inputEmail: EditText
    private lateinit var btnEnviar: Button
    private lateinit var inputCodigo: EditText
    private lateinit var inputNueva: EditText
    private lateinit var inputConfirmar: EditText
    private lateinit var btnGuardar: Button
    private lateinit var tvTimer: TextView
    private lateinit var datos: RequestQueue

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvide_contrasena)

        inputEmail = findViewById(R.id.inputEmail)
        btnEnviar = findViewById(R.id.btnEnviar)
        inputCodigo = findViewById(R.id.inputCodigo)
        inputNueva = findViewById(R.id.inputNueva)
        inputConfirmar = findViewById(R.id.inputConfirmar)
        btnGuardar = findViewById(R.id.btnGuardar)
        tvTimer = findViewById(R.id.tvTimer)

        datos = Volley.newRequestQueue(this)

        setCamposCodigo(false)

        btnEnviar.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.titleText = "Enviando..."
                pDialog.setCancelable(false)
                pDialog.show()
                enviarCorreoRecuperacion(email, pDialog)
            } else {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Por favor, ingresa un correo electrónico válido.")
                    .show()
            }
        }

        btnGuardar.setOnClickListener {
            guardarNuevaContrasena()
        }
    }

    private fun setCamposCodigo(habilitados: Boolean) {
        inputCodigo.isEnabled = habilitados
        inputNueva.isEnabled = habilitados
        inputConfirmar.isEnabled = habilitados
        btnGuardar.isEnabled = habilitados

        if (!habilitados) {
            tvTimer.visibility = View.GONE
            inputEmail.isEnabled = true
            btnEnviar.isEnabled = true
            timer?.cancel()
        } else {
            inputEmail.isEnabled = false
            btnEnviar.isEnabled = false
            tvTimer.visibility = View.VISIBLE
        }
    }

    private fun iniciarTimer() {
        //Timer de 1 minuto (60000 ms)
        timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundos = millisUntilFinished / 1000
                tvTimer.text = "Código expira en: ${segundos}s"
            }

            override fun onFinish() {
                tvTimer.text = "¡Código expirado!"
                SweetAlertDialog(this@OlvideContrasena, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Tiempo Agotado")
                    .setContentText("El código ha expirado. Solicita uno nuevo.")
                    .show()
                setCamposCodigo(false)
            }
        }.start()
    }

    private fun enviarCorreoRecuperacion(email: String, pDialog: SweetAlertDialog) {
        val url = "http://18.211.13.143/enviar_recuperacion_codigo.php?email=$email"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val status = response.getString("status")
                    val message = response.getString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Éxito!")
                            .setContentText(message)
                            .show()
                        setCamposCodigo(true)
                        iniciarTimer()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(message)
                            .show()
                    }
                } catch (e: JSONException) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Respuesta inesperada del servidor.")
                        .show()
                }
            },
            { error ->
                pDialog.dismissWithAnimation()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión")
                    .setContentText("No se pudo conectar al servidor.")
                    .show()
            }
        )
        request.retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        datos.add(request)
    }

    private fun guardarNuevaContrasena() {
        val email = inputEmail.text.toString().trim()
        val codigo = inputCodigo.text.toString().trim()
        val nueva = inputNueva.text.toString()
        val confirmar = inputConfirmar.text.toString()

        //Validaciones de cliente
        if (codigo.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos Vacíos")
                .setContentText("Debes ingresar el código, la nueva contraseña y la confirmación.")
                .show()
            return
        }

        if (codigo.length != 5 || !codigo.matches(Regex("\\d+"))) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Código Inválido")
                .setContentText("El código debe ser de 5 dígitos numéricos.")
                .show()
            return
        }

        val erroresPassword = validarFortalezaPassword(nueva)
        if (erroresPassword.isNotEmpty()) {
            val mensajeError = erroresPassword.joinToString("\n")
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Contraseña Insegura")
                .setContentText(mensajeError)
                .show()
            return
        }

        if (nueva != confirmar) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Las contraseñas no coinciden.")
                .show()
            return
        }

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Guardando..."
        pDialog.setCancelable(false)
        pDialog.show()

        val url = "http://18.211.13.143/validar_y_resetear.php?email=$email&codigo=$codigo&password=$nueva"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                pDialog.dismissWithAnimation()
                try {
                    val status = response.getString("status")
                    val message = response.getString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Éxito!")
                            .setContentText(message)
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                finish()
                            }
                            .show()
                        timer?.cancel()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(message)
                            .show()
                    }
                } catch (e: JSONException) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Respuesta inesperada del servidor.")
                        .show()
                }
            },
            { error ->
                pDialog.dismissWithAnimation()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión")
                    .setContentText("No se pudo conectar al servidor.")
                    .show()
            }
        )
        datos.add(request)
    }

    private fun validarFortalezaPassword(password: String): List<String> {
        val errores = mutableListOf<String>()
        if (password.length < 8) {
            errores.add("Debe tener al menos 8 caracteres")
        }
        if (!password.any { it.isLowerCase() }) {
            errores.add("Debe contener al menos una minúscula")
        }
        if (!password.any { it.isUpperCase() }) {
            errores.add("Debe contener al menos una mayúscula")
        }
        if (!password.any { it.isDigit() }) {
            errores.add("Debe contener al menos un número")
        }
        val patronEspecial = Regex("[^a-zA-Z0-9]")
        if (!patronEspecial.containsMatchIn(password)) {
            errores.add("Debe contener al menos un símbolo (ej. !@#$)")
        }
        return errores
    }
}