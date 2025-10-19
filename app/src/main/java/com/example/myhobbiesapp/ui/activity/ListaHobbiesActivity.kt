package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.HobbyAdapter
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.entity.Hobby
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListaHobbiesActivity : AppCompatActivity(R.layout.activity_lista_hobbies) {

    private lateinit var dao: HobbyDAO
    private var idUsuario: Int = -1

    private lateinit var etHobby: EditText
    private lateinit var etAmigos: EditText

    private lateinit var btnAgregar: Button
    private lateinit var tvVacio: TextView
    private lateinit var rv: RecyclerView
    private lateinit var adapter: HobbyAdapter
    private lateinit var btnHistorial: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        dao = HobbyDAO(this)

        etHobby = findViewById(R.id.etHobby)
        etAmigos = findViewById(R.id.etAmigos)
        btnAgregar = findViewById(R.id.btnAgregar)
        tvVacio  = findViewById(R.id.tvVacio)
        rv = findViewById(R.id.rvHobbies)
        btnHistorial = findViewById(R.id.btnHistorial)

        rv.layoutManager = LinearLayoutManager(this)
        adapter = HobbyAdapter(
            onDelete = { hobby -> confirmarEliminar(hobby) },
            itemLayoutRes = R.layout.item_hobby
        )
        rv.adapter = adapter

        refrescarLista()

        btnHistorial.setOnClickListener {
            val it = Intent(this, HistorialActivity::class.java)
            it.putExtra("idUsuario", idUsuario)
            startActivity(it)
        }

        btnAgregar.setOnClickListener {
            val nombre = etHobby.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Escribe un hobby", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amigos = etAmigos.text.toString().trim().toIntOrNull() ?: 0
            if (amigos < 0) {
                etAmigos.error = "Debe ser 0 o mayor"
                return@setOnClickListener
            }

            val h = Hobby(
                id = 0,
                nombre = nombre,
                amigos = amigos,
                idUsuario = idUsuario
            )

            val insertId = dao.insert(h)
            if (insertId > 0) {
                etHobby.setText("")
                etAmigos.setText("")
                refrescarLista()
            } else {
                Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarEliminar(hobby: Hobby) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar")
            .setMessage("¿Eliminar \"${hobby.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                val r = dao.deleteById(hobby.id)
                if (r > 0) refrescarLista()
                else Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun refrescarLista() {
        val list = dao.listByUser(idUsuario)
        adapter.submit(list)
        tvVacio.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}
