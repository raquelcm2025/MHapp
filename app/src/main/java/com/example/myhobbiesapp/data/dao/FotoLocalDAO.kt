package com.example.myhobbiesapp.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.myhobbiesapp.data.database.AppDatabaseHelper
import com.example.myhobbiesapp.data.entity.FotoLocal

class FotoLocalDAO(context: Context) {
    private val dbh = AppDatabaseHelper(context)

    fun insert(f: FotoLocal): Long {
        val db = dbh.writableDatabase
        val v = ContentValues().apply {
            put("user_id", f.userId)
            put("uri", f.uri)
            put("created_at", f.createdAt)
        }
        val id = db.insert("foto_local", null, v)
        db.close()
        return id
    }

    fun listByUser(userId: Int): List<FotoLocal> {
        val db = dbh.readableDatabase
        val out = mutableListOf<FotoLocal>()
        db.rawQuery(
            "SELECT id, user_id, uri, created_at FROM foto_local WHERE user_id=? ORDER BY created_at DESC",
            arrayOf(userId.toString())
        ).use { c ->
            if (c.moveToFirst()) {
                do {
                    out.add(
                        FotoLocal(
                            id = c.getInt(0),
                            userId = c.getInt(1),
                            uri = c.getString(2),
                            createdAt = c.getLong(3)
                        )
                    )
                } while (c.moveToNext())
            }
        }
        db.close()
        return out
    }

    fun topNByUser(userId: Int, n: Int): List<FotoLocal> =
        listByUser(userId).take(n)

    fun delete(id: Int): Int {
        val db = dbh.writableDatabase
        val rows = db.delete("foto_local", "id=?", arrayOf(id.toString()))
        db.close()
        return rows
    }
}
