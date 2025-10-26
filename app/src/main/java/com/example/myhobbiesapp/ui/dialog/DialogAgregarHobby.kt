package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.View

import android.widget.Toast
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.myhobbiesapp.R

class DialogAgregarHobby : BottomSheetDialogFragment(R.layout.dialog_agregar_hobby){
    private var userId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getInt(ARG_USER_ID, -1)
        if (userId <= 0) { dismiss(); return }

        val til = view.findViewById<TextInputLayout>(R.id.tilHobby)
        val et  = view.findViewById<TextInputEditText>(R.id.etHobby)
        val btn = view.findViewById<MaterialButton>(R.id.btnGuardarHobby)

        btn.setOnClickListener {
            val nombre = et.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                til.error = "Escribe un hobby"
                return@setOnClickListener
            }
            val dao = HobbyDAO(requireContext())
            val idH = dao.getOrCreateByName(nombre)
            dao.linkUsuarioHobby(userId, idH)
            Toast.makeText(requireContext(), "Hobby añadido", Toast.LENGTH_SHORT).show()

            parentFragmentManager.setFragmentResult(RESULT_HOBBIES_CHANGED, Bundle())
            dismiss()
        }
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        const val RESULT_HOBBIES_CHANGED = "hobbies_changed"

        fun newInstance(userId: Int) = DialogAgregarHobby().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}
