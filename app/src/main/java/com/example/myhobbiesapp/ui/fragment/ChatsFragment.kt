package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.ChatsAdapter
import com.example.myhobbiesapp.data.repo.ChatsRepo
import com.example.myhobbiesapp.data.entity.Chat
import com.example.myhobbiesapp.ui.activity.ChatActivity

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: ChatsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvChats)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = ChatsAdapter { chatItem: Chat ->
            val it = Intent(requireContext(), ChatActivity::class.java)
            it.putExtra("chatId", chatItem.id)
            it.putExtra("esAmigo", true)
            startActivity(it)
        }
        rv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.submit(ChatsRepo.listar())
    }
}