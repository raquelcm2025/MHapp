package com.example.myhobbiesapp.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "myhobbies.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        crearTablaFotos(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS foto_local")
        db.execSQL("DROP TABLE IF EXISTS usuario_hobby")
        db.execSQL("DROP TABLE IF EXISTS hobby")
        db.execSQL("DROP TABLE IF EXISTS usuario")

        crearTablaFotos(db)
    }

    private fun crearTablaFotos(db: SQLiteDatabase) {
        db.execSQL("""
           CREATE TABLE IF NOT EXISTS foto_local(
              id        INTEGER PRIMARY KEY AUTOINCREMENT,
              user_id   TEXT    NOT NULL,  
              uri       TEXT    NOT NULL,
              created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
            );
        """.trimIndent())
    }
}