package com.example.myhobbiesapp.data

import android.content.Context
import com.example.myhobbiesapp.entity.Usuario

object UsuarioRepository {

    fun getAll(ctx: Context): List<Usuario> =
        UsuarioDAO(ctx).getAll()

    fun getAllExcept(ctx: Context, idExcluido: Int): List<Usuario> =
        UsuarioDAO(ctx).getAllExcept(idExcluido)

    fun getByCorreo(ctx: Context, correo: String): Usuario? =
        UsuarioDAO(ctx).getByCorreo(correo)

    fun insert(ctx: Context, u: Usuario): Long =
        UsuarioDAO(ctx).insert(u)

    fun updateCelular(ctx: Context, idUsuario: Int, celularNuevo: String): Int =
        UsuarioDAO(ctx).updateCelular(idUsuario, celularNuevo)

    fun updateClave(ctx: Context, idUsuario: Int, claveNueva: String): Int =
        UsuarioDAO(ctx).updateClave(idUsuario, claveNueva)
}
