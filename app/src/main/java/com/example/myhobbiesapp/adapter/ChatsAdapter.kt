package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R

data class ChatItem(val nombre: String, val ultimo: String)

class ChatsAdapter(
    private val items: List<ChatItem>,
    private val onClick: (ChatItem) -> Unit = {}
) : RecyclerView.Adapter<ChatsAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvUltimo: TextView = v.findViewById(R.id.tvUltimo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = items[pos]
        h.tvNombre.text = it.nombre
        h.tvUltimo.text  = it.ultimo
        h.itemView.setOnClickListener { onClick(it) }
    }

    override fun getItemCount() = items.size
}
