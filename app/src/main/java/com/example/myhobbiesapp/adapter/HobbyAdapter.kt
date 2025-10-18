package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Hobby

class HobbyAdapter(
    private val onItemClick: (Hobby) -> Unit
) : RecyclerView.Adapter<HobbyAdapter.VH>() {

    private val data = mutableListOf<Hobby>()

    fun submit(list: List<Hobby>) {
        val unicos = list.distinctBy { it.id }
        data.clear()
        data.addAll(unicos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hobby, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position], onItemClick)
    }

    override fun getItemCount(): Int = data.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvHobbyNombre)
        private val tvNota: TextView?  = itemView.findViewById(R.id.tvHobbyNota)
        private val tvFecha: TextView? = itemView.findViewById(R.id.tvHobbyFecha)

        fun bind(h: Hobby, onItemClick: (Hobby) -> Unit) {
            tvNombre.text = h.nombre
            tvNota?.text  = h.nota
            tvFecha?.text = h.fecha
            itemView.setOnClickListener { onItemClick(h) }
        }
    }
}
