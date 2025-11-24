package com.example.aplicacion_penka_2

import android.content.Intent
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Sensores : AppCompatActivity() {

    // UI Elements
    private lateinit var txtFechaHora: TextView
    private lateinit var tvEstadoBarrera: TextView
    private lateinit var btnAbrirBarrera: Button
    private lateinit var btnCerrarBarrera: Button
    private lateinit var btnGestionSensores: Button
    private lateinit var btnLlaveroDigital: Button
    private lateinit var listHistorial: ListView

    // Data
    private var historialList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var datos: RequestQueue

    // Intent Data
    private var userId: String? = ""
    private var deptId: String? = ""
    private var userRole: String? = ""

    // Handlers
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    private val updateHandler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        // 1. Receive User Data
        userId = intent.getStringExtra("USER_ID")
        deptId = intent.getStringExtra("DEPT_ID")
        userRole = intent.getStringExtra("USER_ROLE")

        // 2. Bind Views
        txtFechaHora = findViewById(R.id.txtFechaHora)
        tvEstadoBarrera = findViewById(R.id.tvEstadoBarrera)
        btnAbrirBarrera = findViewById(R.id.btnAbrirBarrera)
        btnCerrarBarrera = findViewById(R.id.btnCerrarBarrera)
        btnGestionSensores = findViewById(R.id.btnGestionSensores)
        btnLlaveroDigital = findViewById(R.id.btnLlaveroDigital)
        listHistorial = findViewById(R.id.listHistorial)

        // 3. Setup List Adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historialList)
        listHistorial.adapter = adapter

        // 4. Setup Volley
        datos = Volley.newRequestQueue(this)

        // 5. Button Listeners
        btnAbrirBarrera.setOnClickListener { controlarBarrera("ABRIR") }
        btnCerrarBarrera.setOnClickListener { controlarBarrera("CERRAR") }

        btnGestionSensores.setOnClickListener {
            val intent = Intent(this, GestionSensoresSimple::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("DEPT_ID", deptId)
            intent.putExtra("USER_ROLE", userRole)
            startActivity(intent)
        }

        btnLlaveroDigital.setOnClickListener {
            val intent = Intent(this, LlaveroDigital::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("DEPT_ID", deptId)
            startActivity(intent)
        }

        // 6. Start logic
        startClock()
        cargarEstadoBarrera()
        cargarHistorial()

        // Actualización automática cada 5 segundos
        updateRunnable = Runnable {
            cargarEstadoBarrera()
            cargarHistorial()
            updateHandler.postDelayed(updateRunnable, 5000)
        }
        updateHandler.post(updateRunnable)
    }

    private fun controlarBarrera(accion: String) {
        val url = "${Config.URL_BASE}control_barrera.php?accion=$accion&id_usuario=$userId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if(status == "success"){
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Comando Enviado")
                        .setContentText("Barrera: $accion")
                        .show()
                    cargarEstadoBarrera()
                    cargarHistorial()
                }
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

    private fun cargarEstadoBarrera() {
        val url = "${Config.URL_BASE}estado_barrera.php"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if(status == "success"){
                    val estado = response.getString("estado")
                    tvEstadoBarrera.text = "Estado: $estado"

                    // Cambiar color según estado
                    when(estado){
                        "ABIERTA" -> tvEstadoBarrera.setBackgroundColor(0xFF4CAF50.toInt())
                        "CERRADA" -> tvEstadoBarrera.setBackgroundColor(0xFFE0E0E0.toInt())
                        else -> tvEstadoBarrera.setBackgroundColor(0xFFFF9800.toInt())
                    }
                }
            },
            { /* Error silencioso */ }
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
                    for (i in 0 until json.length().coerceAtMost(10)) { // Solo últimos 10
                        val evento = json.getJSONObject(i)
                        val fecha = evento.getString("fecha_hora")
                        val tipo = evento.getString("tipo_uso")
                        val auth = evento.getString("autorizado")

                        historialList.add("$fecha\n$tipo | $auth")
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { /* Error silencioso */ }
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

    override fun onResume() {
        super.onResume()
        cargarEstadoBarrera()
        cargarHistorial()
    }

    override fun onDestroy() {
        super.onDestroy()
        clockHandler.removeCallbacks(clockRunnable)
        updateHandler.removeCallbacks(updateRunnable)
    }
}