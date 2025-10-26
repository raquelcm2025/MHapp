package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.entity.Usuario

class PerfilesAdapter(
    private val onClickItem: (Usuario) -> Unit,
    private val onClickAcciones: (Usuario) -> Unit
) : RecyclerView.Adapter<PerfilesAdapter.VH>() {

    private val data = mutableListOf<Usuario>()

    fun submitList(list: List<Usuario>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_perfil, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position], onClickItem, onClickAcciones)
    }

    override fun getItemCount(): Int = data.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
        private val btnAcciones: ImageButton = itemView.findViewById(R.id.btnAcciones)

        fun bind(
            u: Usuario,
            onClickItem: (Usuario) -> Unit,
            onClickAcciones: (Usuario) -> Unit
        ) {
            val nombreCompleto = buildString {
                append(u.nombre)
                if (u.apellidoPaterno.isNotBlank()) append(" ").append(u.apellidoPaterno)
                if (u.apellidoMaterno.isNotBlank()) append(" ").append(u.apellidoMaterno)
            }.trim()

            tvNombre.text = nombreCompleto
            tvCorreo.text = u.correo

            val generoIcon = when (u.genero?.lowercase()) {
                "femenino", "mujer" -> R.drawable.ic_mujer
                "masculino", "hombre" -> R.drawable.ic_hombre
                else -> R.drawable.ic_person
            }
            iv.setImageResource(if (u.foto != 0) u.foto else generoIcon)

            itemView.setOnClickListener { onClickItem(u) }
            btnAcciones.setOnClickListener { onClickAcciones(u) }
        }
    }
}

