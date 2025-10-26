package com.example.myhobbiesapp.data.entity

import com.example.myhobbiesapp.R

data class Chat(
    val id: Int,
    val titulo: String,
    val ultimoMensaje: String,
    val avatar: Int = R.drawable.ic_person2
)
