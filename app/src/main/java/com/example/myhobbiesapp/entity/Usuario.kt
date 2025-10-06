package com.example.myhobbiesapp.entity

data class Usuario(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val clave: String,
    val celular: String? = null,
    val genero: String? = null,
    val hobbies: List<String> = emptyList()
)
