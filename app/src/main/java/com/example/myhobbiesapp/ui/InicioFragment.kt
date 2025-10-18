package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.data.UsuarioDAO

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvSaludo = view.findViewById<TextView>(R.id.tvSaludo)
        val btnExplora = view.findViewById<View>(R.id.btnIrExplora)

        //  Obtener nombre del usuario logueado
        var nombre = activity?.intent?.getStringExtra("nombreUsuario").orEmpty()
        if (nombre.isBlank()) {
            val idUsuario = activity?.intent?.getIntExtra("idUsuario", -1) ?: -1
            if (idUsuario != -1) {
                UsuarioDAO(requireContext()).getById(idUsuario)?.let { u ->
                    nombre = u.nombre
                }
            } else if (SesionActiva.usuarioActual != null) {
                nombre = SesionActiva.usuarioActual?.nombre ?: ""
            }
        }

        tvSaludo.text = if (nombre.isNotBlank())
            "Bienvenid@, $nombre a MyHobbiesApp"
        else
            "Bienvenid@ a MyHobbiesApp"

        // reemplaza el fragment d
        btnExplora.setOnClickListener {
            try {
                val frag = ExploraFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, frag)
                    .addToBackStack("explora_desde_inicio")
                    .commitAllowingStateLoss()
                Toast.makeText(requireContext(), "Abriendo Explora…", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al abrir Explora: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

    }
}
