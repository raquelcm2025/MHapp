package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Usuario

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

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_perfil, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(data[pos], onClickItem, onClickAcciones)
    override fun getItemCount(): Int = data.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
        private val btnAcciones: ImageButton = itemView.findViewById(R.id.btnAcciones)

        fun bind(u: Usuario, onClickItem: (Usuario) -> Unit, onClickAcciones: (Usuario) -> Unit) {
            tvNombre.text = "${u.nombre} ${u.apellido}".trim()
            tvCorreo.text = u.correo
            iv.setImageResource(if (u.foto != 0) u.foto else R.drawable.ic_person)

            itemView.setOnClickListener { onClickItem(u) }
            btnAcciones.setOnClickListener { onClickAcciones(u) }
        }
    }
}

