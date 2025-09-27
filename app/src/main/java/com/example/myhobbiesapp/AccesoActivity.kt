package com.example.myhobbiesapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.ListaHobbiesActivity
import com.example.myhobbiesapp.RegistroActivity
import com.example.myhobbiesapp.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AccesoActivity : AppCompatActivity() {

    private var tvRegistro: TextView? = null
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var btnAcceso: Button

    private val listaUsuarios = mutableListOf(
        Usuario(1, "Raquel", "Callata", "raquel@mh.pe", "12345"),
        Usuario(2, "Prueba", "Cibertec", "prueba@mh.pe", "00000")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)

        // Vincular IDs del XML
        tvRegistro  = findViewById(R.id.tvRegistro)
        tietCorreo  = findViewById(R.id.tietCorreo)
        tietClave   = findViewById(R.id.tietClave)
        tilCorreo   = findViewById(R.id.tilCorreo)
        tilClave    = findViewById(R.id.tilClave)
        btnAcceso   = findViewById(R.id.btnInicio)

        // Clicks
        btnAcceso.setOnClickListener {
            validarCampos()
        }
        tvRegistro?.setOnClickListener {
            cambioActivity(RegistroActivity::class.java)
        }
    }

    private fun validarCampos() {
        val correo = tietCorreo.text?.toString()?.trim().orEmpty() // ← SOLO lo escrito
        val clave  = tietClave.text?.toString()?.trim().orEmpty()
        var hayError = false

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            hayError = true
        } else {
            tilCorreo.error = null
        }

        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            hayError = true
        } else {
            tilClave.error = null
        }

        if (hayError) return

        var usuarioEncontrado: Usuario? = null
        for (u in listaUsuarios) {
            if (u.correo.equals(correo, ignoreCase = true) && u.clave == clave) {
                usuarioEncontrado = u
                break
            }
        }

        if (usuarioEncontrado != null) {
            Toast.makeText(this, "Bienvenida/o ${usuarioEncontrado.nombres}", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, ListaHobbiesActivity::class.java))
            // finish() // no regresar al login (clic <---)
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }
    }

    private fun cambioActivity(activityDestino: Class<out Activity>) {
        startActivity(Intent(this, activityDestino))
    }
}
