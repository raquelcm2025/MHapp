package com.example.myhobbiesapp.firebase.model

/**
 * Representa la estructura en Firebase en /users/$uid/profile
 */
data class UserProfile(
    // Datos del registro
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val celular: String = "",
    val genero: String = "otro",
    val avatar: String = "otro",
    val aceptaTerminos: Boolean = true,

    val hobbies: Map<String, Boolean> = emptyMap()
)