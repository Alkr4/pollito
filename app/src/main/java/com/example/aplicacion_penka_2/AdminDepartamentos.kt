package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class AdminDepartamentos : AppCompatActivity() {

    private lateinit var etNumero: EditText
    private lateinit var etTorre: EditText
    private lateinit var etPiso: EditText
    private lateinit var etCondominio: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_departamentos)

        etNumero = findViewById(R.id.etDeptNumero)
        etTorre = findViewById(R.id.etDeptTorre)
        etPiso = findViewById(R.id.etDeptPiso)
        etCondominio = findViewById(R.id.etDeptCondominio)
        btnGuardar = findViewById(R.id.btnGuardarDepto)

        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                registrarDepartamento()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (etNumero.text.isEmpty() || etTorre.text.isEmpty() || etPiso.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registrarDepartamento() {
        val numero = etNumero.text.toString().trim()
        val torre = etTorre.text.toString().trim()
        val piso = etPiso.text.toString().trim()
        val condominio = etCondominio.text.toString().trim()

        val url = "${Config.URL_BASE}registrar_departamento.php?" +
                "numero=$numero&torre=$torre&piso=$piso&condominio=$condominio"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    if (status == "success") {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Registrado!")
                            .setContentText("Departamento creado exitosamente")
                            .show()
                        limpiarCampos()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(response.getString("message"))
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión")
                    .setContentText(error.message)
                    .show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun limpiarCampos() {
        etNumero.setText("")
        etTorre.setText("")
        etPiso.setText("")
    }
}