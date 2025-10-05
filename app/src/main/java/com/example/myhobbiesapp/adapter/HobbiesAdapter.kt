package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Hobby


class HobbiesAdapter(
    private val items: List<Hobby>,
    private val onClick: (Hobby) -> Unit
) : RecyclerView.Adapter<HobbiesAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvDesc: TextView   = v.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hobby, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.tvNombre.text = item.nombre
        h.tvDesc.text   = item.descripcion
        h.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
