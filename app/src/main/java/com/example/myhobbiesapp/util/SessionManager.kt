package com.example.myhobbiesapp.util

import android.content.Context

object SessionManager {
    private const val PREFS = "mh_session"
    private const val KEY_EMAIL = "current_email"

    fun saveCurrentEmail(ctx: Context, email: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_EMAIL, email).apply()
    }

    fun getCurrentEmail(ctx: Context): String? {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, null)
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
