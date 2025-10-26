package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.ActivityPerfilBinding
import com.example.myhobbiesapp.util.SessionManager

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = SessionManager.getCurrentEmail(this)
        val usuario = if (email != null) UsuarioDAO(this).getByCorreo(email) else null

        if (usuario != null) {
            val nombreCompleto = "${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}".trim()
            binding.tvNombreCompleto.text = nombreCompleto
            binding.tvCorreo.text = usuario.correo
            binding.tvCelular.text = usuario.celular
            binding.tvGenero.text = usuario.genero ?: ""

            val fotoRes = when (usuario.genero?.lowercase()) {
                "femenino", "mujer" -> R.drawable.ic_mujer
                "masculino", "hombre" -> R.drawable.ic_hombre
                else -> R.drawable.ic_person
            }
            binding.ivFoto.setImageResource(fotoRes)
        } else {
            binding.tvNombreCompleto.text = "Sin sesión"
        }
    }
}
