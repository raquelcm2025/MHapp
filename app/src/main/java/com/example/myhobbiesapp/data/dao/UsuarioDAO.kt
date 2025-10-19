package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.AppDatabaseHelper
import com.example.myhobbiesapp.entity.Usuario

class UsuarioDAO(context: Context) {
    private val dbh = AppDatabaseHelper(context)

    private fun getStringSafe(c: Cursor, col: String, def: String = ""): String {
        val idx = c.getColumnIndex(col)
        return if (idx >= 0 && !c.isNull(idx)) c.getString(idx) ?: def else def
    }
    private fun getIntSafe(c: Cursor, col: String, def: Int = 0): Int {
        val idx = c.getColumnIndex(col)
        return if (idx >= 0 && !c.isNull(idx)) c.getInt(idx) else def
    }
    private fun getBoolSafe(c: Cursor, col: String, def: Boolean = false): Boolean {
        val idx = c.getColumnIndex(col)
        return if (idx >= 0 && !c.isNull(idx)) (c.getInt(idx) == 1) else def
    }

    private fun mapUsuario(c: Cursor): Usuario {
        val id       = getIntSafe(c, "id_usuario", 0)
        val nombre   = getStringSafe(c, "nombre")
        val apellido = getStringSafe(c, "apellido")
        val correo   = getStringSafe(c, "correo")
        val celular  = getStringSafe(c, "celular")
        val clave    = getStringSafe(c, "clave")
        val genero   = run {
            val idx = c.getColumnIndex("genero")
            if (idx >= 0 && !c.isNull(idx)) c.getString(idx) else null
        }
        val acepta   = getBoolSafe(c, "acepta_terminos", false)
        val foto     = getIntSafe(c, "foto", R.drawable.ic_person)

        return Usuario(
            id = id,
            nombre = nombre,
            apellido = apellido,
            correo = correo,
            celular = celular,
            clave = clave,
            genero = genero,
            aceptaTerminos = acepta,
            foto = if (foto != 0) foto else R.drawable.ic_person
        )
    }

    fun getById(id: Int): Usuario? {
        val db = dbh.readableDatabase
        val c = db.rawQuery("SELECT * FROM usuario WHERE id_usuario=? LIMIT 1", arrayOf(id.toString()))
        var u: Usuario? = null
        if (c.moveToFirst()) u = mapUsuario(c)
        c.close(); db.close()
        return u
    }

    fun getByCorreo(correo: String): Usuario? {
        val db = dbh.readableDatabase
        val c = db.rawQuery("SELECT * FROM usuario WHERE correo=? LIMIT 1", arrayOf(correo))
        var u: Usuario? = null
        if (c.moveToFirst()) u = mapUsuario(c)
        c.close(); db.close()
        return u
    }

    fun getAll(): List<Usuario> {
        val db = dbh.readableDatabase
        val out = mutableListOf<Usuario>()
        val c = db.rawQuery("SELECT * FROM usuario ORDER BY nombre COLLATE NOCASE, apellido COLLATE NOCASE", null)
        while (c.moveToNext()) out.add(mapUsuario(c))
        c.close(); db.close()
        return out
    }

    fun getAllExcept(idExcluido: Int): List<Usuario> = listarTodosMenos(idExcluido)

    fun listarTodosMenos(idExcluido: Int): List<Usuario> {
        val db = dbh.readableDatabase
        val out = mutableListOf<Usuario>()
        val c = db.rawQuery(
            """
            SELECT * FROM usuario
            WHERE id_usuario <> ?
            ORDER BY nombre COLLATE NOCASE, apellido COLLATE NOCASE
            """.trimIndent(),
            arrayOf(idExcluido.toString())
        )
        while (c.moveToNext()) out.add(mapUsuario(c))
        c.close(); db.close()
        return out
    }

    fun insert(u: Usuario): Long {
        val db = dbh.writableDatabase
        val cv = ContentValues().apply {
            put("nombre", u.nombre)
            put("apellido", u.apellido)
            put("correo", u.correo)
            put("celular", u.celular)
            put("clave", u.clave)
            put("genero", u.genero)
            put("acepta_terminos", if (u.aceptaTerminos) 1 else 0)
            put("foto", u.foto)
        }
        val id = db.insert("usuario", null, cv)
        db.close()
        return id
    }

    fun updateCelular(idUsuario: Int, celularNuevo: String): Int {
        val db = dbh.writableDatabase
        val cv = ContentValues().apply { put("celular", celularNuevo) }
        val rows = db.update("usuario", cv, "id_usuario=?", arrayOf(idUsuario.toString()))
        db.close()
        return rows
    }

    fun updateClave(idUsuario: Int, claveNueva: String): Int {
        val db = dbh.writableDatabase
        val cv = ContentValues().apply { put("clave", claveNueva) }
        val rows = db.update("usuario", cv, "id_usuario=?", arrayOf(idUsuario.toString()))
        db.close()
        return rows
    }
}
