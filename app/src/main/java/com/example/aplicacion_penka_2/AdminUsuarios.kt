package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class AdminUsuarios : AppCompatActivity() {
    // UI
    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRut: EditText
    private lateinit var etPass: EditText
    private lateinit var etDeptoID: EditText
    private lateinit var switchAdmin: Switch // Toggle: Off=Operador, On=Admin
    private lateinit var btnCrear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_usuarios) // Needs to be created

        etNombre = findViewById(R.id.etAdminNombre)
        etEmail = findViewById(R.id.etAdminEmail)
        etRut = findViewById(R.id.etAdminRut)
        etPass = findViewById(R.id.etAdminPass)
        etDeptoID = findViewById(R.id.etAdminDeptoID)
        switchAdmin = findViewById(R.id.switchEsAdmin)
        btnCrear = findViewById(R.id.btnAdminCrear)

        btnCrear.setOnClickListener {
            crearUsuario()
        }
    }

    private fun crearUsuario() {
        val rol = if (switchAdmin.isChecked) "administrador" else "operador"
        val url = "${Config.URL_BASE}admin_crear_usuario.php?" +
                "nombre=${etNombre.text}&email=${etEmail.text}" +
                "&password=${etPass.text}&rut=${etRut.text}" +
                "&telefono=00000000" +
                "&id_departamento=${etDeptoID.text}&rol=$rol"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                if (response.getString("status") == "success") {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Usuario Creado")
                        .setContentText("Rol asignado: $rol")
                        .show()
                    limpiar()
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
                }
            },
            { error -> error.printStackTrace() }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun limpiar() {
        etNombre.setText(""); etEmail.setText(""); etRut.setText("")
    }
}