package com.example.aplicacion_penka_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class Ingreso : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var lastname: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var repeatPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var datos: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)

        name = findViewById(R.id.et_name)
        lastname = findViewById(R.id.et_lastname)
        email = findViewById(R.id.et_email)
        phone = findViewById(R.id.et_phone)
        password = findViewById(R.id.et_password)
        repeatPassword = findViewById(R.id.et_repeat_password)
        btnRegister = findViewById(R.id.btn_register)

        datos = Volley.newRequestQueue(this)

        btnRegister.setOnClickListener {
            val nombreVal = name.text.toString().trim()
            val apellidoVal = lastname.text.toString().trim()
            val emailVal = email.text.toString().trim()
            val telefonoVal = phone.text.toString().trim()
            val passwordVal = password.text.toString()
            val repeatPasswordVal = repeatPassword.text.toString()

            // Validar campos vacíos
            if (nombreVal.isEmpty() || apellidoVal.isEmpty() || emailVal.isEmpty() || telefonoVal.isEmpty() || passwordVal.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Atención")
                    .setContentText("Debe completar todos los campos")
                    .show()
                return@setOnClickListener
            }

            // Validar formato de email
            if (!esEmailValido(emailVal)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El formato del correo electrónico no es válido")
                    .show()
                return@setOnClickListener
            }

            // Validar fortaleza de contraseña
            val erroresPassword = validarFortalezaPassword(passwordVal)
            if (erroresPassword.isNotEmpty()) {
                val mensajeError = erroresPassword.joinToString(separator = " - ")

                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Contraseña Insegura")
                    .setContentText(mensajeError)
                    .show()
                return@setOnClickListener
            }

            // Validar que las contraseñas coincidan
            if (passwordVal != repeatPasswordVal) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Las contraseñas no coinciden")
                    .show()
                return@setOnClickListener
            }

            // Validar número
            if (telefonoVal.length != 8)  {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El número de teléfono debe contener solo 8 dígitos")
                    .show()
                return@setOnClickListener
            }
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nombreVal = name.text.toString().trim()
        val apellidoVal = lastname.text.toString().trim()
        val emailVal = email.text.toString().trim()
        val telefonoVal = phone.text.toString().trim()
        val passwordVal = password.text.toString()

        val url = "http://18.211.13.143/registro.php?name=$nombreVal&lastname=$apellidoVal&email=$emailVal&phone=$telefonoVal&password=$passwordVal"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
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
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión")
                    .setContentText("Error al registrar el usuario: ${error.message}")
                    .show()
            }
        )
        datos.add(request)
    }

    private fun esEmailValido(email: String): Boolean {
        if (email.isEmpty()) {
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validarFortalezaPassword(password: String): List<String> {
        val errores = mutableListOf<String>()

        //Longitud mínima
        if (password.length < 8) {
            errores.add("Debe tener al menos 8 caracteres")
        }

        //Al menos una letra minúscula
        if (!password.any { it.isLowerCase() }) {
            errores.add("Debe contener al menos una minúscula")
        }

        //Al menos una letra mayúscula
        if (!password.any { it.isUpperCase() }) {
            errores.add("Debe contener al menos una mayúscula")
        }

        //Al menos un número
        if (!password.any { it.isDigit() }) {
            errores.add("Debe contener al menos un número")
        }

        //Al menos un caracter especial
        val patronEspecial = Regex("[^a-zA-Z0-9]")
        if (!patronEspecial.containsMatchIn(password)) {
            errores.add("Debe contener al menos un símbolo (ej. !@#$)")
        }

        return errores
    }
}