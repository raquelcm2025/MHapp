package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.ChatListAdapter
import com.example.myhobbiesapp.ui.activity.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class ChatListItem(
    val uid: String = "",
    val nombre: String = "",
    val chatId: String = ""
)

class ChatFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var refFriends: DatabaseReference

    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView

    private val data = mutableListOf<ChatListItem>()
    private val adapter by lazy {
        ChatListAdapter { item -> abrirChat(item.chatId) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, s: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        rv = v.findViewById(R.id.rvChats)
        tvEmpty = v.findViewById(R.id.tvVacioChats)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val me = auth.currentUser?.uid ?: return

        refFriends = FirebaseDatabase.getInstance()
            .getReference("friends")
            .child(me)

        cargarChats()
    }

    private fun cargarChats() {
        @Suppress("UNUSED_VARIABLE")
        val me = auth.currentUser?.uid ?: return

        refFriends.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                data.clear()

                for (c in snap.children) {
                    val friendUid = c.key ?: continue
                    val chatId = c.value.toString()

                    // Obtener nombre del usuario
                    FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(friendUid)
                        .child("profile")
                        .child("nombre")
                        .get()
                        .addOnSuccessListener { ds ->
                            val nombre = ds.value?.toString() ?: "Usuario"

                            data.add(ChatListItem(friendUid, nombre, chatId))
                            adapter.submitList(data.toList())

                            tvEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
                        }
                }
            }

            override fun onCancelled(e: DatabaseError) {}
        })
    }

    private fun abrirChat(chatId: String) {
        val i = Intent(requireContext(), ChatActivity::class.java)
        i.putExtra("chatId", chatId)
        startActivity(i)
    }
}
