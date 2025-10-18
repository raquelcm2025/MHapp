package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.HistorialItem

class HistorialAdapter(
    private val items: List<HistorialItem>
) : RecyclerView.Adapter<HistorialAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvFecha:  TextView = v.findViewById(R.id.tvFecha)
        val btnAccion: ImageButton? = v.findViewById(R.id.btnAccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = items[pos]
        h.tvNombre.text = it.nombre
        h.tvFecha.text  = it.fecha
    }

    override fun getItemCount() = items.size


}