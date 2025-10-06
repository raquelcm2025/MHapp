package com.example.myhobbiesapp.core

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.myhobbiesapp.entity.Usuario

object UserStore {
    private const val PREFS = "mh_users"
    private const val K_USERS = "users_json"
    private const val K_LOGGED_EMAIL = "logged_email"

    /** Semilla de demo users si está vacío */
    fun ensureSeed(ctx: Context) {
        if (getAllUsers(ctx).isNotEmpty()) return
        val seed = listOf(
            Usuario("Raquel", "Callata Mamani","raquelcm@mh.pe","246810"),
            Usuario("Martha", "Lopez Valencia","marlova@hotmail.com","000000"),
            Usuario("Luis", "Gomez Moreno","lgomez@gmail.com","123456")
        )
        seed.forEach { saveUser(ctx, it) }
    }

    fun saveUser(ctx: Context, u: Usuario) {
        val map = getAllUsers(ctx).toMutableMap()
        map[u.correo.lowercase()] = u
        putAllUsers(ctx, map)
    }

    fun authenticate(ctx: Context, email: String, pass: String): Usuario? {
        val u = getAllUsers(ctx)[email.lowercase()] ?: return null
        return if (u.clave == pass) u else null
    }

    fun setLogged(ctx: Context, email: String?) {
        prefs(ctx).edit().putString(K_LOGGED_EMAIL, email).apply()
    }

    fun getLogged(ctx: Context): Usuario? {
        val email = prefs(ctx).getString(K_LOGGED_EMAIL, null) ?: return null
        return getAllUsers(ctx)[email.lowercase()]
    }

    fun logout(ctx: Context) = setLogged(ctx, null)

    private fun getAllUsers(ctx: Context): Map<String, Usuario> {
        val raw = prefs(ctx).getString(K_USERS, null) ?: return emptyMap()
        val arr = JSONArray(raw)
        val map = mutableMapOf<String, Usuario>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val correo = o.getString("correo")
            val usuario = Usuario(
                nombres = o.getString("nombres"),
                apellidos = o.getString("apellidos"),
                correo = correo,
                clave = o.getString("clave"),
                celular = o.optString("celular", null),
                genero = o.optString("genero", null),
                hobbies = jsonArrayToList(o.optJSONArray("hobbies"))
            )
            map[correo.lowercase()] = usuario
        }
        return map
    }

    private fun putAllUsers(ctx: Context, map: Map<String, Usuario>) {
        val arr = JSONArray()
        map.values.forEach { u ->
            val o = JSONObject().apply {
                put("nombres", u.nombres)
                put("apellidos", u.apellidos)
                put("correo", u.correo)
                put("clave", u.clave)
                put("celular", u.celular)
                put("genero", u.genero)
                put("hobbies", JSONArray(u.hobbies))
            }
            arr.put(o)
        }
        prefs(ctx).edit().putString(K_USERS, arr.toString()).apply()
    }

    private fun jsonArrayToList(a: JSONArray?): List<String> {
        if (a == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until a.length()) list.add(a.getString(i))
        return list
    }

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
