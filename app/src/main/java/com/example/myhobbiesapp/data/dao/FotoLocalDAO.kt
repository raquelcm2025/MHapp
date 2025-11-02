package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.myhobbiesapp.data.database.AppDatabaseHelper
import com.example.myhobbiesapp.data.entity.FotoLocal

class FotoLocalDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun insert(f: FotoLocal): Long {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("user_id", f.userId)
            put("uri", f.uri)
        }
        val id = db.insert("foto_local", null, v)
        db.close()
        return id
    }

    /**
     * Lista fotos por el UID de Firebase
     */
    fun listByUser(userId: String): List<FotoLocal> {
        val db = dbHelper.readableDatabase
        val out = mutableListOf<FotoLocal>()
        val c = db.rawQuery(
            "SELECT id, user_id, uri FROM foto_local WHERE user_id = ? ORDER BY created_at DESC",
            arrayOf(userId)
        )
        c.use {
            while (it.moveToNext()) {
                out.add(FotoLocal(id = it.getInt(0), userId = it.getString(1), uri = it.getString(2)))
            }
        }
        db.close()
        return out
    }

    /**
     * Cuenta fotos por el UID de Firebase
     */
    fun countByUser(userId: String): Int {
        val db = dbHelper.readableDatabase
        // ¡CAMBIO CLAVE!
        val c = db.rawQuery("SELECT COUNT(*) FROM foto_local WHERE user_id = ?", arrayOf(userId))
        var n = 0
        if (c.moveToFirst()) n = c.getInt(0)
        c.close()
        db.close()
        return n
    }

    /**
     * Lista las 3 más recientes por UID de Firebase
     */
    fun listByUserLimit(userId: String, limit: Int = 3): List<FotoLocal> {
        val db = dbHelper.readableDatabase
        val out = mutableListOf<FotoLocal>()
        // ¡CAMBIO CLAVE!
        val c = db.rawQuery(
            "SELECT id, user_id, uri FROM foto_local WHERE user_id = ? ORDER BY created_at DESC LIMIT ?",
            arrayOf(userId, limit.toString())
        )
        c.use {
            while (it.moveToNext()) {
                out.add(FotoLocal(id = it.getInt(0), userId = it.getString(1), uri = it.getString(2)))
            }
        }
        db.close()
        return out
    }
}