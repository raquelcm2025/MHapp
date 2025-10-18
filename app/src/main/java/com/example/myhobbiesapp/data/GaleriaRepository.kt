package com.example.myhobbiesapp.data

import com.example.myhobbiesapp.R

object GaleriaRepository {
    private val fotosPorUsuario = mutableMapOf(
        1 to mutableListOf(R.mipmap.hobby_ajedrez, R.mipmap.hobby_fotografia, R.mipmap.hobby_guitarra),
        2 to mutableListOf(R.mipmap.hobby_fotografia, R.mipmap.hobby_cocina),
        3 to mutableListOf(R.mipmap.hobby_guitarra),
        4 to mutableListOf(R.mipmap.hobby_cocina, R.mipmap.hobby_fotografia)
    )

    fun getFotos(idUsuario: Int): List<Int> = fotosPorUsuario[idUsuario] ?: emptyList()

    // opara simular agregar fotos
    fun agregarFoto(idUsuario: Int, resId: Int) {
        val lista = fotosPorUsuario.getOrPut(idUsuario) { mutableListOf() }
        lista.add(resId)
    }
}
