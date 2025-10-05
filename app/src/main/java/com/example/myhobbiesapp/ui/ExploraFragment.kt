package com.example.myhobbiesapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.HobbiesAdapter
import com.example.myhobbiesapp.entity.Hobby

class ExploraFragment : Fragment(R.layout.fragment_explora) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvHobbies)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // demo data (cambia por lo tuyo)
        val data = listOf(
            Hobby(1, "Guitarra", "Clases y covers"),
            Hobby(2, "Ajedrez", "Partidas online"),
            Hobby(3, "Correr", "5K los sábados")
        )

        rv.adapter = HobbiesAdapter(data) { hobby ->
            Toast.makeText(requireContext(), "Elegiste ${hobby.nombre}", Toast.LENGTH_SHORT).show()
        }
    }
}
