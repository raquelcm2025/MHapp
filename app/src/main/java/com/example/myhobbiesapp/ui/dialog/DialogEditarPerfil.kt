package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.util.SecurityUtils
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.textfield.TextInputEditText

class DialogEditarPerfil : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.MaterialAlertDialog_Material3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return inflater.inflate(R.layout.dialog_editar_perfil, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val etCel  = v.findViewById<TextInputEditText>(R.id.etCelular)
        val etPass = v.findViewById<TextInputEditText>(R.id.etClave)
        val btn    = v.findViewById<Button>(R.id.btnGuardar)

        // Cargar datos actuales
        val email = SessionManager.getCurrentEmail(requireContext())
        val dao   = UsuarioDAO(requireContext())
        val u     = if (email != null) dao.getByCorreo(email) else null

        if (u == null) {
            Toast.makeText(requireContext(), "Sesión no disponible", Toast.LENGTH_SHORT).show()
            dismiss(); return
        }

        etCel.setText(u.celular)
        etPass.setText("")

        btn.setOnClickListener {
            val nuevoCel = etCel.text?.toString()?.trim().orEmpty()
            val nuevaPwd = etPass.text?.toString()?.trim().orEmpty()

            var ok = true

            // Validación: celular exactamente 9 dígitos
            if (nuevoCel.length != 9 || !nuevoCel.all { it.isDigit() }) {
                etCel.error = "Debe tener exactamente 9 dígitos"
                ok = false
            } else etCel.error = null

            // Validación: contraseña opcional, pero si escribe, mínimo 6
            if (nuevaPwd.isNotEmpty() && nuevaPwd.length < 6) {
                etPass.error = "Mínimo 6 caracteres"
                ok = false
            } else etPass.error = null

            if (!ok) return@setOnClickListener

            var cambios = 0

            // Actualizar celular si cambió
            if (nuevoCel != u.celular) {
                cambios += dao.updateCelular(u.id, nuevoCel)
            }

            // Actualizar contraseña si escribió algo
            if (nuevaPwd.isNotEmpty()) {
                val hash = SecurityUtils.sha256(nuevaPwd)
                cambios += dao.updateClave(u.id, hash)
            }

            if (cambios > 0) {
                Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult(RESULT_PERFIL_EDITADO, Bundle())
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Sin cambios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val RESULT_PERFIL_EDITADO = "perfil_editado"
        fun newInstance() = DialogEditarPerfil()
    }
}
