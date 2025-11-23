package com.example.aplicacion_penka_2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import cn.pedant.SweetAlert.SweetAlertDialog

class Lista : AppCompatActivity() {
    private lateinit var list: ListView
    private lateinit var datos: RequestQueue
    private var listaUsuariosCompleta = ArrayList<String>()
    private var listaUsuariosFiltrada = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var etBuscador: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        list = findViewById(R.id.listado_usuarios)
        datos = Volley.newRequestQueue(this)
        etBuscador = findViewById(R.id.etBuscador)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaUsuariosFiltrada)
        list.adapter = adapter

        list.setOnItemClickListener { parent, view, position, id ->
            val usuarioSeleccionado = listaUsuariosFiltrada[position]

            val partes = usuarioSeleccionado.split("|")

            if (partes.size >= 5) {
                val intent = Intent(this, ModificarEliminar::class.java).apply {
                    putExtra("id", partes[0])
                    putExtra("nombre", partes[1])
                    putExtra("apellido", partes[2])
                    putExtra("email", partes[3])
                    putExtra("telefono", partes[4])
                }
                startActivity(intent)
            } else {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al procesar los datos del usuario.")
                    .show()
            }
        }

        etBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLista(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filtrarLista(texto: String) {
        listaUsuariosFiltrada.clear()
        if (texto.isEmpty()) {
            listaUsuariosFiltrada.addAll(listaUsuariosCompleta)
        } else {
            val textoBusqueda = texto.lowercase()
            for (usuario in listaUsuariosCompleta) {
                val partes = usuario.split("|")
                if (partes.size > 2) {
                    val nombreCompleto = "${partes[1]} ${partes[2]}".lowercase()
                    if (nombreCompleto.contains(textoBusqueda)) {
                        listaUsuariosFiltrada.add(usuario)
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        cargaLista()
    }

    private fun cargaLista() {
        listaUsuariosCompleta.clear()
        val url = "http://18.211.13.143/consultas.php"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONArray(response)
                    for (i in 0 until json.length()) {
                        val usuarios = json.getJSONObject(i)
                        val linea =
                            "${usuarios.getString("id")}|${usuarios.getString("nombre")}|${usuarios.getString("apellido")}|${usuarios.getString("email")}|${usuarios.getString("telefono")}"

                        listaUsuariosCompleta.add(linea)
                    }

                    filtrarLista(etBuscador.text.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        datos.add(request)
    }
}