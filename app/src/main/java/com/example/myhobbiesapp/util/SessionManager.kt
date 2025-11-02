package com.example.myhobbiesapp.util

import android.content.Context

object SessionManager {
    private const val PREFS = "mh_session"
    private const val KEY_EMAIL = "current_email"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun setCurrentEmail(ctx: Context, email: String) {
        prefs(ctx).edit().putString(KEY_EMAIL, email).apply()
    }

    fun getCurrentEmail(ctx: Context): String? =
        prefs(ctx).getString(KEY_EMAIL, null)

    fun clear(ctx: Context) {
        prefs(ctx).edit().remove(KEY_EMAIL).apply()
    }

    fun saveCurrentEmail(ctx: Context, email: String) = setCurrentEmail(ctx, email)
    fun getEmail(ctx: Context) = getCurrentEmail(ctx)
}
