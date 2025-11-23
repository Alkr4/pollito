package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
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

        tvClock = findViewById(R.id.tvClock)

        findViewById<Button>(R.id.btn_crud).setOnClickListener {
            startActivity(Intent(this, Crud::class.java))
        }

        findViewById<Button>(R.id.btn_sensores).setOnClickListener {
            startActivity(Intent(this, Sensores::class.java))
        }

        findViewById<Button>(R.id.btn_desarrollador).setOnClickListener {
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