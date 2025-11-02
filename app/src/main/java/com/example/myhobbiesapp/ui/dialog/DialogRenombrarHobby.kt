package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.myhobbiesapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DialogRenombrarHobby : BottomSheetDialogFragment(R.layout.dialog_agregar_hobby) {

    private var oldName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        oldName = requireArguments().getString(ARG_OLD_NAME, "")
        if (oldName.isBlank()) {
            dismiss(); return
        }

        val til = view.findViewById<TextInputLayout>(R.id.tilHobby)
        val et  = view.findViewById<TextInputEditText>(R.id.etHobby)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarHobby)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelarHobby)

        val btnHistorial = view.findViewById<MaterialButton>(R.id.btnHistorial)
        btnHistorial?.visibility = View.GONE

        et.setText(oldName)
        til.hint = "Nuevo nombre del hobby"
        btnGuardar.text = "Renombrar"

        btnGuardar.setOnClickListener {
            val nuevoNombre = et.text?.toString()?.trim().orEmpty()
            if (nuevoNombre.isEmpty()) {
                til.error = "Escribe un nombre"; return@setOnClickListener
            }
            if (nuevoNombre == oldName) {
                til.error = "Es el mismo nombre"; return@setOnClickListener
            }
            til.error = null


            val bundle = Bundle().apply {
                putString("hobbyRenombradoViejo", oldName)
                putString("hobbyRenombradoNuevo", nuevoNombre)
            }
            parentFragmentManager.setFragmentResult(RESULT_HOBBY_RENAMED, bundle)
            dismiss()
        }

        btnCancelar?.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_OLD_NAME = "arg_old_name"
        const val RESULT_HOBBY_RENAMED = "hobby_renamed"

        fun newInstance(oldName: String) =
            DialogRenombrarHobby().apply {
                arguments = Bundle().apply {
                    putString(ARG_OLD_NAME, oldName)
                }
            }
    }
}