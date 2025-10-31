package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myhobbiesapp.data.database.AppDatabaseHelper
import com.example.myhobbiesapp.data.entity.Hobby

class HobbyDAO(context: Context) {
    private val dbh = AppDatabaseHelper(context)

    private inline fun <T> read(block: (SQLiteDatabase) -> T): T {
        val db = dbh.readableDatabase
        return try { block(db) } finally { db.close() }
    }
    private inline fun <T> write(block: (SQLiteDatabase) -> T): T {
        val db = dbh.writableDatabase
        return try { block(db) } finally { db.close() }
    }

    // === CRUD Hobby ===
    fun getIdByName(nombre: String): Int = read { db ->
        db.rawQuery(
            "SELECT id_hobby FROM hobby WHERE LOWER(nombre)=LOWER(?)",
            arrayOf(nombre.trim())
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else -1 }
    }

    fun getOrCreateByName(nombre: String): Int = write { db ->
        val name = nombre.trim()
        db.rawQuery("SELECT id_hobby FROM hobby WHERE nombre=?", arrayOf(name)).use { c ->
            if (c.moveToFirst()) return@write c.getInt(0)
        }
        val v = ContentValues().apply { put("nombre", name) }
        db.insert("hobby", null, v).toInt()
    }

    fun getAll(): List<Hobby> = read { db ->
        val lista = mutableListOf<Hobby>()
        db.rawQuery(
            "SELECT id_hobby, nombre FROM hobby ORDER BY nombre COLLATE NOCASE",
            null
        ).use { c ->
            if (c.moveToFirst()) do {
                lista.add(Hobby(id = c.getInt(0), nombre = c.getString(1)))
            } while (c.moveToNext())
        }
        lista
    }

    // === Relación usuario_hobby ===
    fun linkUsuarioHobby(idUsuario: Int, idHobby: Int): Boolean = write { db ->
        val v = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_hobby", idHobby)
        }
        val rowId = db.insertWithOnConflict(
            "usuario_hobby", null, v, SQLiteDatabase.CONFLICT_IGNORE
        )
        rowId != -1L
    }

    fun unlinkUsuarioHobby(idUsuario: Int, idHobby: Int): Int = write { db ->
        db.delete(
            "usuario_hobby",
            "id_usuario=? AND id_hobby=?",
            arrayOf(idUsuario.toString(), idHobby.toString())
        )
    }

    fun unlinkUsuarioHobbyByName(idUsuario: Int, nombreHobby: String): Int {
        val idH = getIdByName(nombreHobby)
        if (idH <= 0) return 0
        return unlinkUsuarioHobby(idUsuario, idH)
    }


    fun updateHobbyName(idHobby: Int, nuevoNombre: String): Int = write { db ->
        val v = ContentValues().apply { put("nombre", nuevoNombre.trim()) }
        db.update("hobby", v, "id_hobby=?", arrayOf(idHobby.toString()))
    }

    /** Evita duplicados visuales/errores de UI: */
    fun existsUsuarioHobby(idUsuario: Int, idHobby: Int): Boolean = read { db ->
        db.rawQuery(
            "SELECT 1 FROM usuario_hobby WHERE id_usuario=? AND id_hobby=? LIMIT 1",
            arrayOf(idUsuario.toString(), idHobby.toString())
        ).use { it.moveToFirst() }
    }

    /** Limpia hobbies huérfanos (nadie los usa). Llamar si quieres mantener tabla prolija. */
    fun deleteOrphanHobbies(): Int = write { db ->
        db.delete(
            "hobby",
            "id_hobby NOT IN (SELECT DISTINCT id_hobby FROM usuario_hobby)",
            null
        )
    }

    /** NUEVO: lista solo nombres de hobbies por usuario */
    fun listHistorialByUserNames(idUsuario: Int): List<String> = read { db ->
        val lista = mutableListOf<String>()
        db.rawQuery(
            """
        SELECT h.nombre
        FROM hobby h
        JOIN usuario_hobby uh ON uh.id_hobby = h.id_hobby
        WHERE uh.id_usuario = ?
        ORDER BY h.nombre COLLATE NOCASE
        """.trimIndent(),
            arrayOf(idUsuario.toString())
        ).use { c ->
            if (c.moveToFirst()) {
                do lista.add(c.getString(0)) while (c.moveToNext())
            }
        }
        lista
    }

    // HobbyDAO — metodo con IDs REALES del usuario
    fun listUserHobbies(idUsuario: Int): List<Hobby> = read { db ->
        val lista = mutableListOf<Hobby>()
        db.rawQuery(
            """
        SELECT h.id_hobby, h.nombre
        FROM hobby h
        JOIN usuario_hobby uh ON uh.id_hobby = h.id_hobby
        WHERE uh.id_usuario = ?
        ORDER BY h.nombre COLLATE NOCASE
        """.trimIndent(),
            arrayOf(idUsuario.toString())
        ).use { c ->
            if (c.moveToFirst()) {
                do { lista.add(Hobby(id = c.getInt(0), nombre = c.getString(1))) }
                while (c.moveToNext())
            }
        }
        lista
    }


    /* ======= MÉTODOS LEGACY PARA EVITAR ERRORES ======= */

    fun listByUser(idUsuario: Int): List<Hobby> = listUserHobbies(idUsuario)


    fun listHistorialByUser(idUsuario: Int): List<Pair<String, Int>> {
        val nombres = listHistorialByUserNames(idUsuario)
        return nombres.map { it to 0 }
    }

}
