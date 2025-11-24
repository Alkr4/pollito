package com.example.aplicacion_penka_2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class AdminUsuarios : AppCompatActivity() {

    // UI - Registro
    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRut: EditText
    private lateinit var etPass: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDeptoID: EditText
    private lateinit var switchAdmin: Switch
    private lateinit var btnCrear: Button

    // UI - Lista
    private lateinit var listUsuarios: ListView
    private lateinit var btnRefrescar: Button

    // Data
    private var usuariosList = ArrayList<HashMap<String, String>>()
    private lateinit var usuariosAdapter: SimpleAdapter
    private var idAdmin: String = ""
    private var idDepartamento: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_usuarios)

        // Get Admin ID
        idAdmin = intent.getStringExtra("USER_ID") ?: ""
        idDepartamento = intent.getStringExtra("DEPT_ID") ?: ""

        // Bind Views
        etNombre = findViewById(R.id.etAdminNombre)
        etEmail = findViewById(R.id.etAdminEmail)
        etRut = findViewById(R.id.etAdminRut)
        etPass = findViewById(R.id.etAdminPass)
        etTelefono = findViewById(R.id.etAdminTelefono)
        etDeptoID = findViewById(R.id.etAdminDeptoID)
        switchAdmin = findViewById(R.id.switchEsAdmin)
        btnCrear = findViewById(R.id.btnAdminCrear)
        listUsuarios = findViewById(R.id.listUsuarios)
        btnRefrescar = findViewById(R.id.btnRefrescarUsuarios)

        // Pre-fill department ID
        etDeptoID.setText(idDepartamento)

        // Buttons
        btnCrear.setOnClickListener {
            if (validarCampos()) {
                crearUsuario()
            }
        }

        btnRefrescar.setOnClickListener {
            cargarUsuarios()
        }

        // List Click
        listUsuarios.setOnItemClickListener { _, _, position, _ ->
            mostrarOpcionesUsuario(usuariosList[position])
        }

        // Load Users
        cargarUsuarios()
    }

    private fun validarCampos(): Boolean {
        if (etNombre.text.isEmpty() || etEmail.text.isEmpty() ||
            etRut.text.isEmpty() || etPass.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etPass.text.length < 8) {
            Toast.makeText(this, "Contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun crearUsuario() {
        val rol = if (switchAdmin.isChecked) "administrador" else "operador"
        val telefono = if (etTelefono.text.isEmpty()) "00000000" else etTelefono.text.toString()

        val url = "${Config.URL_BASE}admin_crear_usuario.php?" +
                "nombre=${etNombre.text}&email=${etEmail.text}" +
                "&password=${etPass.text}&rut=${etRut.text}" +
                "&telefono=$telefono" +
                "&id_departamento=${etDeptoID.text}&rol=$rol&id_admin=$idAdmin"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                if (response.getString("status") == "success") {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Usuario Creado")
                        .setContentText("Rol asignado: $rol")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            limpiarCampos()
                            cargarUsuarios()
                        }
                        .show()
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun cargarUsuarios() {
        val url = "${Config.URL_BASE}consultas.php?id_departamento=$idDepartamento&id_usuario=$idAdmin"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    usuariosList.clear()
                    val status = response.getString("status")

                    if (status == "success") {
                        val usuarios = response.getJSONArray("usuarios")

                        for (i in 0 until usuarios.length()) {
                            val user = usuarios.getJSONObject(i)
                            val map = HashMap<String, String>()

                            map["id"] = user.getString("id")
                            map["nombre"] = "${user.getString("nombre")} ${user.getString("apellido")}"
                            map["email"] = user.getString("email")
                            map["rol"] = user.getString("privilegios")
                            map["estado"] = user.getString("estado")
                            map["telefono"] = user.getString("telefono")

                            usuariosList.add(map)
                        }
                    }

                    usuariosAdapter = SimpleAdapter(
                        this,
                        usuariosList,
                        R.layout.item_usuario,
                        arrayOf("nombre", "email", "rol", "estado"),
                        intArrayOf(R.id.tvNombreUsuario, R.id.tvEmailUsuario, R.id.tvRolUsuario, R.id.tvEstadoUsuario)
                    )
                    listUsuarios.adapter = usuariosAdapter

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error cargando usuarios", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarOpcionesUsuario(usuario: HashMap<String, String>) {
        // Don't allow admin to modify themselves
        if (usuario["id"] == idAdmin) {
            Toast.makeText(this, "No puedes modificarte a ti mismo", Toast.LENGTH_SHORT).show()
            return
        }

        val opciones = arrayOf("Activar", "Desactivar", "Bloquear", "Eliminar")

        AlertDialog.Builder(this)
            .setTitle("Gestionar: ${usuario["nombre"]}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> cambiarEstadoUsuario(usuario["id"]!!, "ACTIVO")
                    1 -> cambiarEstadoUsuario(usuario["id"]!!, "INACTIVO")
                    2 -> cambiarEstadoUsuario(usuario["id"]!!, "BLOQUEADO")
                    3 -> eliminarUsuario(usuario["id"]!!)
                }
            }
            .show()
    }

    private fun cambiarEstadoUsuario(idUsuario: String, nuevoEstado: String) {
        val url = "${Config.URL_BASE}admin_actualizar.php?" +
                "id_usuario_target=$idUsuario&nuevo_estado=$nuevoEstado&id_usuario_ejecutor=$idAdmin"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                if (response.getString("status") == "success") {
                    Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show()
                    cargarUsuarios()
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun eliminarUsuario(idUsuario: String) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Eliminar usuario?")
            .setContentText("Se eliminarán todos sus sensores asociados")
            .setConfirmText("Eliminar")
            .setCancelText("Cancelar")
            .showCancelButton(true)
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()

                val url = "${Config.URL_BASE}eliminar_usuario.php?id_usuario=$idUsuario&id_admin=$idAdmin"

                val request = JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        if (response.getString("status") == "success") {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Eliminado")
                                .setContentText(response.getString("message"))
                                .show()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    },
                    { error ->
                        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
                Volley.newRequestQueue(this).add(request)
            }
            .show()
    }

    private fun limpiarCampos() {
        etNombre.setText("")
        etEmail.setText("")
        etRut.setText("")
        etPass.setText("")
        etTelefono.setText("")
        switchAdmin.isChecked = false
    }
}