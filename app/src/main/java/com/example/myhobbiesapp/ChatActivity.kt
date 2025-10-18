package com.example.myhobbiesapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.sesion.SesionActiva
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity(R.layout.activity_chat) {

    private lateinit var rvMensajes: RecyclerView
    private lateinit var etMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvTitulo: TextView

    private val listaMensajes = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var chatId: Int = -1
    private var chatTitulo: String = "Chat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvMensajes = findViewById(R.id.rvMensajes)
        etMensaje = findViewById(R.id.etMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        tvTitulo = findViewById(R.id.tvTituloChat)

        // datos del chat seleccionado
        chatId = intent.getIntExtra("chatId", -1)
        chatTitulo = when (chatId) {
            1001 -> "Chateando con Marco Duarte López"
            1002 -> "Chateando con Luz Domínguez Gómez"
            1003 -> "Chateando con Josué Romero Loayza"
            else -> "Chat"
        }
        tvTitulo.text = chatTitulo

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMensajes)
        rvMensajes.layoutManager = LinearLayoutManager(this)
        rvMensajes.adapter = object : RecyclerView.Adapter<SimpleVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleVH {
                val v = TextView(parent.context)
                v.setPadding(16, 12, 16, 12)
                return SimpleVH(v)
            }

            override fun onBindViewHolder(holder: SimpleVH, position: Int) {
                (holder.itemView as TextView).text = listaMensajes[position]
            }

            override fun getItemCount() = listaMensajes.size
        }

        btnEnviar.setOnClickListener {
            val texto = etMensaje.text.toString().trim()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = SesionActiva.usuarioActual
            val nombre = user?.nombre ?: "Tú"
            val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            val msg = "$nombre: $texto  ($hora)"
            listaMensajes.add(msg)
            rvMensajes.adapter?.notifyItemInserted(listaMensajes.size - 1)
            etMensaje.setText("")
            rvMensajes.scrollToPosition(listaMensajes.size - 1)
        }
    }

    class SimpleVH(v: android.view.View) : RecyclerView.ViewHolder(v)
}
