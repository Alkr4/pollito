package com.example.aplicacion_penka_2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class GestionSensores : AppCompatActivity() {

    private lateinit var etMac: EditText
    private lateinit var spTipo: Spinner
    private lateinit var spUsuarios: Spinner // Select User
    private lateinit var btnGuardar: Button
    private lateinit var listSensores: ListView

    // For the User Spinner
    private var usuariosMap = HashMap<String, String>() // Name -> ID
    private var selectedUserId: String = ""
    private var userNamesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores)

        etMac = findViewById(R.id.etMacSensor)
        spTipo = findViewById(R.id.spTipo)
        spUsuarios = findViewById(R.id.spUsuarios)
        btnGuardar = findViewById(R.id.btnGuardarSensor)
        listSensores = findViewById(R.id.listSensores)

        // 1. Setup Type Spinner
        val tipos = arrayOf("Llavero", "Tarjeta", "Etiqueta")
        spTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        // 2. Load Users into Spinner (The "Scenario B" requirement)
        cargarUsuariosSpinner()

        // 3. Handle User Selection
        spUsuarios.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p4: Long) {
                val name = userNamesList[position]
                selectedUserId = usuariosMap[name] ?: ""
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // 4. Save Button
        btnGuardar.setOnClickListener {
            if (selectedUserId.isNotEmpty()) {
                registrarSensor(selectedUserId)
            } else {
                Toast.makeText(this, "Seleccione un usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Load existing sensors list...
        // cargarSensores()
    }

    private fun cargarUsuariosSpinner() {
        val url = "${Config.URL_BASE}listar_usuarios_simple.php"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                userNamesList.clear()
                usuariosMap.clear()
                for (i in 0 until response.length()) {
                    val u = response.getJSONObject(i)
                    val nombre = "${u.getString("nombre")} (${u.getString("rut")})"
                    val id = u.getString("id")

                    userNamesList.add(nombre)
                    usuariosMap[nombre] = id
                }
                // Attach to Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, userNamesList)
                spUsuarios.adapter = adapter
            },
            { error -> Toast.makeText(this, "Error cargando usuarios", Toast.LENGTH_SHORT).show() }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun registrarSensor(idUsuario: String) {
        val mac = etMac.text.toString()
        val tipo = spTipo.selectedItem.toString()

        // Send the SELECTED user ID to the database
        val url = "${Config.URL_BASE}registrar_sensor.php?id_usuario=$idUsuario&MAC=$mac&tipo=$tipo&id_departamento=1" // You might want to fetch dept dynamically if needed

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Sensor asignado correctamente", Toast.LENGTH_SHORT).show()
                etMac.setText("")
            },
            { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show() }
        )
        Volley.newRequestQueue(this).add(request)
    }
}