package com.example.myhobbiesapp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.myhobbiesapp.databinding.DialogOpcionesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogOpciones : DialogFragment() {

    interface Listener {
        fun onEditar(hobbyId: Int)
        fun onEliminar(hobbyId: Int)
    }

    private var _binding: DialogOpcionesBinding? = null
    private val binding get() = _binding!!

    private var hobbyId: Int = -1
    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hobbyId = arguments?.getInt(ARG_HOBBY_ID) ?: -1
        listener = activity as? Listener ?: parentFragment as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogOpcionesBinding.inflate(layoutInflater)
        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.btnEditar.setOnClickListener {
            listener?.onEditar(hobbyId)
            dismiss()
        }
        binding.btnEliminar.setOnClickListener {
            listener?.onEliminar(hobbyId)
            dismiss()
        }
        binding.btnCerrarDialog.setOnClickListener { dismiss() }

        return dlg
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_HOBBY_ID = "arg_hobby_id"
        fun newInstance(hobbyId: Int) = DialogOpciones().apply {
            arguments = Bundle().apply { putInt(ARG_HOBBY_ID, hobbyId) }
        }
    }
}
