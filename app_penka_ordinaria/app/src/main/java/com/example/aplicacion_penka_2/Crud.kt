package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Crud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        findViewById<Button>(R.id.btn_ingresar_usuario).setOnClickListener{
            startActivity(Intent(this, Ingreso::class.java))
        }
        findViewById<Button>(R.id.btn_listar_usuarios).setOnClickListener{
            startActivity(Intent(this, Lista::class.java))
        }
    }
}