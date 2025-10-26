package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SimpleRenameHobbySheet : BottomSheetDialogFragment(R.layout.dialog_agregar_hobby) {

    private var userId = -1
    private var oldHobbyId = -1
    private var oldName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getInt(ARG_USER_ID, -1)
        oldHobbyId = requireArguments().getInt(ARG_OLD_HOBBY_ID, -1)
        oldName = requireArguments().getString(ARG_OLD_NAME, "")

        val til = view.findViewById<TextInputLayout>(R.id.tilHobby)
        val et  = view.findViewById<TextInputEditText>(R.id.etHobby)
        val btn = view.findViewById<MaterialButton>(R.id.btnGuardarHobby)

        et.setText(oldName)
        til.hint = "Nuevo nombre del hobby"

        btn.setOnClickListener {
            val nuevo = et.text?.toString()?.trim().orEmpty()
            if (nuevo.isEmpty()) {
                til.error = "Escribe un hobby"
                return@setOnClickListener
            }
            val dao = HobbyDAO(requireContext())
            // “Renombrar”: se quita el vínculo antiguo y crea/relaciona el nuevo
            dao.unlinkUsuarioHobby(userId, oldHobbyId)
            val newId = dao.getOrCreateByName(nuevo)
            dao.linkUsuarioHobby(userId, newId)

            Toast.makeText(requireContext(), "Hobby actualizado", Toast.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult(DialogAgregarHobby.RESULT_HOBBIES_CHANGED, Bundle())
            dismiss()
        }
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_OLD_HOBBY_ID = "arg_old_hobby_id"
        private const val ARG_OLD_NAME = "arg_old_name"

        fun newInstance(userId: Int, oldHobbyId: Int, oldName: String) =
            SimpleRenameHobbySheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                    putInt(ARG_OLD_HOBBY_ID, oldHobbyId)
                    putString(ARG_OLD_NAME, oldName)
                }
            }
    }
}
