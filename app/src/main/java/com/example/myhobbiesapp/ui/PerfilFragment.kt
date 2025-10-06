package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.core.HobbiesStore
import com.example.myhobbiesapp.core.UserStore
import com.google.android.material.chip.Chip

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvNombre = view.findViewById<android.widget.TextView>(R.id.tvNombre)
        val tvCorreo = view.findViewById<android.widget.TextView>(R.id.tvCorreo)
        val chips = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipsHobbies)
        val btnEditar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditarHobbies)
        val btnLogout = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCerrarSesion)

        val user = UserStore.getLogged(requireContext())
        if (user == null) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }

        tvNombre.text = "${user.nombres} ${user.apellidos}"
        tvCorreo.text = user.correo
        renderChips(chips, user.hobbies)

        btnEditar.setOnClickListener { showHobbiesDialog(chips) }
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas salir?")
                .setPositiveButton("Sí") { _, _ ->
                    UserStore.logout(requireContext())
                    requireActivity().startActivity(
                        android.content.Intent(requireContext(), com.example.myhobbiesapp.AccesoActivity::class.java)
                    )
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun renderChips(group: com.google.android.material.chip.ChipGroup, hobbies: List<String>) {
        group.removeAllViews()
        if (hobbies.isEmpty()) {
            val chip = Chip(requireContext())
            chip.text = "Sin hobbies"
            chip.isEnabled = false
            group.addView(chip)
            return
        }
        hobbies.forEach {
            val chip = Chip(requireContext())
            chip.text = it
            chip.isCheckable = false
            group.addView(chip)
        }
    }

    private fun showHobbiesDialog(chipsGroup: com.google.android.material.chip.ChipGroup) {
        val all = HobbiesStore.todos
        val current = UserStore.getLogged(requireContext()) ?: return
        val selected = current.hobbies.toMutableSet()
        val checked = all.map { it in selected }.toBooleanArray()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Elige tus hobbies")
            .setMultiChoiceItems(all.toTypedArray(), checked) { _, which, isChecked ->
                val h = all[which]
                if (isChecked) selected.add(h) else selected.remove(h)
            }
            .setPositiveButton("Guardar") { d, _ ->
                val updated = current.copy(hobbies = selected.toList())
                UserStore.saveUser(requireContext(), updated)
                renderChips(chipsGroup, updated.hobbies)
                d.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Limpiar") { d, _ ->
                val updated = current.copy(hobbies = emptyList())
                UserStore.saveUser(requireContext(), updated)
                renderChips(chipsGroup, emptyList())
                d.dismiss()
            }
            .show()
    }
}
