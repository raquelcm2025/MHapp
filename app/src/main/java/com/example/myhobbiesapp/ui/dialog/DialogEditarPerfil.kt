package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.util.SecurityUtils
import com.example.myhobbiesapp.util.SessionManager
import com.example.myhobbiesapp.sesion.SesionActiva

class DialogEditarPerfil : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_editar_perfil, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etCel = view.findViewById<EditText>(R.id.etCelular)
        val etPass = view.findViewById<EditText>(R.id.etClave)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        val dao = UsuarioDAO(requireContext())

        val emailActual = SessionManager.getCurrentEmail(requireContext())
        val uSesion = if (emailActual != null) dao.getByCorreo(emailActual) else null
            ?: SesionActiva.usuarioActual // fallback si aún se usa este objeto

        if (uSesion == null) {
            Toast.makeText(requireContext(), "Sesión no disponible", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        etCel.setText(uSesion.celular)
        etPass.setText("")

        btnGuardar.setOnClickListener {
            val nuevoCel = etCel.text?.toString()?.trim().orEmpty()
            val nuevaClave = etPass.text?.toString()?.trim().orEmpty()

            var ok = true

            if (nuevoCel != uSesion.celular) {
                val soloDigitos = nuevoCel.all { it.isDigit() }
                if (nuevoCel.length != 9 || !soloDigitos) {
                    etCel.error = "Debe tener 9 dígitos"
                    ok = false
                }
            }

            if (nuevaClave.isNotEmpty() && nuevaClave.length < 6) {
                etPass.error = "Mínimo 6 caracteres"
                ok = false
            }

            if (!ok) return@setOnClickListener

            var cambios = 0
            if (nuevoCel != uSesion.celular) {
                cambios += dao.updateCelular(uSesion.id, nuevoCel)
            }
            if (nuevaClave.isNotEmpty()) {
                val hash = SecurityUtils.sha256(nuevaClave)
                cambios += dao.updateClave(uSesion.id, hash)
            }

            if (cambios > 0) {
                val userRefrescado = dao.getById(uSesion.id)
                if (userRefrescado != null) {
                    SesionActiva.usuarioActual = userRefrescado
                    SessionManager.saveCurrentEmail(requireContext(), userRefrescado.correo)
                }
                parentFragmentManager.setFragmentResult("perfil_editado", Bundle())
                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Sin cambios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
