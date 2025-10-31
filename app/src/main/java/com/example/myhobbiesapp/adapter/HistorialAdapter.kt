package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.entity.HistorialItem
import com.google.android.material.button.MaterialButton

class HistorialAdapter(
    private var items: MutableList<HistorialItem>,
    private val onDelete: (HistorialItem, Int) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val btnEliminar: MaterialButton = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_historial, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = items[pos]
        h.tvNombre.text = it.nombre
        h.btnEliminar.setOnClickListener {
            onDelete(items[h.bindingAdapterPosition], h.bindingAdapterPosition)
        }
    }

    override fun getItemCount() = items.size

    fun submit(nuevos: List<HistorialItem>) {
        items = nuevos.toMutableList()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
