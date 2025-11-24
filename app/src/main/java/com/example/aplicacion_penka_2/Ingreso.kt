package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class Ingreso : AppCompatActivity() {

    // Define all UI elements
    private lateinit var etName: EditText
    private lateinit var etLastname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etRut: EditText            // NEW
    private lateinit var etDepartamento: EditText   // NEW
    private lateinit var etPassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var datos: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)

        // Bind ID from XML
        etName = findViewById(R.id.et_name)
        etLastname = findViewById(R.id.et_lastname)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etRut = findViewById(R.id.et_rut)                   // Make sure this ID exists in XML
        etDepartamento = findViewById(R.id.et_departamento) // Make sure this ID exists in XML
        etPassword = findViewById(R.id.et_password)
        etRepeatPassword = findViewById(R.id.et_repeat_password)
        btnRegister = findViewById(R.id.btn_register)

        datos = Volley.newRequestQueue(this)

        btnRegister.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nombre = etName.text.toString().trim()
        val apellido = etLastname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val rut = etRut.text.toString().trim()
        val depto = etDepartamento.text.toString().trim()
        val password = etPassword.text.toString()
        val repeatPassword = etRepeatPassword.text.toString()

        // 1. Check Empty
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
            rut.isEmpty() || depto.isEmpty() || password.isEmpty()) {
            mostrarError("Debe completar todos los campos")
            return false
        }

        // 2. Validate Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarError("El formato del correo electrónico no es válido")
            return false
        }

        // 3. Validate Passwords Match
        if (password != repeatPassword) {
            mostrarError("Las contraseñas no coinciden")
            return false
        }

        // 4. Validate Password Strength
        val erroresPass = validarFortalezaPassword(password)
        if (erroresPass.isNotEmpty()) {
            mostrarError(erroresPass.joinToString("\n"))
            return false
        }

        return true
    }

    private fun registrarUsuario() {
        val nombre = etName.text.toString().trim()
        val apellido = etLastname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etPhone.text.toString().trim()
        val rut = etRut.text.toString().trim()
        val depto = etDepartamento.text.toString().trim()
        val password = etPassword.text.toString()

        // Use Config.URL_BASE + pass ALL parameters required by PHP
        val url = "${Config.URL_BASE}registro.php?" +
                "name=$nombre&lastname=$apellido&email=$email" +
                "&phone=$telefono&rut=$rut&id_departamento=$depto&password=$password"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    val message = response.getString("message")

                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Registrado!")
                            .setContentText(message)
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                finish() // Go back to Login
                            }
                            .show()
                    } else {
                        mostrarError(message)
                    }
                } catch (e: JSONException) {
                    mostrarError("Error al procesar respuesta: ${e.message}")
                }
            },
            { error ->
                mostrarError("Error de Conexión: Verifique que WampServer esté corriendo y la IP en Config.kt sea correcta.")
            }
        )
        datos.add(request)
    }

    private fun mostrarError(mensaje: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(mensaje)
            .show()
    }

    private fun validarFortalezaPassword(password: String): List<String> {
        val errores = mutableListOf<String>()
        if (password.length < 8) errores.add("- Mínimo 8 caracteres")
        if (!password.any { it.isLowerCase() }) errores.add("- Al menos una minúscula")
        if (!password.any { it.isUpperCase() }) errores.add("- Al menos una mayúscula")
        if (!password.any { it.isDigit() }) errores.add("- Al menos un número")
        return errores
    }
}