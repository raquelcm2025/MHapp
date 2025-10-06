package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.core.UserStore

data class Persona(val nombre: String, val hobbies: List<String>)

class ExploraFragment : Fragment(R.layout.fragment_explora) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvExplora)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val yo = UserStore.getLogged(requireContext())
        val misHobbies = yo?.hobbies?.toSet() ?: emptySet()

        // dataset falso
        val otros = listOf(
            Persona("Ana Torres", listOf("Karaoke", "Lectura", "Pintura")),
            Persona("Carlos Díaz", listOf("Running", "Fútbol", "Ajedrez")),
            Persona("Lucía Pérez", listOf("Yoga", "Senderismo", "Cocina")),
            Persona("Marco Ruiz", listOf("Fotografía", "Guitarra", "Lectura"))
        )
        // ordenar por coincidencias
        val ordenados = otros.sortedByDescending { (it.hobbies.toSet() intersect misHobbies).size }

        rv.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(p: android.view.ViewGroup, v: Int): VH {
                val view = android.view.LayoutInflater.from(p.context)
                    .inflate(R.layout.item_explora_user, p, false)
                return VH(view)
            }
            override fun getItemCount() = ordenados.size
            override fun onBindViewHolder(h: VH, pos: Int) {
                val p = ordenados[pos]
                val comunes = (p.hobbies.toSet() intersect misHobbies).size
                h.tvNombre.text = p.nombre
                h.tvCoin.text = if (comunes == 1) "1 hobby en común" else "$comunes hobbies en común"
                h.tvHobbies.text = "Hobbies: ${p.hobbies.joinToString(", ")}"
            }
        }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvCoin: TextView = v.findViewById(R.id.tvCoincidencias)
        val tvHobbies: TextView = v.findViewById(R.id.tvHobbies)
    }
}
