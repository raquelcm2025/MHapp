package com.example.myhobbiesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhobbiesapp.adapter.PerfilesAdapter
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.FragmentExploraBinding
import com.example.myhobbiesapp.ui.dialog.DialogOpcionesExplora
import com.example.myhobbiesapp.ui.dialog.DialogPerfilExplora
import com.example.myhobbiesapp.util.CurrentUser

class ExploraFragment : Fragment() {

    private var _binding: FragmentExploraBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        PerfilesAdapter(
            onClickItem = { u ->
                val myId = CurrentUser.idFromSession(requireContext())
                if (myId != null && u.id == myId) return@PerfilesAdapter
                // Primero: opciones
                DialogOpcionesExplora.newInstance(u.id)
                    .show(parentFragmentManager, "opciones_explora")
            },
            onClickAcciones = { u ->
                // También desde el botón de acciones
                val myId = CurrentUser.idFromSession(requireContext())
                if (myId != null && u.id == myId) return@PerfilesAdapter
                DialogOpcionesExplora.newInstance(u.id)
                    .show(parentFragmentManager, "opciones_explora")
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentExploraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        binding.rvPerfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPerfiles.adapter = adapter

        parentFragmentManager.setFragmentResultListener(
            DialogOpcionesExplora.RESULT_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val accion = result.getString("accion")
            val userId = result.getInt("userId", -1)
            if (userId <= 0) return@setFragmentResultListener

            when (accion) {
                "ver_perfil" -> {
                    DialogPerfilExplora.newInstance(userId)
                        .show(parentFragmentManager, "perfil_explora")
                }
                "conectar" -> {
                    //  luego se conectas a una API de amistad
                     Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Carga de usuarios (excluye al actual)
        val idYo = CurrentUser.idFromSession(requireContext())
        val dao = UsuarioDAO(requireContext())
        val lista = dao.getAll().let { l -> if (idYo != null) l.filter { it.id != idYo } else l }
        adapter.submitList(lista)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
