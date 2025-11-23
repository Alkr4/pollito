package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog

class ModificarEliminar : AppCompatActivity() {
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var datos: RequestQueue

    private var originalId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_eliminar)

        etNombre = findViewById(R.id.et_nombre)
        etApellido = findViewById(R.id.et_apellido)
        etEmail = findViewById(R.id.et_email)
        etTelefono = findViewById(R.id.et_telefono)
        btnModificar = findViewById(R.id.btn_modificar)
        btnEliminar = findViewById(R.id.btn_eliminar)

        datos = Volley.newRequestQueue(this)

        val id = intent.getStringExtra("id") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""

        originalId = id

        etNombre.setText(nombre)
        etApellido.setText(apellido)
        etEmail.setText(email)
        etTelefono.setText(telefono)

        btnModificar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoApellido = etApellido.text.toString().trim()
            val nuevoEmail = etEmail.text.toString().trim()
            val nuevoTelefono = etTelefono.text.toString().trim()

            // Validar campos vacíos
            if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoEmail.isEmpty() || nuevoTelefono.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("¡Atención!")
                    .setContentText("Complete todos los campos")
                    .show()
                return@setOnClickListener
            }
            // Validar Nombre
            if (!esTextoValido(nuevoNombre)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El nombre solo debe contener letras y espacios")
                    .show()
                return@setOnClickListener
            }
            //Validar Apellido
            if (!esTextoValido(nuevoApellido)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El apellido solo debe contener letras y espacios")
                    .show()
                return@setOnClickListener
            }

            // Validar formato de email
            if (!esEmailValido(nuevoEmail)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El formato del correo electrónico no es válido")
                    .show()
                return@setOnClickListener
            }

            // Validar número de teléfono
            if (!esTelefonoValido(nuevoTelefono)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El número de teléfono debe contener 8 dígitos numéricos")
                    .show()
                return@setOnClickListener
            }

            modificarUsuario(nuevoNombre, nuevoApellido, nuevoEmail, nuevoTelefono)
        }

        btnEliminar.setOnClickListener {
            eliminarUsuario()
        }
    }

    private fun modificarUsuario(nombre: String, apellido: String, email: String, telefono: String) {
        val url = "http://18.211.13.143/modificar.php?id=$originalId&nombre=$nombre&apellido=$apellido&email=$email&telefono=$telefono"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText("Usuario modificado: $nombre $apellido")
                    .setConfirmClickListener {
                        it.dismissWithAnimation()
                        finish()
                    }
                    .show()
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al modificar el usuario: ${error.message}")
                    .show()
            }
        )
        datos.add(request)
    }

    private fun eliminarUsuario() {
        val url = "http://18.211.13.143/eliminar.php?id=$originalId"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText("Usuario eliminado: ${etNombre.text} ${etApellido.text}")
                    .setConfirmClickListener {
                        it.dismissWithAnimation()
                        finish()
                    }
                    .show()
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al eliminar el usuario: ${error.message}")
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

    private fun esTelefonoValido(telefono: String): Boolean {
        val patron = Regex("\\d{8}")
        return patron.matches(telefono)
    }

    private fun esTextoValido(texto: String): Boolean {
        val patron = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+\$")
        return texto.isNotEmpty() && patron.matches(texto)
    }
}