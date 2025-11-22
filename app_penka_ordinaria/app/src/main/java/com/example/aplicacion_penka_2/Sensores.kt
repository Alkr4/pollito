package com.example.aplicacion_penka_2

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Sensores : AppCompatActivity() {

    private var luzEncendida = false
    private var linternaEncendida = false

    private lateinit var txtFechaHora: TextView
    private lateinit var txtTemperatura: TextView
    private lateinit var txtHumedad: TextView
    private lateinit var iconTemperatura: ImageView
    private lateinit var iconLuz: ImageView
    private lateinit var iconLinterna: ImageView

    private lateinit var datos: RequestQueue
    private val apiHandler = Handler(Looper.getMainLooper())
    private lateinit var apiRunnable: Runnable

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        txtFechaHora = findViewById(R.id.txtFechaHora)
        txtTemperatura = findViewById(R.id.txtTemperatura)
        txtHumedad = findViewById(R.id.txtHumedad)
        iconTemperatura = findViewById(R.id.iconTemperatura)
        iconLuz = findViewById(R.id.iconLuz)
        iconLinterna = findViewById(R.id.iconLinterna)

        datos = Volley.newRequestQueue(this)

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception) {
            e.printStackTrace()
            iconLinterna.isEnabled = false
        }

        iconTemperatura.setImageResource(R.drawable.ic_temp_moderada)

        iconLinterna.setOnClickListener {
            controlarLinterna()
        }

        iconLuz.setOnClickListener {
            luzEncendida = !luzEncendida
            iconLuz.setImageResource(
                if (luzEncendida) R.drawable.ic_luz_on else R.drawable.ic_luz_off
            )
        }
        iniciarConsultaAPI()
    }

    private fun controlarLinterna() {
        if (cameraId == null) return
        try {
            linternaEncendida = !linternaEncendida

            cameraManager.setTorchMode(cameraId!!, linternaEncendida)

            iconLinterna.setImageResource(
                if (linternaEncendida) R.drawable.ic_linterna_on else R.drawable.ic_linterna_off
            )

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Hecho!")
                .setContentText("Linterna ${if (linternaEncendida) "Encendida" else "Apagada"}")
                .show()

        } catch (e: CameraAccessException) {
            e.printStackTrace()
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudo acceder a la linterna.")
                .show()
        }
    }

    private fun iniciarConsultaAPI() {
        apiRunnable = Runnable {
            consultarAPI()
            apiHandler.postDelayed(apiRunnable, 2000)
        }
        apiHandler.post(apiRunnable)
    }

    private fun consultarAPI() {
        val url = "https://www.pnk.cl/muestra_datos.php"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    txtFechaHora.text = dateFormat.format(Date())

                    val temperatura = response.getDouble("temperatura")
                    val humedad = response.getDouble("humedad")

                    txtTemperatura.text = "Temperatura\n${String.format("%.1f", temperatura)}°C"
                    txtHumedad.text = "Humedad\n${String.format("%.1f", humedad)}%"

                    if (temperatura > 20.0) {
                        iconTemperatura.setImageResource(R.drawable.ic_temp_high)
                    } else {
                        iconTemperatura.setImageResource(R.drawable.ic_temp_low)
                    }

                } catch (e: JSONException) {
                    txtFechaHora.text = "Error de JSON"
                }
            },
            { error ->
                txtFechaHora.text = "Error de API"
            }
        )
        datos.add(request)
    }

    override fun onPause() {
        super.onPause()
        apiHandler.removeCallbacks(apiRunnable)


        if (linternaEncendida && cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId!!, false)
            } catch (e: CameraAccessException) { }
            linternaEncendida = false
        }
    }

    override fun onResume() {
        super.onResume()
        iniciarConsultaAPI()

        iconLinterna.setImageResource(R.drawable.ic_linterna_off)
        linternaEncendida = false
    }
}