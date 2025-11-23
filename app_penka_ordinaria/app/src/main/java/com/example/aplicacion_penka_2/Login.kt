package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import cn.pedant.SweetAlert.SweetAlertDialog

class Login : AppCompatActivity() {
    private lateinit var usu: TextView
    private lateinit var clave: TextView
    private lateinit var btn: Button
    private lateinit var datos: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usu = findViewById(R.id.et_email)
        clave = findViewById(R.id.et_password)
        btn = findViewById(R.id.btn_login)
        datos = Volley.newRequestQueue(this)

        btn.setOnClickListener {
            val emailVal = usu.text.toString().trim()
            val passwordVal = clave.text.toString()

            if (emailVal.isEmpty() || passwordVal.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Atención")
                    .setContentText("Debe completar todos los campos")
                    .show()
                return@setOnClickListener
            }
            if (!esEmailValido(emailVal)) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El formato del correo electrónico no es válido")
                    .show()
                return@setOnClickListener
            }
            consultarDatos(emailVal, passwordVal)
        }

        findViewById<TextView>(R.id.tv_register).setOnClickListener {
            startActivity(Intent(this, Ingreso::class.java))
        }

        findViewById<TextView>(R.id.tv_forgot).setOnClickListener {
            startActivity(Intent(this, OlvideContrasena::class.java))
        }
    }

    private fun consultarDatos(email: String, password: String) {
        val url = "http://18.211.13.143/apiconsultausu.php?email=$email&password=$password"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val estado = response.getString("estado")
                    if (estado == "0") {
                        SweetAlertDialog(this@Login, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Las credenciales son erróneas")
                            .show()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Éxito!")
                            .setContentText("Bienvenido a penka app")
                            .setConfirmClickListener { sDialog ->
                                sDialog.dismissWithAnimation()
                                startActivity(Intent(this, Menu::class.java))
                            }
                            .show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
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
}