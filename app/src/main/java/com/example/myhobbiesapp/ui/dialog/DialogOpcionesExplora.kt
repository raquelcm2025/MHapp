package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myhobbiesapp.databinding.DialogOpcionesExploraBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.os.bundleOf

class DialogOpcionesExplora : BottomSheetDialogFragment() {

    private var _binding: DialogOpcionesExploraBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getInt(ARG_USER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogOpcionesExploraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        binding.btnVerPerfil.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf("accion" to "ver_perfil", "userId" to userId)
            )
            dismiss()
        }

        binding.btnConectar.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf("accion" to "conectar", "userId" to userId)
            )
            dismiss()
        }

        binding.btnCancelar.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        const val RESULT_KEY = "opciones_explora_result"

        fun newInstance(userId: Int) = DialogOpcionesExplora().apply {
            arguments = bundleOf(ARG_USER_ID to userId)
        }
    }
}
