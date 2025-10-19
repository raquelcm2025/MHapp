package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.myhobbiesapp.data.AppDatabaseHelper
import com.example.myhobbiesapp.entity.Hobby

class HobbyDAO(private val ctx: Context) {
    private val dbh = AppDatabaseHelper(ctx)



    fun listByUser(idUsuario: Int): List<Hobby> {
        val db = dbh.readableDatabase
        val c = db.rawQuery(
            "SELECT id_hobby, nombre, id_usuario, amigos FROM hobby WHERE id_usuario=? ORDER BY id_hobby DESC",
            arrayOf(idUsuario.toString())
        )
        val out = mutableListOf<Hobby>()
        c.use {
            while (it.moveToNext()) {
                out += Hobby(
                    id = it.getInt(it.getColumnIndexOrThrow("id_hobby")),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                    amigos = it.getInt(it.getColumnIndexOrThrow("amigos")),
                    idUsuario = it.getInt(it.getColumnIndexOrThrow("id_usuario"))
                )
            }
        }
        return out
    }




    fun insert(h: Hobby): Long {
        val db = dbh.writableDatabase
        db.beginTransaction()
        return try {
            val v = ContentValues().apply {
                put("nombre", h.nombre)
                put("id_usuario", h.idUsuario)
                put("amigos", h.amigos)
            }
            val newId = db.insert("hobby", null, v)

            if (newId > 0) {
                val vh = ContentValues().apply {
                    put("id_usuario", h.idUsuario)
                    put("nombre", h.nombre)
                    put("accion", "CREADO")
                    put("amigos", h.amigos)
                }
                db.insert("hobby_historial", null, vh)
            }
            db.setTransactionSuccessful()
            newId
        } finally { db.endTransaction() }
    }

    fun deleteById(idHobby: Int): Int {
        val db = dbh.writableDatabase
        db.beginTransaction()
        return try {
            var nombre = ""; var idUsuario = -1; var amigos = 0
            db.rawQuery(
                "SELECT nombre, id_usuario, amigos FROM hobby WHERE id_hobby=?",
                arrayOf(idHobby.toString())
            ).use { c ->
                if (c.moveToFirst()) {
                    nombre = c.getString(0)
                    idUsuario = c.getInt(1)
                    amigos = c.getInt(2)
                }
            }

            val rows = db.delete("hobby", "id_hobby=?", arrayOf(idHobby.toString()))
            if (rows > 0 && idUsuario != -1) {
                val vh = ContentValues().apply {
                    put("id_usuario", idUsuario)
                    put("nombre", nombre)
                    put("accion", "ELIMINADO")
                    put("amigos", amigos)
                }
                db.insert("hobby_historial", null, vh)
            }
            db.setTransactionSuccessful()
            rows
        } finally { db.endTransaction() }
    }

    fun listHistorialByUser(idUsuario: Int): List<Pair<String, Int>> {
        val db = dbh.readableDatabase
        val out = mutableListOf<Pair<String, Int>>()
        db.rawQuery(
            "SELECT nombre, amigos FROM hobby_historial WHERE id_usuario=? ORDER BY id_hist DESC",
            arrayOf(idUsuario.toString())
        ).use { c ->
            while (c.moveToNext()) out += c.getString(0) to c.getInt(1)
        }
        return out
    }

}
