package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Hobby

class HobbyAdapter(
    private val onDelete: ((Hobby) -> Unit)? = null,
    @LayoutRes private val itemLayoutRes: Int = R.layout.item_hobby
) : RecyclerView.Adapter<HobbyAdapter.VH>() {

    private val data = mutableListOf<Hobby>()

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView =
            (v.findViewById<TextView>(R.id.tvNombre) ?: v.findViewById(R.id.tvHobbyNombre))!!
        val tvAmigos: TextView = v.findViewById(R.id.tvAmigos)
        val btnEliminar: ImageButton? = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val hobby = data[position]
        holder.tvNombre.text = hobby.nombre
        holder.tvAmigos.text = "N° amigos que practican tu hobby favorito: ${hobby.amigos}"

        if (holder.btnEliminar != null && onDelete != null) {
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEliminar.setOnClickListener { onDelete.invoke(hobby) }
        } else if (holder.btnEliminar != null) {
            holder.btnEliminar.visibility = View.GONE
            holder.btnEliminar.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = data.size

    fun submit(list: List<Hobby>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}


