package com.example.myhobbiesapp.util

import android.content.Context
import com.example.myhobbiesapp.data.dao.UsuarioDAO

object CurrentUser {
    fun idFromSession(ctx: Context): Int? {
        val email = SessionManager.getCurrentEmail(ctx) ?: return null
        val u = UsuarioDAO(ctx).getByCorreo(email) ?: return null
        return u.id
    }
}
