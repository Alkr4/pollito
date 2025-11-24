package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class GestionSensoresSimple : AppCompatActivity() {

    private lateinit var listSensores: ListView
    private var sensoresList = ArrayList<HashMap<String, String>>()
    private lateinit var adapter: SimpleAdapter

    private var userId: String? = null
    private var deptId: String? = null
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_sensores_simple)

        userId = intent.getStringExtra("USER_ID")
        deptId = intent.getStringExtra("DEPT_ID")
        userRole = intent.getStringExtra("USER_ROLE")

        listSensores = findViewById(R.id.listSensores)

        adapter = SimpleAdapter(
            this,
            sensoresList,
            android.R.layout.simple_list_item_2,
            arrayOf("titulo", "detalle"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listSensores.adapter = adapter

        listSensores.setOnItemClickListener { _, _, position, _ ->
            val sensor = sensoresList[position]
            mostrarOpcionesSensor(sensor)
        }

        cargarSensores()
    }

    private fun cargarSensores() {
        val url = "${Config.URL_BASE}listar_sensores.php?id_departamento=$deptId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    sensoresList.clear()
                    val status = response.getString("status")

                    if(status == "success"){
                        val sensores = response.getJSONArray("sensores")

                        for(i in 0 until sensores.length()){
                            val s = sensores.getJSONObject(i)
                            val mapa = HashMap<String, String>()

                            val codigo = s.getString("codigo_sensor")
                            val tipo = s.getString("tipo")
                            val estado = s.getString("estado")
                            val usuario = "${s.getString("usuario_nombre")} ${s.getString("usuario_apellido")}"
                            val id = s.getString("id")

                            mapa["titulo"] = "$codigo ($tipo)"
                            mapa["detalle"] = "Estado: $estado | Usuario: $usuario"
                            mapa["id"] = id
                            mapa["estado"] = estado
                            mapa["codigo"] = codigo

                            sensoresList.add(mapa)
                        }
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al cargar sensores", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarOpcionesSensor(sensor: HashMap<String, String>) {
        if(userRole != "administrador"){
            Toast.makeText(this, "Solo administradores pueden modificar sensores", Toast.LENGTH_LONG).show()
            return
        }

        val opciones = arrayOf(
            "Activar",
            "Desactivar",
            "Marcar como Perdido",
            "Bloquear",
            "Eliminar Sensor"
        )

        AlertDialog.Builder(this)
            .setTitle("Gestionar: ${sensor["codigo"]}")
            .setItems(opciones) { _, which ->
                when(which){
                    0 -> cambiarEstadoSensor(sensor["id"]!!, "ACTIVO")
                    1 -> cambiarEstadoSensor(sensor["id"]!!, "INACTIVO")
                    2 -> cambiarEstadoSensor(sensor["id"]!!, "PERDIDO")
                    3 -> cambiarEstadoSensor(sensor["id"]!!, "BLOQUEADO")
                    4 -> confirmarEliminarSensor(sensor["id"]!!)
                }
            }
            .show()
    }

    private fun cambiarEstadoSensor(idSensor: String, nuevoEstado: String) {
        val url = "${Config.URL_BASE}actualizar_estado_sensor.php?" +
                "id_usuario=$userId&id_sensor=$idSensor&estado=$nuevoEstado"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if(status == "success"){
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("¡Actualizado!")
                        .setContentText("Estado cambiado a $nuevoEstado")
                        .show()
                    cargarSensores()
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

    private fun confirmarEliminarSensor(idSensor: String) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Estás seguro?")
            .setContentText("Esta acción no se puede deshacer")
            .setConfirmText("Sí, eliminar")
            .setCancelText("Cancelar")
            .setConfirmClickListener { dialog ->
                eliminarSensor(idSensor)
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun eliminarSensor(idSensor: String) {
        val url = "${Config.URL_BASE}eliminar_sensor.php?id_usuario=$userId&id_sensor=$idSensor"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if(status == "success"){
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Eliminado")
                        .setContentText("Sensor eliminado correctamente")
                        .show()
                    cargarSensores()
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
}