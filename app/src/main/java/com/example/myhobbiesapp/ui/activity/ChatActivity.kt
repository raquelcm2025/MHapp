package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.repo.ChatMessage
import com.example.myhobbiesapp.data.repo.ChatsRepo
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.util.CurrentUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity(R.layout.activity_chat) {


    private lateinit var rvMensajes: RecyclerView
    private lateinit var etMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvTitulo: TextView

    private var chatId: Int = -1
    private val listaMensajes = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvMensajes = findViewById(R.id.rvMensajes)
        etMensaje  = findViewById(R.id.etMensaje)
        btnEnviar  = findViewById(R.id.btnEnviar)
        tvTitulo   = findViewById(R.id.tvTituloChat)

        chatId = intent.getIntExtra("chatId", -1)

        // En CHATS todos ya son amigos
        val chat = ChatsRepo.get(chatId)
        tvTitulo.text = chat?.let { "Chateando con ${it.titulo}" } ?: "Chat"

        // Cargar historial persistido en memoria
        listaMensajes.clear()
        listaMensajes.addAll(ChatsRepo.getMensajes(chatId))

        rvMensajes.layoutManager = LinearLayoutManager(this)
        rvMensajes.adapter = object : RecyclerView.Adapter<SimpleVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleVH {
                val v = layoutInflater.inflate(R.layout.item_chat_msg, parent, false)
                return SimpleVH(v)
            }
            override fun onBindViewHolder(holder: SimpleVH, position: Int) {
                val m = listaMensajes[position]
                val tv = holder.itemView.findViewById<TextView>(R.id.tvMsg)
                val btn = holder.itemView.findViewById<ImageButton>(R.id.btnBorrar)

                val myId = SesionActiva.usuarioActual?.id
                tv.text = if (m.autorId == myId)
                    "Tú: ${m.texto}${if (m.hora.isNotBlank()) " (${m.hora})" else ""}"
                else
                    "${m.texto}${if (m.hora.isNotBlank()) " (${m.hora})" else ""}"

                val soyYo = (m.autorId == myId)
                btn.visibility = if (soyYo) View.VISIBLE else View.GONE
                btn.setOnClickListener {
                    val idx = holder.bindingAdapterPosition
                    if (idx != RecyclerView.NO_POSITION) {
                        // Elimina del repo y del listado
                        ChatsRepo.eliminarMensaje(chatId, idx)
                        listaMensajes.removeAt(idx)
                        notifyItemRemoved(idx)
                        Toast.makeText(this@ChatActivity, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun getItemCount() = listaMensajes.size
        }

        btnEnviar.setOnClickListener {
            val texto = etMensaje.text.toString()
            if (texto.isEmpty()) return@setOnClickListener

            val myId = SesionActiva.usuarioActual?.id
            val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val msg = ChatMessage(myId, texto, hora)

            // Guarda en repo (para que persista al volver)
            ChatsRepo.agregarMensaje(chatId, msg)

            // Refleja en pantalla
            listaMensajes.add(msg)
            rvMensajes.adapter?.notifyItemInserted(listaMensajes.size - 1)
            rvMensajes.scrollToPosition(listaMensajes.size - 1)
            etMensaje.setText("")
        }
    }

    class SimpleVH(v: View) : RecyclerView.ViewHolder(v)
}