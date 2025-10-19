package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.entity.Usuario
import com.example.myhobbiesapp.sesion.SesionActiva
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AccesoActivity : AppCompatActivity(R.layout.activity_acceso) {

    private val dominios = setOf("mh.pe", "gmail.com", "hotmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Llama a la función local para asegurar los usuarios de prueba.
        seedIfEmpty()

        val etCorreo = findViewById<TextInputEditText>(R.id.tietUsuario)
        val etClave  = findViewById<TextInputEditText>(R.id.tietClave)
        val btnInicio = findViewById<MaterialButton>(R.id.btnInicio)
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        btnInicio.setOnClickListener {
            val correo = etCorreo.text?.toString()?.trim().orEmpty()
            val clave  = etClave.text?.toString()?.trim().orEmpty()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dom = correo.substringAfter("@").lowercase()
            if (dom !in dominios) {
                Toast.makeText(this, "Dominio no permitido. Usa @mh.pe @gmail.com @hotmail.com", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dao = UsuarioDAO(this)
            // uso de getByCorreo para verificar la existencia.
            val u = dao.getByCorreo(correo)

            if (u == null) {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (u.clave != clave) {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SesionActiva.usuarioActual = u
            startActivity(
                Intent(this, InicioActivity::class.java).apply {
                    putExtra("idUsuario", u.id)
                    putExtra("nombreUsuario", u.nombre)
                }
            )
            finish()
        }

        tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun seedIfEmpty() {
        val dao = UsuarioDAO(this)

        if (dao.getAll().isNotEmpty()) return

        val base = listOf(
            Usuario(
                0,
                "Susana Luzmila ",
                "Martinez León",
                "susana@mh.pe",
                "907343431",
                "123456",
                "Femenino",
                true,
                R.mipmap.hobby_fotografia
            ),
            Usuario(
                0,
                "Ana Camila",
                "Rivera López",
                "ana@gmail.com",
                "904111222",
                "clave123",
                "Femenino",
                true,
                R.mipmap.hobby_yoga
            ),
            Usuario(
                0,
                "Carlos Eduardo",
                "Vega Torres",
                "carlos@hotmail.com",
                "911555222",
                "clave246",
                "Masculino",
                true,
                R.mipmap.hobby_guitarra
            )
        )
        base.forEach { u ->
            val dom = u.correo.substringAfter("@").lowercase()
            if (dom in dominios && dao.getByCorreo(u.correo) == null) dao.insert(u)
        }
    }
}