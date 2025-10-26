package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.ActivityAccesoBinding
import com.example.myhobbiesapp.util.SecurityUtils
import com.example.myhobbiesapp.util.SessionManager

class AccesoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccesoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccesoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInicio.setOnClickListener {
            val correo = binding.tietUsuario.text?.toString()?.trim().orEmpty()
            val clavePlano = binding.tietClave.text?.toString()?.trim().orEmpty()

            if (!isCorreoPermitido(correo)) {
                binding.tietUsuario.error = "Correo inválido"
                return@setOnClickListener
            }
            if (clavePlano.isEmpty()) {
                binding.tietClave.error = "Ingresa tu clave"
                return@setOnClickListener
            }

            val claveHash = SecurityUtils.sha256(clavePlano)
            val usuario = UsuarioDAO(this).getByCorreo(correo)
            if (usuario != null && usuario.claveHash == claveHash) {
                SessionManager.saveCurrentEmail(this, correo)
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun isCorreoPermitido(correo: String): Boolean {
        if (!correo.contains("@")) return false
        val dominios = listOf("@mh.pe", "@hotmail.com", "@gmail.com")
        val okDom = dominios.any { correo.endsWith(it, ignoreCase = true) }
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return okDom && regex.matches(correo)
    }
}
