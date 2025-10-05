package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.adapter.HistorialAdapter
import com.example.myhobbiesapp.entity.HistorialItem

class HistorialActivity : AppCompatActivity() {
    private lateinit var rvHistorial : RecyclerView
    private lateinit var historialAdapter: HistorialAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        // Hace que el teclado del dispositivo no tape a los Views (EditText, TextInputEditText, etc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }

        rvHistorial = findViewById(R.id.rvHistorial)

        // Datos de prueba
        val items = listOf(
            HistorialItem("Karaoke", 2, "27/09/2024"),
            HistorialItem("Baile",   6, "23/09/2025"),
            HistorialItem("Cocina", 10, "25/09/2023")
        )
        // Inicializa el adaptador
        historialAdapter = HistorialAdapter(items)
        // Orientación del adaptador
        rvHistorial.layoutManager = LinearLayoutManager(this)
        // Asigna el adaptador al RecyclerView
        rvHistorial.adapter = historialAdapter
    }
}