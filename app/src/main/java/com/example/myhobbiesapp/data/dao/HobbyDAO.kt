package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.myhobbiesapp.data.database.DatabaseHelper
import com.example.myhobbiesapp.data.entity.Hobby

class HobbyDAO(context: Context) {
    private val dbh = DatabaseHelper(context)

    fun insert(h: Hobby): Long {
        val db = dbh.writableDatabase
        val v = ContentValues().apply { put("nombre", h.nombre.trim()) }
        val id = db.insert("hobby", null, v)
        db.close()
        return id
    }

    fun deleteById(id: Int): Int {
        val db = dbh.writableDatabase
        val rows = db.delete("hobby", "id_hobby=?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun getAll(): List<Hobby> {
        val db = dbh.readableDatabase
        val c = db.rawQuery("SELECT id_hobby, nombre FROM hobby ORDER BY nombre", null)
        val out = mutableListOf<Hobby>()
        if (c.moveToFirst()) {
            do out.add(Hobby(c.getInt(0), c.getString(1), amigos = 0)) while (c.moveToNext())
        }
        c.close(); db.close()
        return out
    }

    fun getOrCreateByName(nombre: String): Int {
        val name = nombre.trim()
        val db = dbh.writableDatabase
        // ¿existe?
        db.rawQuery("SELECT id_hobby FROM hobby WHERE nombre = ?", arrayOf(name)).use { c ->
            if (c.moveToFirst()) {
                val id = c.getInt(0)
                db.close()
                return id
            }
        }
        // crear
        val v = ContentValues().apply { put("nombre", name) }
        val rowId = db.insert("hobby", null, v)
        db.close()
        return rowId.toInt()
    }

    fun linkUsuarioHobby(userId: Int, hobbyId: Int): Int {
        val db = dbh.writableDatabase
        val v = ContentValues().apply {
            put("id_usuario", userId)
            put("id_hobby", hobbyId)
        }
        val rowId = db.insertWithOnConflict("usuario_hobby", null, v, 4) // CONFLICT_IGNORE
        db.close()
        return if (rowId == -1L) 0 else 1
    }

    fun unlinkUsuarioHobby(userId: Int, hobbyId: Int): Int {
        val db = dbh.writableDatabase
        val rows = db.delete(
            "usuario_hobby",
            "id_usuario=? AND id_hobby=?",
            arrayOf(userId.toString(), hobbyId.toString())
        )
        db.close()
        return rows
    }

    /** Hobbies del usuario con conteo de amigos (otros usuarios) por hobby */
    fun listByUser(userId: Int): List<Hobby> {
        val sql = """
            SELECT h.id_hobby,
                   h.nombre,
                   -- amigos = todos los usuarios que tienen este hobby excepto yo
                   (SELECT COUNT(*) FROM usuario_hobby uh2 WHERE uh2.id_hobby = h.id_hobby AND uh2.id_usuario <> ?) AS amigos
            FROM hobby h
            INNER JOIN usuario_hobby uh ON uh.id_hobby = h.id_hobby
            WHERE uh.id_usuario = ?
            ORDER BY h.nombre
        """.trimIndent()
        val db = dbh.readableDatabase
        val out = mutableListOf<Hobby>()
        db.rawQuery(sql, arrayOf(userId.toString(), userId.toString())).use { c ->
            if (c.moveToFirst()) {
                do {
                    out.add(
                        Hobby(
                            id = c.getInt(0),
                            nombre = c.getString(1),
                            amigos = c.getInt(2)
                        )
                    )
                } while (c.moveToNext())
            }
        }
        db.close()
        return out
    }

    /** Historial por usuario: nombre del hobby + cantidad de amigos */
    fun listHistorialByUser(userId: Int): List<Pair<String, Int>> {
        val sql = """
            SELECT h.nombre,
                   (SELECT COUNT(*) FROM usuario_hobby uh2 WHERE uh2.id_hobby = h.id_hobby AND uh2.id_usuario <> ?) AS amigos
            FROM hobby h
            INNER JOIN usuario_hobby uh ON uh.id_hobby = h.id_hobby
            WHERE uh.id_usuario = ?
            ORDER BY h.nombre
        """.trimIndent()
        val db = dbh.readableDatabase
        val out = mutableListOf<Pair<String, Int>>()
        db.rawQuery(sql, arrayOf(userId.toString(), userId.toString())).use { c ->
            if (c.moveToFirst()) {
                do out.add(Pair(c.getString(0), c.getInt(1))) while (c.moveToNext())
            }
        }
        db.close()
        return out
    }
}
