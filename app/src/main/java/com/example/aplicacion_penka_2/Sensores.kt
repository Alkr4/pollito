package com.example.aplicacion_penka_2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Sensores : AppCompatActivity() {

    // UI Elements
    private lateinit var txtFechaHora: TextView
    private lateinit var btnAbrir: Button
    private lateinit var btnCerrar: Button
    private lateinit var listHistorial: ListView

    // Data
    private var historialList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var datos: RequestQueue

    // Intent Data
    private var userId: String? = ""
    private var deptId: String? = ""

    // Simple Clock Handler
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        // 1. Receive User Data
        userId = intent.getStringExtra("USER_ID")
        deptId = intent.getStringExtra("DEPT_ID")

        // 2. Bind Views (Only the ones that exist in your new XML)
        txtFechaHora = findViewById(R.id.txtFechaHora)
        btnAbrir = findViewById(R.id.btnAbrirBarrera)
        btnCerrar = findViewById(R.id.btnCerrarBarrera)
        listHistorial = findViewById(R.id.listHistorial)

        // 3. Setup List Adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historialList)
        listHistorial.adapter = adapter

        // 4. Setup Volley
        datos = Volley.newRequestQueue(this)

        // 5. Button Listeners
        btnAbrir.setOnClickListener { controllingBarrera("ABRIR") }
        btnCerrar.setOnClickListener { controllingBarrera("CERRAR") }

        // 6. Start logic
        startClock()
        cargarHistorial()
    }

    private fun controllingBarrera(accion: String) {
        // Call your local PHP (Config.URL_BASE)
        val url = "${Config.URL_BASE}control_barrera.php?accion=$accion&id_usuario=$userId"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Comando Enviado")
                    .setContentText("Barrera: $accion")
                    .show()
                // Refresh history to see the new event
                cargarHistorial()
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de Conexión")
                    .setContentText("No se pudo contactar al servidor")
                    .show()
            }
        )
        datos.add(request)
    }

    private fun cargarHistorial() {
        val url = "${Config.URL_BASE}listar_eventos.php?id_departamento=$deptId"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    historialList.clear()
                    val json = JSONArray(response)
                    for (i in 0 until json.length()) {
                        val evento = json.getJSONObject(i)

                        val fecha = evento.getString("fecha_hora")
                        val tipo = evento.getString("tipo_uso") // APERTURA_MANUAL etc.
                        val auth = evento.getString("autorizado")

                        historialList.add("$fecha | $tipo | $auth")
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error -> error.printStackTrace() }
        )
        datos.add(request)
    }

    private fun startClock() {
        clockRunnable = Runnable {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            txtFechaHora.text = dateFormat.format(Date())
            clockHandler.postDelayed(clockRunnable, 1000)
        }
        clockHandler.post(clockRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        clockHandler.removeCallbacks(clockRunnable)
    }
}