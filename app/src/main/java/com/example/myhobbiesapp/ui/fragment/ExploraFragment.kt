package com.example.myhobbiesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhobbiesapp.adapter.PerfilesAdapter
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.FragmentExploraBinding
import com.example.myhobbiesapp.ui.dialog.DialogPerfilExplora
import com.example.myhobbiesapp.util.CurrentUser

class ExploraFragment : Fragment() {
    private var _binding: FragmentExploraBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        PerfilesAdapter(
            onClickItem = { u ->
                DialogPerfilExplora.newInstance(u.id)
                    .show(parentFragmentManager, "perfil_explora")
            },
            onClickAcciones = { _ -> /* acciones adicional */ }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentExploraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        binding.rvPerfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPerfiles.adapter = adapter

        val idUsuario = CurrentUser.idFromSession(requireContext())
        val dao = UsuarioDAO(requireContext())

        val lista = dao.getAll().let { l ->
            if (idUsuario != null) l.filter { it.id != idUsuario } else l
        }
        adapter.submitList(lista)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
