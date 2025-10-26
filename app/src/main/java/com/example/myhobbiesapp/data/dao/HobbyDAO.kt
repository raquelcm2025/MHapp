package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myhobbiesapp.data.database.AppDatabaseHelper
import com.example.myhobbiesapp.data.entity.Hobby

class HobbyDAO(context: Context) {
    private val dbh = AppDatabaseHelper(context)

    // Utilidades
    private fun Cursor.getIntSafe(col: String): Int {
        val i = getColumnIndex(col); return if (i != -1) getInt(i) else 0
    }
    private fun Cursor.getStringSafe(col: String): String {
        val i = getColumnIndex(col); return if (i != -1) getString(i) ?: "" else ""
    }

    /** Crea o devuelve el id de un hobby por nombre (idempotente) */
    fun getOrCreateByName(nombre: String): Int {
        val name = nombre.trim()
        val db = dbh.writableDatabase
        db.rawQuery("SELECT id_hobby FROM hobby WHERE nombre=?", arrayOf(name)).use { c ->
            if (c.moveToFirst()) {
                val id = c.getInt(0)
                db.close()
                return id
            }
        }
        val v = ContentValues().apply { put("nombre", name) }
        val rowId = db.insert("hobby", null, v) // nombre es UNIQUE
        db.close()
        return rowId.toInt()
    }

    /** Devuelve todos los hobbies (sin filtrar por usuario) */
    fun getAll(): List<Hobby> {
        val db = dbh.readableDatabase
        val list = mutableListOf<Hobby>()
        db.rawQuery("SELECT id_hobby, nombre FROM hobby ORDER BY nombre", null).use { c ->
            if (c.moveToFirst()) {
                do {
                    list.add(
                        Hobby(
                            id = c.getInt(0),
                            nombre = c.getString(1),
                            amigos = 0
                        )
                    )
                } while (c.moveToNext())
            }
        }
        db.close()
        return list
    }

    /** Vincula un hobby a un usuario (ignora si ya existe) */
    fun linkUsuarioHobby(idUsuario: Int, idHobby: Int): Int {
        val db = dbh.writableDatabase
        val v = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_hobby", idHobby)
        }
        // 4 = CONFLICT_IGNORE
        val rowId = db.insertWithOnConflict("usuario_hobby", null, v, 4)
        db.close()
        return if (rowId == -1L) 0 else 1
    }

    /** ❗ Desvincula un hobby de un usuario (ESTE ES EL QUE TE FALTA) */
    fun unlinkUsuarioHobby(idUsuario: Int, idHobby: Int): Int {
        val db = dbh.writableDatabase
        val rows = db.delete(
            "usuario_hobby",
            "id_usuario=? AND id_hobby=?",
            arrayOf(idUsuario.toString(), idHobby.toString())
        )
        db.close()
        return rows
    }

    /**
     * Lista hobbies del usuario con conteo de "amigos" (otros usuarios que también lo tienen).
     * Devuelve List<Hobby> con (id, nombre, amigos)
     */
    fun listByUser(idUsuario: Int): List<Hobby> {
        val sql = """
            SELECT DISTINCT h.id_hobby, h.nombre,
                   (SELECT COUNT(*) 
                      FROM usuario_hobby uh2
                      WHERE uh2.id_hobby = h.id_hobby
                        AND uh2.id_usuario <> ?) AS amigos
            FROM hobby h
            INNER JOIN usuario_hobby uh ON uh.id_hobby = h.id_hobby
            WHERE uh.id_usuario = ?
            ORDER BY h.nombre
        """.trimIndent()

        val db = dbh.readableDatabase
        val list = mutableListOf<Hobby>()
        db.rawQuery(sql, arrayOf(idUsuario.toString(), idUsuario.toString())).use { c ->
            if (c.moveToFirst()) {
                do {
                    list.add(
                        Hobby(
                            id = c.getIntSafe("id_hobby"),
                            nombre = c.getStringSafe("nombre"),
                            amigos = c.getIntSafe("amigos")
                        )
                    )
                } while (c.moveToNext())
            }
        }
        db.close()
        return list
    }
}
