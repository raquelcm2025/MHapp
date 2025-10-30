package com.example.myhobbiesapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.ui.activity.InicioActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private fun prefs() =
        requireContext().getSharedPreferences("mh_prefs", Context.MODE_PRIVATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvSaludo    = view.findViewById<TextView>(R.id.tvSaludo)
        val btnTutorial = view.findViewById<View>(R.id.btnVerTutorial)

        // Saludo
        var nombre = activity?.intent?.getStringExtra("nombreUsuario").orEmpty()
        if (nombre.isBlank()) {
            val idUsuario = activity?.intent?.getIntExtra("idUsuario", -1) ?: -1
            if (idUsuario != -1) {
                UsuarioDAO(requireContext()).getById(idUsuario)?.let { u -> nombre = u.nombre }
            } else if (SesionActiva.usuarioActual != null) {
                nombre = SesionActiva.usuarioActual?.nombre ?: ""
            }
        }
        tvSaludo.text = if (nombre.isNotBlank())
            "Bienvenid@, $nombre a MyHobbiesApp"
        else
            "Bienvenid@ a MyHobbiesApp"

        // Mostrar diálogo para iniciar tour solo la 1ª vez
        if (!prefs().getBoolean("tutorial_inicio_visto", false)) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tutorial rápido")
                .setMessage("Te mostramos las secciones clave (6 pasos).")
                .setPositiveButton("Empezar") { _, _ ->
                    (requireActivity() as? InicioActivity)?.launchOnboardingTour()
                }
                .setNegativeButton("Saltar") { d, _ ->
                    prefs().edit().putBoolean("tutorial_inicio_visto", true).apply()
                    d.dismiss()
                }
                .show()
        }


        btnTutorial.setOnClickListener {
            (requireActivity() as? InicioActivity)?.launchOnboardingTour()
        }

    }
}
