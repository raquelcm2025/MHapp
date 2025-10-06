package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.ChatItem
import com.example.myhobbiesapp.adapter.ChatsAdapter

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvChats)
        val tvVacio = view.findViewById<TextView>(R.id.tvVacio)

        val data = listOf(
            ChatItem("Martha", "¿Vamos al karaoke hoy?"),
            ChatItem("Luis", "Te paso el set de guitarras 🎸"),
            ChatItem("Club Ajedrez", "Partida 8:00 pm")
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = ChatsAdapter(data) { chat ->
            Toast.makeText(requireContext(), "Abrir chat con ${chat.nombre}", Toast.LENGTH_SHORT).show()
        }

        tvVacio.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }
}
