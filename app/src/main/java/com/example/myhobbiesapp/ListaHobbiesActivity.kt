package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.*
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

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaHobbies
        )

        btnAgregar.setOnClickListener {
            val hobby = etHobby.text.toString().trim()
            if (hobby.isEmpty()) {
                listaHobbies.add(hobby)
                adapter.notifyDataSetChanged()
                etHobby.text.clear()
            } else {
                Toast.makeText(this, "Escribe un hobby", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
