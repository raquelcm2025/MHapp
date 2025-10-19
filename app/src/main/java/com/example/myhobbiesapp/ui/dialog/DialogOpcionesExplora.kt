package com.example.myhobbiesapp.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.myhobbiesapp.databinding.DialogOpcionesExploraBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogOpcionesExplora : DialogFragment() {

    private var _binding: DialogOpcionesExploraBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = -1
    private var nombre: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogOpcionesExploraBinding.inflate(LayoutInflater.from(requireContext()))
        userId = requireArguments().getInt(ARG_USER_ID)
        nombre = requireArguments().getString(ARG_NOMBRE, "")

        binding.tvTitulo.text = nombre.ifBlank { "Opciones" }

        binding.btnVerPerfil.setOnClickListener {
            setFragmentResult("explora_ops", bundleOf("action" to "ver", "userId" to userId))
            dismiss()
        }
        binding.btnConectar.setOnClickListener {
            setFragmentResult("explora_ops", bundleOf("action" to "conectar", "userId" to userId))
            dismiss()
        }
        binding.btnCancelar?.setOnClickListener { dismiss() } // si existe en tu layout

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_NOMBRE = "arg_nombre"
        fun newInstance(userId: Int, nombre: String) = DialogOpcionesExplora().apply {
            arguments = Bundle().apply {
                putInt(ARG_USER_ID, userId)
                putString(ARG_NOMBRE, nombre)
            }
        }
    }
}
