package com.example.myhobbiesapp.entity

import com.example.myhobbiesapp.R

data class Usuario(
    val id: Int = 0,
    var nombre: String,
    var apellido: String,
    var correo: String,
    var celular: String,
    var clave: String,
    var genero: String?,
    var aceptaTerminos: Boolean,
    var foto: Int = R.drawable.ic_person
)
