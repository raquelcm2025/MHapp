package com.example.myhobbiesapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.PerfilesAdapter
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.entity.Usuario
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.ui.dialog.DialogOpcionesExplora
import com.example.myhobbiesapp.ui.dialog.DialogPerfilExplora

class ExploraFragment : Fragment(R.layout.fragment_explora) {

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

        // Escuchar acciones del diálogo (ver / conectar)
        setFragmentResultListener("explora_ops") { _, b ->
            when (b.getString("action")) {
                "ver" -> {
                    val userId = b.getInt("userId")
                    // Mini vista tipo tarjeta (o cambia por tu pantalla preferida)
                    DialogPerfilExplora.newInstance(userId)
                        .show(parentFragmentManager, "mini_perfil")
                }
                "conectar" -> {
                    Toast.makeText(requireContext(), "Se envió su solicitud de amistad ✨", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        DialogOpcionesExplora.Companion.newInstance(user.id, nomApe)
            .show(parentFragmentManager, "OpcionesExplora")
    }
}