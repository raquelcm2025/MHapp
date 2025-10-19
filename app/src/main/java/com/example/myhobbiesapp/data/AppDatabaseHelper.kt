package com.example.myhobbiesapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(ctx: Context) : SQLiteOpenHelper(ctx, "mhapp.db", null, DB_VERSION) {

    companion object {
        // Súbela para forzar la migración
        const val DB_VERSION = 6
    }

    override fun onCreate(db: SQLiteDatabase) {
        // === USUARIO ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuario(
                id_usuario       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre           TEXT    NOT NULL,
                apellido         TEXT    NOT NULL,
                correo           TEXT    NOT NULL UNIQUE,
                celular          TEXT,
                clave            TEXT    NOT NULL,
                genero           TEXT,
                acepta_terminos  INTEGER DEFAULT 0,
                foto             INTEGER NOT NULL
            )
        """.trimIndent())

        // === HOBBY (SIN fecha, CON amigos) ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hobby(
                id_hobby   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre     TEXT    NOT NULL,
                id_usuario INTEGER NOT NULL,
                amigos     INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // === CHAT ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS chat(
                id_chat     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_emisor   INTEGER NOT NULL,
                id_receptor INTEGER NOT NULL,
                mensaje     TEXT    NOT NULL,
                fecha_envio TEXT    NOT NULL
            )
        """.trimIndent())

        // === AMISTAD ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS amistad(
                id_amistad     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_solicitante INTEGER NOT NULL,
                id_receptor    INTEGER NOT NULL,
                estado         TEXT    NOT NULL
            )
        """.trimIndent())

        // === HISTORIAL (SIN fecha, CON amigos) ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hobby_historial(
                id_hist    INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                nombre     TEXT    NOT NULL,
                accion     TEXT    NOT NULL,  -- 'CREADO' | 'ELIMINADO'
                amigos     INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        // Asegurar tablas base (idempotente)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuario(
                id_usuario       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre           TEXT    NOT NULL,
                apellido         TEXT    NOT NULL,
                correo           TEXT    NOT NULL UNIQUE,
                celular          TEXT,
                clave            TEXT    NOT NULL,
                genero           TEXT,
                acepta_terminos  INTEGER DEFAULT 0,
                foto             INTEGER NOT NULL
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
                estado         TEXT    NOT NULL
            )
        """.trimIndent())

        // === MIGRAR HOBBY: quitar 'fecha' y añadir 'amigos' ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hobby_new(
                id_hobby   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre     TEXT    NOT NULL,
                id_usuario INTEGER NOT NULL,
                amigos     INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Copia datos desde la tabla vieja si existe
        try {
            db.execSQL("""
                INSERT INTO hobby_new (id_hobby, nombre, id_usuario, amigos)
                SELECT id_hobby, nombre, id_usuario,
                       CASE WHEN EXISTS(SELECT 1 FROM pragma_table_info('hobby') WHERE name='amigos')
                            THEN amigos ELSE 0 END
                FROM hobby
            """.trimIndent())
        } catch (_: Exception) { /* si no existe hobby vieja, no pasa nada */ }

        db.execSQL("DROP TABLE IF EXISTS hobby")
        db.execSQL("ALTER TABLE hobby_new RENAME TO hobby")

        // === MIGRAR HISTORIAL: quitar 'fecha' y añadir 'amigos' ===
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hobby_historial_new(
                id_hist    INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                nombre     TEXT    NOT NULL,
                accion     TEXT    NOT NULL,
                amigos     INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        try {
            db.execSQL("""
                INSERT INTO hobby_historial_new (id_hist, id_usuario, nombre, accion, amigos)
                SELECT id_hist, id_usuario, nombre, accion,
                       CASE WHEN EXISTS(SELECT 1 FROM pragma_table_info('hobby_historial') WHERE name='amigos')
                            THEN amigos ELSE 0 END
                FROM hobby_historial
            """.trimIndent())
        } catch (_: Exception) { /* si no existe tabla vieja, no pasa nada */ }

        db.execSQL("DROP TABLE IF EXISTS hobby_historial")
        db.execSQL("ALTER TABLE hobby_historial_new RENAME TO hobby_historial")
    }
}
