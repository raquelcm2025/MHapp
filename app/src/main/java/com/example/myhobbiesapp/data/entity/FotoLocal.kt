package com.example.myhobbiesapp.data.entity

data class FotoLocal(
    val id: Int = 0,
    val userId: String,
    val uri: String,
    val createdAt: Long = System.currentTimeMillis()
)