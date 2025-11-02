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
import com.google.android.material.button.MaterialButton

data class UsuarioItem(
    val uid: String,   // aquí guardamos el email
    val nombre: String,
    val correo: String
)

class UsuarioAdapter(
    private val onVerMas: (uid: String) -> Unit
) : ListAdapter<UsuarioItem, UsuarioAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<UsuarioItem>() {
            override fun areItemsTheSame(a: UsuarioItem, b: UsuarioItem) = a.uid == b.uid
            override fun areContentsTheSame(a: UsuarioItem, b: UsuarioItem) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombreUsuario)
        val tvCorreo: TextView = v.findViewById(R.id.tvCorreoUsuario)
        val ivFoto: ImageView  = v.findViewById(R.id.ivFotoUser)
        val btnVerMas: MaterialButton = v.findViewById(R.id.btnVerPerfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_explora, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.tvNombre.text = item.nombre
        h.tvCorreo.text = item.correo
        h.btnVerMas.setOnClickListener { onVerMas(item.uid) }
        h.itemView.setOnClickListener { onVerMas(item.uid) }
    }
}
