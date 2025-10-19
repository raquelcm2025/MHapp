package com.example.myhobbiesapp.data.repo

import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Chat

object AmistadRepo {
    fun seedFor(userId: Int): List<Chat> {
        return listOf(
            Chat(
                id = 1001,
                titulo = "Marco Duarte López: Arte",
                ultimoMensaje = "¿Qué pinturas te inspiran últimamente?",
                avatar = R.drawable.ic_hombre
            ),
            Chat(
                id = 1002,
                titulo = "Luz Domínguez Gómez: Fotografía",
                ultimoMensaje = "¿Prefieres luz natural o artificial para retratos?",
                avatar = R.drawable.ic_mujer
            ),
            Chat(
                id = 1003,
                titulo = "Josué Romero Loayza: Ciclismo",
                ultimoMensaje = "¿Qué ruta te gustó más el fin de semana?",
                avatar = R.drawable.ic_hombre
            )
        )
    }
}

