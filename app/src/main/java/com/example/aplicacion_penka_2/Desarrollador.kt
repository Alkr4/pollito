package com.example.aplicacion_penka_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Desarrollador : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desarrollador)

        findViewById<Button>(R.id.btnGithub2).setOnClickListener {
            abrirEnlace("https://github.com/imVic-byte")
        }

        findViewById<Button>(R.id.btnGithub).setOnClickListener {
            abrirEnlace("https://github.com/Alkr4")
        }
    }

    private fun abrirEnlace(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}