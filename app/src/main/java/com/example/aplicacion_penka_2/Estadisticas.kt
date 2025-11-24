package com.example.aplicacion_penka_2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*

class Estadisticas : AppCompatActivity() {

    // UI Elements
    private lateinit var tvTotalUsuarios: TextView
    private lateinit var tvTotalSensores: TextView
    private lateinit var tvAccesosHoy: TextView
    private lateinit var tvFechaActual: TextView
    private lateinit var tvHoraActual: TextView
    private lateinit var btnRefrescar: Button

    // Data
    private var idDepartamento: String = ""
    private var nombreDepartamento: String = ""

    // Auto-refresh
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var refreshRunnable: Runnable
    private val REFRESH_INTERVAL = 5000L // 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        // Recibir datos del Intent
        idDepartamento = intent.getStringExtra("DEPT_ID") ?: "1"
        nombreDepartamento = intent.getStringExtra("DEPT_NAME") ?: "Departamento"

        // Bind Views
        tvTotalUsuarios = findViewById(R.id.tvTotalUsuarios)
        tvTotalSensores = findViewById(R.id.tvTotalSensores)
        tvAccesosHoy = findViewById(R.id.tvAccesosHoy)
        tvFechaActual = findViewById(R.id.tvFechaActual)
        tvHoraActual = findViewById(R.id.tvHoraActual)
        btnRefrescar = findViewById(R.id.btnRefrescar)

        // Set Department Name
        findViewById<TextView>(R.id.tvDepartamento).text = nombreDepartamento

        // Button Listener
        btnRefrescar.setOnClickListener {
            cargarEstadisticas()
        }

        // Initial Load
        cargarEstadisticas()
        iniciarReloj()
        iniciarAutoRefresh()
    }

    private fun cargarEstadisticas() {
        val url = "${Config.URL_BASE}estadisticas_departamento.php?id_departamento=$idDepartamento"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        // Animar los números
                        animarContador(tvTotalUsuarios, response.getInt("usuarios"))
                        animarContador(tvTotalSensores, response.getInt("sensores"))
                        animarContador(tvAccesosHoy, response.getInt("accesos_hoy"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun animarContador(textView: TextView, valorFinal: Int) {
        val valorInicial = textView.text.toString().toIntOrNull() ?: 0
        val duracion = 500 // ms
        val pasos = 20
        val incremento = (valorFinal - valorInicial).toFloat() / pasos
        var pasoActual = 0

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (pasoActual < pasos) {
                        val nuevoValor = (valorInicial + incremento * pasoActual).toInt()
                        textView.text = nuevoValor.toString()
                        pasoActual++
                    } else {
                        textView.text = valorFinal.toString()
                        timer.cancel()
                    }
                }
            }
        }, 0, duracion.toLong() / pasos)
    }

    private fun iniciarReloj() {
        val clockRunnable = object : Runnable {
            override fun run() {
                val ahora = Date()
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

                tvFechaActual.text = formatoFecha.format(ahora)
                tvHoraActual.text = formatoHora.format(ahora)

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(clockRunnable)
    }

    private fun iniciarAutoRefresh() {
        refreshRunnable = object : Runnable {
            override fun run() {
                cargarEstadisticas()
                handler.postDelayed(this, REFRESH_INTERVAL)
            }
        }
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}