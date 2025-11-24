package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Menu : AppCompatActivity() {

    private lateinit var tvClock: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Retrieve Data
        val userId = intent.getStringExtra("USER_ID")
        val userRole = intent.getStringExtra("USER_ROLE")
        val deptId = intent.getStringExtra("DEPT_ID")

        tvClock = findViewById(R.id.tvClock)
        val btnCrud = findViewById<Button>(R.id.btn_crud) // User Management
        val btnSensores = findViewById<Button>(R.id.btn_sensores) // Barrier & History
        val btnDev = findViewById<Button>(R.id.btn_desarrollador)

        // This button logic assumes you might add a new button for Sensor Management
        // or reuse the existing CRUD button for Admins only.

        // Look for this part in your onCreate:
        if (userRole == "administrador") {
            btnCrud.visibility = View.VISIBLE
            btnCrud.text = "Panel de Administración" // Update text if you want

            btnCrud.setOnClickListener {
                // CHANGE THIS LINE:
                // Old: startActivity(Intent(this, Crud::class.java))

                // New: Go to the new Dashboard
                startActivity(Intent(this, AdminPanel::class.java))
            }
        } else {
            btnCrud.visibility = View.GONE // Hide User CRUD for Operators
        }

        // Everyone can access Barrier Control
        btnSensores.text = "Control Acceso / Barrera"
        btnSensores.setOnClickListener {
            val intent = Intent(this, Sensores::class.java)
            intent.putExtra("DEPT_ID", deptId)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        btnDev.setOnClickListener {
            startActivity(Intent(this, Desarrollador::class.java))
        }
        startClock()
    }

    private fun startClock() {
        clockRunnable = Runnable {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            tvClock.text = dateFormat.format(Date())
            handler.postDelayed(clockRunnable, 1000)
        }
        handler.post(clockRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(clockRunnable)
    }
}