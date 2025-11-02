package com.example.myhobbiesapp.firebase.model

data class ChatMessage(
    val fromUid: String = "",
    val body: String = "",
    val ts: Long = 0L
)
