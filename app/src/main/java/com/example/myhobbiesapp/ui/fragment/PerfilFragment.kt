package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.FragmentPerfilBinding
import com.example.myhobbiesapp.ui.activity.AccesoActivity
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.chip.Chip

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        fun cargarPerfil() {
            val email = SessionManager.getCurrentEmail(requireContext())
            val u = if (email != null) UsuarioDAO(requireContext()).getByCorreo(email) else null

            if (u != null) {
                val nombreCompleto = "${u.nombre} ${u.apellidoPaterno} ${u.apellidoMaterno}".trim()
                binding.tvNombreCompleto.text = nombreCompleto
                binding.tvCorreo.text = u.correo
                binding.tvCelular.text = u.celular

                val fotoRes = when (u.genero?.lowercase()) {
                    "femenino", "mujer" -> R.drawable.ic_mujer
                    "masculino", "hombre" -> R.drawable.ic_hombre
                    else -> R.drawable.ic_person
                }
                binding.ivAvatar.setImageResource(fotoRes)

                binding.chipsHobbies.removeAllViews()
                // Si luego cargas hobbies:
                // HobbyDAO(requireContext()).getByUsuario(u.id).forEach { addChip(it.nombre) }
            } else {
                binding.tvNombreCompleto.text = "Sin sesión"
                binding.tvCorreo.text = ""
                binding.tvCelular.text = ""
                binding.ivAvatar.setImageResource(R.drawable.ic_person)
                binding.chipsHobbies.removeAllViews()
            }
        }

        cargarPerfil()

        parentFragmentManager.setFragmentResultListener("perfil_editado", viewLifecycleOwner) { _, _ ->
            cargarPerfil()
        }

        binding.btnEditarPerfil.setOnClickListener {
            // abre tu dialog
        }

        binding.btnEditarHobbies.setOnClickListener { /* flujo hobbies */ }
        binding.btnAgregarFoto.setOnClickListener { /* selector imagen */ }

        binding.btnCerrarSesion.setOnClickListener {
            SessionManager.clear(requireContext())
            startActivity(Intent(requireContext(), AccesoActivity::class.java))
            activity?.finish()
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCheckable = false
            isClickable = false
        }
        binding.chipsHobbies.addView(chip)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
