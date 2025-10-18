package com.example.myhobbiesapp.entity

data class Hobby(
    val id: Int = 0,
    val nombre: String,
    val nota: String = "",
    val fecha: String = "",
    val idUsuario: Int
)
