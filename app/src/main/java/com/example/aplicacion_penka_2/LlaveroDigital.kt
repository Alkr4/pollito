package com.example.aplicacion_penka_2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class LlaveroDigital : AppCompatActivity() {

    private lateinit var cardEventoPendiente: LinearLayout
    private lateinit var tvDetallesEvento: TextView
    private lateinit var btnPermitir: Button
    private lateinit var btnDenegar: Button
    private lateinit var listEventos: ListView
    private lateinit var tvActualizacion: TextView

    private var eventosList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private var userId: String? = null
    private var deptId: String? = null

    private var sensorActualId: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_llavero_digital)

        userId = intent.getStringExtra("USER_ID")
        deptId = intent.getStringExtra("DEPT_ID")

        cardEventoPendiente = findViewById(R.id.cardEventoPendiente)
        tvDetallesEvento = findViewById(R.id.tvDetallesEvento)
        btnPermitir = findViewById(R.id.btnPermitir)
        btnDenegar = findViewById(R.id.btnDenegar)
        listEventos = findViewById(R.id.listEventos)
        tvActualizacion = findViewById(R.id.tvActualizacion)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventosList)
        listEventos.adapter = adapter

        btnPermitir.setOnClickListener { procesarAcceso("PERMITIR") }
        btnDenegar.setOnClickListener { procesarAcceso("DENEGAR") }

        cargarEventos()
        iniciarActualizacionAutomatica()
    }

    private fun iniciarActualizacionAutomatica() {
        updateRunnable = Runnable {
            verificarEventosPendientes()
            cargarEventos()
            handler.postDelayed(updateRunnable, 3000) // Cada 3 segundos
        }
        handler.post(updateRunnable)
    }

    private fun verificarEventosPendientes() {
        val url = "${Config.URL_BASE}obtener_eventos_pendientes.php?id_departamento=$deptId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    if(status == "success"){
                        val hayEvento = response.getBoolean("hay_evento")

                        if(hayEvento){
                            val evento = response.getJSONObject("evento")
                            mostrarEventoPendiente(evento)
                        } else {
                            cardEventoPendiente.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { /* Error silencioso */ }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarEventoPendiente(evento: org.json.JSONObject) {
        sensorActualId = evento.getString("sensor_id")

        val tipoUso = evento.getString("tipo_uso")
        val codigo = evento.optString("codigo_sensor", "Desconocido")
        val usuario = "${evento.optString("usuario_nombre", "")} ${evento.optString("usuario_apellido", "")}"
        val tipo = evento.optString("sensor_tipo", "")
        val autorizado = evento.getString("autorizado")
        val fecha = evento.getString("fecha_hora")

        val detalles = """
            📅 $fecha
            👤 Usuario: $usuario
            🔑 Sensor: $codigo ($tipo)
            📊 Tipo: $tipoUso
            🚦 Estado: $autorizado
        """.trimIndent()

        tvDetallesEvento.text = detalles

        // Mostrar card solo si el acceso fue denegado
        if(autorizado == "DENEGADO"){
            cardEventoPendiente.visibility = View.VISIBLE
            cardEventoPendiente.setBackgroundColor(0xFFFFF3E0.toInt()) // Naranja claro
        } else {
            // Si fue permitido, mostrar brevemente en verde
            cardEventoPendiente.visibility = View.VISIBLE
            cardEventoPendiente.setBackgroundColor(0xFFE8F5E9.toInt()) // Verde claro
            btnPermitir.visibility = View.GONE
            btnDenegar.visibility = View.GONE

            // Auto-ocultar después de 3 segundos
            handler.postDelayed({
                cardEventoPendiente.visibility = View.GONE
                btnPermitir.visibility = View.VISIBLE
                btnDenegar.visibility = View.VISIBLE
            }, 3000)
        }
    }

    private fun procesarAcceso(accion: String) {
        if(sensorActualId.isNullOrEmpty()){
            Toast.makeText(this, "No hay sensor seleccionado", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${Config.URL_BASE}aprobar_acceso_manual.php?" +
                "id_usuario=$userId&id_sensor=$sensorActualId&accion=$accion"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if(status == "success"){
                    val mensaje = if(accion == "PERMITIR") {
                        "✓ Acceso PERMITIDO - Barrera abierta"
                    } else {
                        "✗ Acceso DENEGADO"
                    }

                    val tipoAlerta = if(accion == "PERMITIR") {
                        SweetAlertDialog.SUCCESS_TYPE
                    } else {
                        SweetAlertDialog.ERROR_TYPE
                    }

                    SweetAlertDialog(this, tipoAlerta)
                        .setTitleText(mensaje)
                        .setContentText("El comando se ha ejecutado correctamente")
                        .show()

                    cardEventoPendiente.visibility = View.GONE
                    cargarEventos()
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun cargarEventos() {
        val url = "${Config.URL_BASE}listar_eventos.php?id_departamento=$deptId"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    eventosList.clear()
                    val json = JSONArray(response)

                    for(i in 0 until json.length().coerceAtMost(20)){ // Últimos 20
                        val evento = json.getJSONObject(i)
                        val fecha = evento.getString("fecha_hora")
                        val tipo = evento.getString("tipo_uso")
                        val auth = evento.getString("autorizado")

                        val emoji = when(auth) {
                            "PERMITIDO" -> "✓"
                            "DENEGADO" -> "✗"
                            else -> "•"
                        }

                        eventosList.add("$emoji $fecha\n$tipo | $auth")
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { /* Error silencioso */ }
        )
        Volley.newRequestQueue(this).add(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
}