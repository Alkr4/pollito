package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminPanel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel) // Links to your XML

        // Button 1: Manage Departments
        findViewById<Button>(R.id.btnGestDeptos).setOnClickListener {
            startActivity(Intent(this, AdminDepartamentos::class.java))
        }

        // Button 2: Register People
        findViewById<Button>(R.id.btnGestUsuarios).setOnClickListener {
            startActivity(Intent(this, AdminUsuarios::class.java))
        }

        // Button 3: Assign Sensors
        findViewById<Button>(R.id.btnGestSensores).setOnClickListener {
            // Pass the User ID so we know who is doing the assigning
            val intent = Intent(this, GestionSensoresSimple::class.java)
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID")) // Pass it along if needed
            startActivity(intent)
        }
    }
}