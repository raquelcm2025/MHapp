package com.example.myhobbiesapp

data class Usuario(
    var codigo: Int = 0,
    var nombres: String = "",
    var apellidos: String = "",
    var correo: String = "",
    var clave: String = "",
    var celular: String = "",
    var fechaNacimiento: String = "",                // (yyyy-MM-dd)
    var genero: String = "",
    var fotoPerfil: String = "",                      // URL de foto
    var pais: String = "",
    var fechaRegistro: String = "",
    var estadoCuenta: Boolean = true,                  // Activo
    var hobbiesFavoritos: List<String> = emptyList()    // Lista de hobbies
)
