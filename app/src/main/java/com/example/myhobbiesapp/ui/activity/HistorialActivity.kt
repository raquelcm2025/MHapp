package com.example.myhobbiesapp.ui.activity

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("idUsuario", -1)
        val rv = findViewById<RecyclerView>(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = HistorialAdapter(emptyList())
        rv.adapter = adapter

        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        val dao = HobbyDAO(this)
     //   val hist = dao.listHistorialByUser(idUsuario)
      //  val lista = hist.map { (nombre, amigos) ->
       //     HistorialItem(nombre = nombre, amigos = amigos)
        //}
        //adapter.submit(lista)
    }
}
