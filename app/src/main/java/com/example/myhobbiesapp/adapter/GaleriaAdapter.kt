package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R

class GaleriaAdapter(private val fotos: List<Int>) :
    RecyclerView.Adapter<GaleriaAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val iv1: ImageView = v.findViewById(R.id.iv1)
        val iv2: ImageView = v.findViewById(R.id.iv2)
        val iv3: ImageView = v.findViewById(R.id.iv3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_galeria, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (fotos.isNotEmpty()) {
            holder.iv1.setImageResource(fotos.getOrNull(0) ?: 0)
            holder.iv2.setImageResource(fotos.getOrNull(1) ?: 0)
            holder.iv3.setImageResource(fotos.getOrNull(2) ?: 0)
        }
    }

    override fun getItemCount(): Int = 1 // Solo una fila
}

