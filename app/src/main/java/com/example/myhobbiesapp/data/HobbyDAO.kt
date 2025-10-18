package com.example.myhobbiesapp.data

import android.content.ContentValues
import android.content.Context
import com.example.myhobbiesapp.entity.Hobby

class HobbyDAO(context: Context) {
    private val dbh = AppDatabaseHelper(context)

    fun listByUser(idUsuario: Int): List<Hobby> {
        val db = dbh.readableDatabase
        val out = mutableListOf<Hobby>()
        val c = db.rawQuery(
            "SELECT id_hobby, nombre, nota, fecha FROM hobby WHERE id_usuario=? ORDER BY id_hobby DESC",
            arrayOf(idUsuario.toString())
        )
        while (c.moveToNext()) {
            out.add(
                Hobby(
                    id = c.getInt(0),
                    nombre = c.getString(1) ?: "",
                    nota = c.getString(2) ?: "",
                    fecha = c.getString(3) ?: "",
                    idUsuario = idUsuario
                )
            )
        }
        c.close(); db.close()
        return out
    }

    fun insert(h: Hobby): Long {
        val db = dbh.writableDatabase
        val cv = ContentValues().apply {
            put("nombre", h.nombre)
            put("nota", h.nota)
            put("fecha", h.fecha)
            put("id_usuario", h.idUsuario)
        }
        val r = db.insert("hobby", null, cv)
        db.close()
        return r
    }

    fun deleteById(id: Int): Int {
        val db = dbh.writableDatabase
        val rows = db.delete("hobby", "id_hobby=?", arrayOf(id.toString()))
        db.close()
        return rows
    }
    fun getById(id: Int): Hobby? {
        val db = dbh.readableDatabase
        val c = db.rawQuery(
            "SELECT id_hobby, nombre, nota, fecha, id_usuario FROM hobby WHERE id_hobby=? LIMIT 1",
            arrayOf(id.toString())
        )

        var out: Hobby? = null
        if (c.moveToFirst()) {
            out = Hobby(
                id = c.getInt(0),
                nombre = c.getString(1) ?: "",
                nota = c.getString(2) ?: "",
                fecha = c.getString(3) ?: "",
                idUsuario = c.getInt(4)
            )
        }
        c.close()
        db.close()
        return out
    }

    fun update(h: Hobby): Int {
        val db = dbh.writableDatabase
        val cv = android.content.ContentValues().apply {
            put("nombre", h.nombre)
            put("nota", h.nota)
            put("fecha", h.fecha)
            put("id_usuario", h.idUsuario)
        }
        val rows = db.update("hobby", cv, "id_hobby=?", arrayOf(h.id.toString()))
        db.close()
        return rows
    }

}
