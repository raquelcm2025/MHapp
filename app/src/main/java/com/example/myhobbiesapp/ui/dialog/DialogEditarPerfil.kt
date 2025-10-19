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
import com.example.myhobbiesapp.sesion.SesionActiva

class DialogEditarPerfil : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_editar_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCel    = view.findViewById<EditText>(R.id.etCelular)
        val etPass   = view.findViewById<EditText>(R.id.etClave)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        val uSesion = SesionActiva.usuarioActual
        if (uSesion == null) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // cargar datos actuales
        etCel.setText(uSesion.celular)
        etPass.setText(uSesion.clave)

        val dao = UsuarioDAO(requireContext())

        btnGuardar.setOnClickListener {
            val newCel = etCel.text?.toString()?.trim().orEmpty()
            val newPwd = etPass.text?.toString()?.trim().orEmpty()

            var cambios = 0
            var hayError = false

            // Validación de celular (9 dígitos numéricos)
            if (newCel != uSesion.celular) {
                val soloDigitos = newCel.all { it.isDigit() }
                if (newCel.length != 9 || !soloDigitos) {
                    etCel.error = "Debe tener exactamente 9 dígitos"
                    hayError = true
                } else {
                    val filas = dao.updateCelular(uSesion.id, newCel)
                    if (filas > 0) cambios++
                }
            }

            // Validación de clave
            if (newPwd != uSesion.clave) {
                if (newPwd.length < 6) {
                    etPass.error = "Mínimo 6 caracteres"
                    hayError = true
                } else {
                    val filas = dao.updateClave(uSesion.id, newPwd)
                    if (filas > 0) cambios++
                }
            }

            if (hayError) return@setOnClickListener

            if (cambios > 0) {
                // Refrescar sesión desde la BD
                val actualizado = dao.getById(uSesion.id)
                if (actualizado != null) {
                    SesionActiva.usuarioActual = actualizado
                } else {
                    // Si por alguna razón no lee, al menos actualiza campos en memoria
                    uSesion.celular = newCel.ifEmpty { uSesion.celular }
                    uSesion.clave   = newPwd.ifEmpty { uSesion.clave }
                }

                // Notificar al PerfilFragment para que se actualice de inmediato
                parentFragmentManager.setFragmentResult("perfil_editado", Bundle())

                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Sin cambios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
