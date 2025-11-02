package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.myhobbiesapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DialogAgregarHobby : BottomSheetDialogFragment(R.layout.dialog_agregar_hobby){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val til = view.findViewById<TextInputLayout>(R.id.tilHobby)
        val et  = view.findViewById<TextInputEditText>(R.id.etHobby)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarHobby)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelarHobby)

        val btnHistorial = view.findViewById<MaterialButton>(R.id.btnHistorial)
        btnHistorial?.visibility = View.GONE

        btnGuardar.setOnClickListener {
            val nombre = et.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                til.error = "Escribe un hobby"
                return@setOnClickListener
            }
            if (nombre.length > 30) {
                til.error = "Nombre muy largo (máx 30)"
                return@setOnClickListener
            }
            til.error = null


            val bundle = Bundle().apply {
                putString("nuevoHobby", nombre)
            }
            parentFragmentManager.setFragmentResult(RESULT_HOBBIES_CHANGED, bundle)

            dismiss()
        }

        btnCancelar?.setOnClickListener {
            dismiss()
        }

    }

    companion object {

        const val RESULT_HOBBIES_CHANGED = "hobbies_changed"

        fun newInstance() = DialogAgregarHobby()


    }
}