package com.example.myhobbiesapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "hobby")
data class Hobby(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    //  Nombres reales en tu tabla
    @ColumnInfo(name = "titulo")  val titulo: String = "",
    @ColumnInfo(name = "detalle") val detalle: String = ""
) {
    // Alias que usa tu UI (no tocan la BD)
    val nombre: String get() = titulo
    val descripcion: String get() = detalle
}
