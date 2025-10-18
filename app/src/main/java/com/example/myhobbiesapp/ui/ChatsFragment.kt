package com.example.myhobbiesapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.ChatsAdapter
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.data.AmistadRepo
import com.example.myhobbiesapp.data.ChatDAO
import com.example.myhobbiesapp.entity.Chat

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private lateinit var rv: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var adapter: ChatsAdapter
    private val dao by lazy { ChatDAO(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.rvChats)
        tvVacio = view.findViewById(R.id.tvVacioChats)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatsAdapter { preview -> abrirChat(preview) }
        rv.adapter = adapter

        cargarChats()
    }

    override fun onResume() {
        super.onResume()
        cargarChats()
    }

    private fun cargarChats() {
        val idUsuario = SesionActiva.usuarioActual?.id ?: 0

        val desdeBD: List<Chat> = dao.listByUser(idUsuario)
        val lista = if (desdeBD.isNotEmpty()) desdeBD else AmistadRepo.seedFor(idUsuario)

        adapter.submitList(lista)
        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun abrirChat(preview: Chat) {
        // Abre tu pantalla de detalle de chat (si ya la tienes)
        // Ajusta el nombre de la Activity/Fragment real:
        val it = Intent(requireContext(), com.example.myhobbiesapp.ChatActivity::class.java)
        it.putExtra("chatId", preview.id)
        startActivity(it)
    }
}
