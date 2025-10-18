package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R


class GaleriaAdapter(
    private val esPropio: Boolean,
    private val onAgregarClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fotos: MutableList<Int> = mutableListOf()

    companion object {
        private const val TYPE_AGREGAR = 0
        private const val TYPE_FILA = 1
        private const val FOTOS_POR_FILA = 3
    }

    fun submit(nueva: List<Int>) {
        fotos.clear()
        fotos.addAll(nueva)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (esPropio && position == 0) TYPE_AGREGAR else TYPE_FILA
    }

    override fun getItemCount(): Int {
        val filas = if (fotos.isEmpty()) 0 else ((fotos.size - 1) / FOTOS_POR_FILA) + 1
        return if (esPropio) filas + 1 else filas
    }

    class VHAgregar(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.cardAgregar)
    }

    class VHFila(v: View) : RecyclerView.ViewHolder(v) {
        val iv1: ImageView = v.findViewById(R.id.ivFoto1)
        val iv2: ImageView = v.findViewById(R.id.ivFoto2)
        val iv3: ImageView = v.findViewById(R.id.ivFoto3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_AGREGAR) {
            val v = inf.inflate(R.layout.item_galeria_agregar, parent, false)
            VHAgregar(v)
        } else {
            val v = inf.inflate(R.layout.item_galeria_foto, parent, false)
            VHFila(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VHAgregar -> holder.card.setOnClickListener { onAgregarClick?.invoke() }
            is VHFila -> bindFila(holder, position)
        }
    }

    private fun bindFila(h: VHFila, adapterPos: Int) {
        // Si es propio, la lista de filas arranca en posición 1 (0 = agregar)
        val filaIndex = if (esPropio) adapterPos - 1 else adapterPos
        val base = filaIndex * FOTOS_POR_FILA

        fun bind(iv: ImageView, idx: Int) {
            if (idx in fotos.indices) {
                iv.visibility = View.VISIBLE
                iv.setImageResource(fotos[idx])
            } else {
                iv.visibility = View.INVISIBLE
            }
        }
        bind(h.iv1, base)
        bind(h.iv2, base + 1)
        bind(h.iv3, base + 2)
    }
}
