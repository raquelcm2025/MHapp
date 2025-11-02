package com.example.myhobbiesapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.ui.activity.InicioActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private fun prefs() =
        requireContext().getSharedPreferences("mh_prefs", Context.MODE_PRIVATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvSaludo = view.findViewById<TextView>(R.id.tvSaludo)
        val btnTutorial = view.findViewById<View>(R.id.btnVerTutorial)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseDb.getUserProfile(uid) { profile ->
                if (isAdded) {
                    val nombre = profile?.nombre?.ifBlank { "Usuario" } ?: "Usuario"
                    tvSaludo.text = "Bienvenid@, $nombre a MyHobbiesApp"
                }
            }
        } else {
            tvSaludo.text = "Bienvenid@ a MyHobbiesApp"
        }


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
                .setCancelable(false)
                .show()
        }

        btnTutorial.setOnClickListener {
            (requireActivity() as? InicioActivity)?.launchOnboardingTour()
        }
    }
}