package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.entity.Chat

class ChatsAdapter(
    private val onClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.VH>() {

    private val data = mutableListOf<Chat>()

    fun submit(list: List<Chat>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = data.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvTitulo: TextView = v.findViewById(R.id.tvTituloChat)
        private val tvUltimo: TextView = v.findViewById(R.id.tvUltimoMsg)
        private val ivAvatar: ImageView = v.findViewById(R.id.ivAvatarChat)

        fun bind(c: Chat) {
            tvTitulo.text = c.titulo
            tvUltimo.text = c.ultimoMensaje
            ivAvatar.setImageResource(c.avatar)
        }
    }
}
