package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.core.UserStore

class InicioFragment : Fragment(R.layout.fragment_inicio) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvSaludo = view.findViewById<android.widget.TextView>(R.id.tvSaludo)
        val btnIr = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnIrExplora)

        val user = UserStore.getLogged(requireContext())
        tvSaludo.text = if (user != null) "¡Hola, ${user.nombres}!" else "¡Hola!"

        btnIr.setOnClickListener {
            (activity as? InicioActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragments, ExploraFragment())
                    .commit()
                it.findViewById<com.google.android.material.navigation.NavigationView>(R.id.nvMenu)
                    .setCheckedItem(R.id.itExplora)
                it.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).title = "Explora"
            }
        }
    }
}
