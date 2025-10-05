package com.example.myhobbiesapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Hobby

class HistorialAdapter(private val listaHistorial: List<Hobby>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    // Enlaza la interfaz item_historial con el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    // Mostrar datos en la interfaz
    override fun onBindViewHolder(holder: HistorialViewHolder,position: Int) {
        val hobby = listaHistorial[position]
        holder.tvHobby.text = hobby.hobby
        holder.tvHoras.text = "Número de horas dedicadas semanalmente: ${hobby.horasDedicadasSemanalmente}"
        holder.tvUltimaFecha.text = "Se realizó por última vez el: ${hobby.ultimaFecha}"
    }

    // Cantidad de elementos a mostrar
    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    // Define los elementos visuales de la interfaz
    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHobby : TextView = itemView.findViewById(R.id.tvHobby)
        val tvHoras : TextView = itemView.findViewById(R.id.tvHoras)
        val tvUltimaFecha : TextView = itemView.findViewById(R.id.tvUltimaFecha)
    }
}