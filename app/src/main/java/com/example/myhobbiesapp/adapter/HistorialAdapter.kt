package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.HistorialItem

class HistorialAdapter(private var items: List<HistorialItem>)
    : RecyclerView.Adapter<HistorialAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView =
            (v.findViewById<TextView>(R.id.tvNombre) ?: v.findViewById(R.id.tvHobbyNombre))!!
        val tvAmigos: TextView = v.findViewById(R.id.tvAmigos)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_historial, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = items[pos]
        h.tvNombre.text = it.nombre
        h.tvAmigos.text  = "N° de Amigos que practican tu hobby favorito: ${it.amigos}"
    }

    override fun getItemCount() = items.size
    fun submit(nuevos: List<HistorialItem>) { items = nuevos; notifyDataSetChanged() }
}

