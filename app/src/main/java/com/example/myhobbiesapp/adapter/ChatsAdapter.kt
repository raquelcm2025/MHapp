package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Chat

class ChatsAdapter(
    private val onClick: (Chat) -> Unit
) : ListAdapter<Chat, ChatsAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(old: Chat, new: Chat) = old.id == new.id
            override fun areContentsTheSame(old: Chat, new: Chat) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivAvatarChat)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloChat)
        private val tvUltimo: TextView = itemView.findViewById(R.id.tvUltimoMsg)

        fun bind(cp: Chat, onClick: (Chat) -> Unit) {
            iv.setImageResource(cp.avatar)
            tvTitulo.text = cp.titulo
            tvUltimo.text = cp.ultimoMensaje
            itemView.setOnClickListener { onClick(cp) }
        }
    }
}
