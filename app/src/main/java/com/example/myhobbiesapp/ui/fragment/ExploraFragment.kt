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
import com.example.myhobbiesapp.ui.dialog.DialogOpcionesExplora
import com.example.myhobbiesapp.util.CurrentUser

class ExploraFragment : Fragment() {

    private var _binding: FragmentExploraBinding? = null
    private val binding get() = _binding!!

    // Ahora el adapter abre el diálogo de opciones
    private val adapter by lazy {
        PerfilesAdapter(
            onClickItem = { usuario ->
                DialogOpcionesExplora
                    .newInstance(usuario.id)
                    .show(parentFragmentManager, "opciones_explora")
            },
            onClickAcciones = { usuario ->
                DialogOpcionesExplora
                    .newInstance(usuario.id)
                    .show(parentFragmentManager, "opciones_explora")
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar lista
        binding.rvPerfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPerfiles.adapter = adapter

        // Cargar usuarios, excluyendo al actual
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
