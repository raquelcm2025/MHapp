package com.example.myhobbiesapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(ctx: Context) : SQLiteOpenHelper(ctx, "mhapp.db", null, 3) {
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS usuario(
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            nombre   TEXT NOT NULL,
            apellido TEXT NOT NULL,
            correo   TEXT NOT NULL UNIQUE,
            celular  TEXT,
            clave    TEXT NOT NULL,
            genero   TEXT,
            acepta_terminos INTEGER DEFAULT 0,
            foto     INTEGER NOT NULL
        )
    """.trimIndent())

        db.execSQL("""
        CREATE TABLE IF NOT EXISTS chat(
            id_chat     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            id_emisor   INTEGER NOT NULL,
            id_receptor INTEGER NOT NULL,
            mensaje     TEXT    NOT NULL,
            fecha_envio TEXT    NOT NULL
        )
    """.trimIndent())

        db.execSQL("""
        CREATE TABLE IF NOT EXISTS amistad(
            id_amistad     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            id_solicitante INTEGER NOT NULL,
            id_receptor    INTEGER NOT NULL,
            estado         TEXT NOT NULL
        )
    """.trimIndent())
    }


    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        // reset limpio
        db.execSQL("DROP TABLE IF EXISTS amistad")
        db.execSQL("DROP TABLE IF EXISTS chat")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }
}
