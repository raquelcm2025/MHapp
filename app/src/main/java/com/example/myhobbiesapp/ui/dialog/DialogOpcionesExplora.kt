package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myhobbiesapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class DialogOpcionesExplora : BottomSheetDialogFragment(R.layout.dialog_opciones_explora) {

    private var userId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getInt(ARG_USER_ID, -1)

        val btnVer = view.findViewById<MaterialButton>(R.id.btnVerPerfil)
        val btnCon = view.findViewById<MaterialButton>(R.id.btnConectar)
        val btnCan = view.findViewById<MaterialButton>(R.id.btnCancelar)

        btnVer.setOnClickListener {
            DialogPerfilExplora.newInstance(userId).show(parentFragmentManager, "perfil_explora")
            dismiss()
        }
        btnCon.setOnClickListener {
            Toast.makeText(requireContext(), "Solicitud de conexión enviada ✨", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btnCan.setOnClickListener { dismiss() }
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = DialogOpcionesExplora().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}
