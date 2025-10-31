package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myhobbiesapp.data.database.AppDatabaseHelper
import com.example.myhobbiesapp.data.entity.Usuario

class UsuarioDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    private fun getStringSafe(c: Cursor, col: String): String {
        val i = c.getColumnIndex(col)
        return if (i != -1) c.getString(i) ?: "" else ""
    }

    private fun splitApellidos(a: String): Pair<String,String> {
        val p = a.trim().split(" ")
        return if (p.size >= 2) p.first() to p.drop(1).joinToString(" ") else a to ""
    }

    fun getAll(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT * FROM usuario", null)
        val lista = mutableListOf<Usuario>()
        if (c.moveToFirst()) {
            do {
                val apellido = getStringSafe(c, "apellido")
                val parts = apellido.trim().split(" ")
                val apPat = parts.firstOrNull().orEmpty()
                val apMat = parts.drop(1).joinToString(" ")

                lista.add(
                    Usuario(
                        id = c.getInt(c.getColumnIndex("id_usuario")),
                        nombre = getStringSafe(c, "nombre"),
                        apellidoPaterno = apPat,
                        apellidoMaterno = apMat,
                        correo = getStringSafe(c, "correo"),
                        celular = getStringSafe(c, "celular"),
                        claveHash = getStringSafe(c, "clave"),
                        genero = getStringSafe(c, "genero"),
                        aceptaTerminos = (c.getInt(c.getColumnIndex("acepta_terminos")) == 1),
                        foto = c.getInt(c.getColumnIndex("foto"))
                    )
                )
            } while (c.moveToNext())
        }
        c.close(); db.close()
        return lista
    }


    fun insert(u: Usuario): Long {
        val db = dbHelper.writableDatabase
        val ap = "${u.apellidoPaterno} ${u.apellidoMaterno}".trim()
        val v = ContentValues().apply {
            put("nombre", u.nombre)
            put("apellido", ap)
            put("correo", u.correo)
            put("celular", u.celular)
            put("clave", u.claveHash)
            put("genero", u.genero)
            put("acepta_terminos", if (u.aceptaTerminos) 1 else 0)
            put("foto", u.foto)
        }
        val id = db.insert("usuario", null, v)
        db.close()
        return id
    }

    fun getById(id: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT * FROM usuario WHERE id_usuario=?", arrayOf(id.toString()))
        var u: Usuario? = null
        if (c.moveToFirst()) {
            val (apPat, apMat) = splitApellidos(getStringSafe(c, "apellido"))
            u = Usuario(
                id = c.getInt(c.getColumnIndex("id_usuario")),
                nombre = getStringSafe(c, "nombre"),
                apellidoPaterno = apPat,
                apellidoMaterno = apMat,
                correo = getStringSafe(c, "correo"),
                celular = getStringSafe(c, "celular"),
                claveHash = getStringSafe(c, "clave"),
                genero = getStringSafe(c, "genero"),
                aceptaTerminos = (c.getInt(c.getColumnIndex("acepta_terminos")) == 1),
                foto = c.getInt(c.getColumnIndex("foto"))
            )
        }
        c.close(); db.close()
        return u
    }

    fun getByCorreo(correo: String): Usuario? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT * FROM usuario WHERE correo=?", arrayOf(correo))
        var u: Usuario? = null
        if (c.moveToFirst()) {
            val (apPat, apMat) = splitApellidos(getStringSafe(c, "apellido"))
            u = Usuario(
                id = c.getInt(c.getColumnIndex("id_usuario")),
                nombre = getStringSafe(c, "nombre"),
                apellidoPaterno = apPat,
                apellidoMaterno = apMat,
                correo = getStringSafe(c, "correo"),
                celular = getStringSafe(c, "celular"),
                claveHash = getStringSafe(c, "clave"),
                genero = getStringSafe(c, "genero"),
                aceptaTerminos = (c.getInt(c.getColumnIndex("acepta_terminos")) == 1),
                foto = c.getInt(c.getColumnIndex("foto"))
            )
        }
        c.close(); db.close()
        return u
    }

    fun updateCelular(id: Int, nuevoCel: String): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply { put("celular", nuevoCel) }
        val rows = db.update("usuario", v, "id_usuario=?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun updateClave(id: Int, nuevaClaveHash: String): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply { put("clave", nuevaClaveHash) }
        val rows = db.update("usuario", v, "id_usuario=?", arrayOf(id.toString()))
        db.close()
        return rows
    }

}
