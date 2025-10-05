package com.example.myhobbiesapp.entity

data class Usuarios(
    var codigo: Int = 0,
    var nombres: String = "",
    var apellidos: String = "",
    var correo: String = "",
    var clave: String = "",
    var celular: String = "",
    var genero: String = "",
    var fechaRegistro: String = "",
    var hobbiesFavoritos: List<String> = emptyList()
)
