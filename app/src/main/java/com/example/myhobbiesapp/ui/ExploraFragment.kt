package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.PerfilesAdapter
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.data.UsuarioDAO
import com.example.myhobbiesapp.entity.Usuario

class ExploraFragment : Fragment(R.layout.fragment_explora), DialogOpcionesExplora.Listener {

    private lateinit var rv: RecyclerView
    private var tvVacio: TextView? = null
    private lateinit var adapter: PerfilesAdapter
    private val dao by lazy { UsuarioDAO(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.rvPerfiles)
        tvVacio = view.findViewById(R.id.tvVacioExplora)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = PerfilesAdapter(
            onClickItem = { user -> mostrarOpciones(user) },
            onClickAcciones = { user -> mostrarOpciones(user) }
        )
        rv.adapter = adapter

        cargarPerfiles()
    }

    private fun cargarPerfiles() {
        val miId = SesionActiva.usuarioActual?.id ?: 0
        val lista: List<Usuario> = try {
            dao.listarTodosMenos(miId)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            emptyList()
        }
        adapter.submitList(lista)
        tvVacio?.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarOpciones(user: Usuario) {
        val nomApe = "${user.nombre} ${user.apellido}".trim()
        DialogOpcionesExplora.newInstance(user.id, nomApe)
            .show(parentFragmentManager, "OpcionesExplora")
    }

    // del diálogo
    override fun onVerPerfil(userId: Int) {
        val frag = PerfilFragment.newInstance(userId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, frag)
            .addToBackStack("perfil_externo")
            .commitAllowingStateLoss()
    }

    override fun onConectar(userId: Int) {
        Toast.makeText(requireContext(), "Solicitud enviada ✨", Toast.LENGTH_SHORT).show()
    }
}
