package com.example.myhobbiesapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.myhobbiesapp.databinding.DialogOpcionesExploraBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogOpcionesExplora : DialogFragment() {

    interface Listener {
        fun onVerPerfil(userId: Int)
        fun onConectar(userId: Int)
    }

    private var _binding: DialogOpcionesExploraBinding? = null
    private val binding get() = _binding!!
    private var userId: Int = -1
    private var nombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt(ARG_USER_ID) ?: -1
        nombre = arguments?.getString(ARG_NOMBRE) ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogOpcionesExploraBinding.inflate(LayoutInflater.from(context))
        binding.tvTitulo.text = if (nombre.isNotBlank()) nombre else "Usuario"

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.btnVerPerfil.setOnClickListener {
            (parentFragment as? Listener)?.onVerPerfil(userId)
            dismiss()
        }
        binding.btnConectar.setOnClickListener {
            (parentFragment as? Listener)?.onConectar(userId)
            dismiss()
        }
        binding.btnCancelar.setOnClickListener { dismiss() }

        return dlg
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
