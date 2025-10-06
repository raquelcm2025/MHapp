package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class ListaHobbiesActivity : AppCompatActivity() {

    private lateinit var tietHobby: EditText
    private lateinit var btnAgregar: Button
    private lateinit var lvHobbies: ListView

    private lateinit var btnHistorial: Button

    private val listaHobbies = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_hobbies)

        tietHobby = findViewById(R.id.tietHobby)
        btnAgregar = findViewById(R.id.ivAgregar)
        lvHobbies = findViewById(R.id.lvHobbies)
        btnHistorial = findViewById(R.id.btnHistorial)

        // Inicializar adaptador
        adapter = ArrayAdapter(
            this, // Activity actual
            android.R.layout.simple_list_item_1, // Diseño de cada elemento de lista
            listaHobbies // Datos a utilizar
        )
        lvHobbies.adapter = adapter // Asignamos adaptador a ListView

        // Evento: agregar producto
        btnAgregar.setOnClickListener {
            val hobby = tietHobby.text.toString().trim() // Obtenemos el texto ingresado
            if (hobby.isNotEmpty()) { // Si NO está vacío
                listaHobbies.add(hobby) // Agrega el texto a la lista
                adapter.notifyDataSetChanged() // Avisa al adaptador que se agregó un elemento
                tietHobby.text?.clear() // Limpia el contenido de la caja de texto
            } else { // Si está vacío muestra un mensaje al usuario
                Toast.makeText(this, "Escribe un hobby", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento: clic en producto de la lista
        lvHobbies.setOnItemClickListener { _, _, position, _ ->
            val hobby = listaHobbies[position] // Obtenemos el producto seleccionado
            Toast.makeText( // Arma el mensaje a mostrar
                this, // Activity actual
                "Seleccionaste: $hobby", //Texto a mostrar, $... es concatenación
                Toast.LENGTH_SHORT // Duración del mensaje
            ).show() // Muestra el mensaje
        }


        /**
         *         Evento: clic largo para eliminar producto con Dialog Personalizado
         */
        lvHobbies.setOnItemLongClickListener { _, _, position, _ ->
            val hobby = listaHobbies[position]

            // Inflar el layout personalizado
            val dialogView = layoutInflater.inflate(R.layout.dialog_opciones, null)

            val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
            val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminar)
            val btnMarcar = dialogView.findViewById<Button>(R.id.btnMarcar)
            val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)

            tvTitulo.text = "Opciones para $hobby"

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Acciones de botones
            btnEliminar.setOnClickListener {
                listaHobbies.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "$hobby eliminado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnMarcar.setOnClickListener {
                Toast.makeText(this, "$hobby marcado como comprado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            true
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // Hace que el teclado del dispositivo no tape a los Views
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
    }
}