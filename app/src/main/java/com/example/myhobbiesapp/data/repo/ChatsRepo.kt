package com.example.myhobbiesapp.data.repo

import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Chat

data class ChatMessage(val autorId: Int?, val texto: String, val hora: String)

object ChatsRepo {
    private val chats = mutableListOf(
        Chat(1001, "Marco Duarte López",  "¡Hola! ¿Vamos al concierto?", avatar = R.drawable.ic_hombre),
        Chat(1002, "Luz Domínguez Gómez", "Tengo una receta nueva 👩‍🍳",    avatar = R.drawable.ic_mujer),
        Chat(1003, "Josué Romero Loayza", "¿Vamos al taller de cerámica?",  avatar = R.drawable.ic_hombre)
    )

    private val mensajesPorChat = mutableMapOf<Int, MutableList<ChatMessage>>().apply {
        chats.forEach { chat ->
            if (chat.ultimoMensaje.isNotBlank()) {
                put(chat.id, mutableListOf(ChatMessage(null, chat.ultimoMensaje, "")))
            }
        }
    }

    fun listar(): List<Chat> = chats.toList()

    fun get(chatId: Int): Chat? = chats.find { it.id == chatId }

    fun actualizarUltimo(chatId: Int, texto: String) {
        val i = chats.indexOfFirst { it.id == chatId }
        if (i >= 0) chats[i] = chats[i].copy(ultimoMensaje = texto)
    }

    fun getMensajes(chatId: Int): List<ChatMessage> =
        mensajesPorChat[chatId]?.toList() ?: emptyList()

    fun agregarMensaje(chatId: Int, msg: ChatMessage) {
        val lista = mensajesPorChat.getOrPut(chatId) { mutableListOf() }
        lista.add(msg)
        val prefijo = if (msg.autorId != null) "Tú: " else ""
        actualizarUltimo(chatId, "$prefijo${msg.texto}")
    }

    fun eliminarMensaje(chatId: Int, index: Int) {
        val lista = mensajesPorChat[chatId] ?: return
        if (index in 0 until lista.size) {
            lista.removeAt(index)
        }
        val ultimo = lista.lastOrNull()
        if (ultimo != null) {
            val prefijo = if (ultimo.autorId != null) "Tú: " else ""
            actualizarUltimo(chatId, "$prefijo${ultimo.texto}")
        }
    }
}
