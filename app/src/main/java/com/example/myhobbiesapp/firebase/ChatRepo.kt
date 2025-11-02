package com.example.myhobbiesapp.firebase

import com.google.firebase.database.FirebaseDatabase

object ChatRepo {
    fun chatIdFor(a: String, b: String): String {
        return if (a <= b) "${a}_${b}" else "${b}_${a}"
    }

    fun ensureChat(aUid: String, bUid: String, onReady: (String) -> Unit) {
        val chatId = chatIdFor(aUid, bUid)
        val chats = FirebaseDatabase.getInstance().getReference("chats").child(chatId)

        // Usamos una operación en paralelo para asegurarnos de que ambos miembros se añadan
        val updates = mapOf(
            "members/$aUid" to true,
            "members/$bUid" to true
        )

        // Esto crea el nodo /chats/chatId/members
        chats.updateChildren(updates).addOnCompleteListener {
            onReady(chatId)
        }
    }
}