package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.adapter.HobbyAdapter
import com.example.myhobbiesapp.data.HobbyDAO
import com.example.myhobbiesapp.entity.Hobby
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistorialActivity : AppCompatActivity(R.layout.activity_historial) {

    private lateinit var dao: HobbyDAO
    private lateinit var rv: RecyclerView
    private lateinit var adapter: HobbyAdapter
    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        dao = HobbyDAO(this)

        rv = findViewById(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Reusa tu adapter (no mostramos diálogo aquí, solo listado)
        adapter = HobbyAdapter { /* sin acciones en historial */ }
        rv.adapter = adapter

        cargar()

        // FAB: llévalo a ListaHobbiesActivity del mismo usuario para agregar/ver
        findViewById<FloatingActionButton>(R.id.fabOpciones).setOnClickListener {
            val it = Intent(this, ListaHobbiesActivity::class.java)
            it.putExtra("idUsuario", idUsuario)
            startActivity(it)
        }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        val lista: List<Hobby> = dao.listByUser(idUsuario)
        adapter.submit(lista)

        if (lista.isEmpty()) {
            // Como tu XML no tiene tvVacio, avisamos con un Toast
            Toast.makeText(this, "No hay hobbies registrados aún", Toast.LENGTH_SHORT).show()
        }
    }
}
