package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast


class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Conecto el archivo Kotlin con activity_registro.xml
        setContentView(R.layout.activity_registro)

        //vincula cada vista con su variable correspondiente
        val etNombres = findViewById<EditText>(R.id.etNombres)
        val etApellidos = findViewById<EditText>(R.id.etApellidos)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etClave = findViewById<EditText>(R.id.etClave)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)


        btnRegistrar.setOnClickListener {
            // Obtengo lo que el usuario escribe en cada campo
            val nombres = etNombres.text.toString()
            val apellidos = etApellidos.text.toString()
            val correo = etCorreo.text.toString()
            val clave = etClave.text.toString()

            // Valido que los campos de texto obligatorios no estén vacíos
            if (nombres.isNotEmpty() && apellidos.isNotEmpty()  && correo.isNotEmpty() && clave.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Usuario ${nombres} registrado con éxito",
                    Toast.LENGTH_SHORT
                ).show()

                finish() // Cierra la actividad actual y regresa a AccesoActivity

            } else {
                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}