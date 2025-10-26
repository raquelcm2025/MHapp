package com.example.myhobbiesapp.data.model

data class DniResponse(
    val dni: String,
    val nombres: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val codVerifica: String? = null,
    val source: String? = null
)
