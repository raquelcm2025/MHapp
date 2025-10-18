// app/src/main/java/com/example/myhobbiesapp/data/ChatDAO.kt
package com.example.myhobbiesapp.data

import android.content.Context
import com.example.myhobbiesapp.entity.Chat

class ChatDAO(context: Context) {
    fun listByUser(userId: Int): List<Chat> {
        return emptyList()
    }
}
