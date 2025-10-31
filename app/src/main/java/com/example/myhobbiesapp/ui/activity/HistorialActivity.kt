package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.HistorialAdapter
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.data.entity.HistorialItem

class HistorialActivity : AppCompatActivity(R.layout.activity_historial) {

    private lateinit var adapter: HistorialAdapter
    private var idUsuario: Int = -1
    private lateinit var dao: HobbyDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        dao = HobbyDAO(this)

        val rv = findViewById<RecyclerView>(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = HistorialAdapter(mutableListOf()) { item, position ->
            // Eliminar relación usuario-hobby (ajusta al método que tengas)
            try {
                // Si tienes un método por nombre:
                dao.unlinkUsuarioHobbyByName(idUsuario, item.nombre) // <-- implementa/ajusta según tu DAO
                adapter.removeAt(position)
                Toast.makeText(this, "Eliminado: ${item.nombre}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
            }
        }
        rv.adapter = adapter

        cargarDatos()
    }

    private fun cargarDatos() {
        // Si tu DAO ya devuelve solo nombres:
        // val nombres: List<String> = dao.listHobbiesByUserName(idUsuario)

        // Si tu DAO devuelve Pair(nombre, amigos), ignoramos amigos:
        val pares: List<Pair<String, Int>> = dao.listHistorialByUser(idUsuario)
        val lista = pares.map { (nombre, _) -> HistorialItem(nombre) }

        adapter.submit(lista)
    }
}
