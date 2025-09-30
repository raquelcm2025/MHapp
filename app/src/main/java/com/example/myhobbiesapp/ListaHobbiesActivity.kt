package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ListaHobbiesActivity : AppCompatActivity() {

    private lateinit var etHobby: EditText
    private lateinit var btnAgregar: Button
    private lateinit var lvHobbies: ListView

    private val listaHobbies = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_hobbies)

        etHobby = findViewById(R.id.etHobby)
        btnAgregar = findViewById(R.id.btnAgregar)
        lvHobbies = findViewById(R.id.lvHobbies)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaHobbies)
        lvHobbies.adapter = adapter

        lvHobbies.emptyView = TextView(this).apply {
            text = "Sin hobbies aún"
            textSize = 16f
            setPadding(16, 32, 16, 0)
        }.also { (lvHobbies.parent as? LinearLayout)?.addView(it) }

        btnAgregar.setOnClickListener {
            val hobby = etHobby.text.toString().trim()
            if (hobby.isEmpty()) {
                Toast.makeText(this, "Escribe un hobby", Toast.LENGTH_SHORT).show()
            } else if (listaHobbies.any { it.equals(hobby, ignoreCase = true) }) {
                Toast.makeText(this, "Ese hobby ya está en la lista", Toast.LENGTH_SHORT).show()
            } else {
                listaHobbies.add(hobby)
                adapter.notifyDataSetChanged()
                etHobby.text.clear()
            }
        }

        lvHobbies.setOnItemClickListener { _, _, position, _ ->
            val hobby = listaHobbies[position]
            Toast.makeText(this, "Te gusta: $hobby", Toast.LENGTH_SHORT).show()
        }

        lvHobbies.setOnItemLongClickListener { _, _, position, _ ->
            val hobby = listaHobbies[position]
            val opciones = arrayOf("Ver detalles", "Eliminar", "Marcar como favorito")

            AlertDialog.Builder(this)
                .setTitle("Opciones para $hobby")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> Toast.makeText(this, "Detalles de $hobby", Toast.LENGTH_SHORT).show()
                        1 -> {
                            listaHobbies.removeAt(position)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this, "$hobby eliminado", Toast.LENGTH_SHORT).show()
                        }
                        2 -> Toast.makeText(this, "$hobby marcado como favorito", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }
    }
}