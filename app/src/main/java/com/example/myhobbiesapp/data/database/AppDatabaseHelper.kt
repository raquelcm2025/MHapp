package com.example.myhobbiesapp.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "myhobbies.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuario(
              id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
              nombre TEXT NOT NULL,
              apellido TEXT NOT NULL,
              correo TEXT NOT NULL UNIQUE,
              celular TEXT NOT NULL,
              clave TEXT NOT NULL,
              genero TEXT,
              acepta_terminos INTEGER NOT NULL DEFAULT 0,
              foto INTEGER NOT NULL DEFAULT 0
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hobby(
              id_hobby INTEGER PRIMARY KEY AUTOINCREMENT,
              nombre TEXT NOT NULL UNIQUE
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuario_hobby(
              id_usuario INTEGER NOT NULL,
              id_hobby INTEGER NOT NULL,
              PRIMARY KEY(id_usuario, id_hobby),
              FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
              FOREIGN KEY(id_hobby) REFERENCES hobby(id_hobby) ON DELETE CASCADE
            );
        """.trimIndent())

        db.execSQL("INSERT OR IGNORE INTO hobby(nombre) VALUES ('Cine'),('Música'),('Natación'),('Fútbol'),('Videojuegos');")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) onCreate(db)
    }
}
