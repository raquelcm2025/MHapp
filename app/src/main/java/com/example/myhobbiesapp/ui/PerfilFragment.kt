package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.google.android.material.button.MaterialButton

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // opcional pero recomendado

        val tvNombre: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvCorreo: TextView = view.findViewById(R.id.tvCorreo)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditarPerfil)
        // Si en tu XML usaste <Button> normal:
        // val btnEditar: Button = view.findViewById(R.id.btnEditarPerfil)

        tvNombre.text = "Raquel"
        tvCorreo.text = "raquelcm@mh.pe"

        btnEditar.setOnClickListener {
            // abrir pantalla de edición o mostrar dialog
        }
    }
}
