package com.example.myhobbiesapp.data.entity

import com.example.myhobbiesapp.R


data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val celular: String,

    // 1. Por seguridad (nunca guardar la clave real)
    val claveHash: String,

    val genero: String?,
    val aceptaTerminos: Boolean,

    var foto: Int = R.drawable.ic_person
)
