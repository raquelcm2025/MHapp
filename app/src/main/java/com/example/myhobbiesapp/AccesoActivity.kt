package com.example.myhobbiesapp

import android.app.Activity
import android.content.Intent // para cambiar de pantalla
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast // mostrar mensajes
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.ListaHobbiesActivity
import com.example.myhobbiesapp.RegistroActivity
import com.example.myhobbiesapp.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AccesoActivity : AppCompatActivity() {

    /* ----- Declaración de variables que necesito en esta pantalla ----- */

    private var tvRegistro: TextView? = null   // "?" indica que puede ser nulo
    private lateinit var tietCorreo: TextInputEditText  // lateinit: se inicializa más tarde
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout // mostrar errores debao del input
    private lateinit var tilClave: TextInputLayout
    private lateinit var btnAcceso: Button

    /* Lista temporal de usuarios registrados en memoria */
    private val listaUsuarios = mutableListOf(
        Usuario(1, "Raquel", "Callata", "raquel@mh.pe", "12345"),
        Usuario(2, "Prueba", "Cibertec", "prueba@mh.pe", "00000")
    )

    //se ejecuta al crear pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Conecto el archivo Kotlin con activity_acceso.xml
        setContentView(R.layout.activity_acceso)

        //vincula cada vista con su variable correspondiente
        tvRegistro  = findViewById(R.id.tvRegistro)
        tietCorreo  = findViewById(R.id.tietCorreo)
        tietClave   = findViewById(R.id.tietClave)
        tilCorreo   = findViewById(R.id.tilCorreo)
        tilClave    = findViewById(R.id.tilClave)
        btnAcceso   = findViewById(R.id.btnInicio)

        //vincula evento click al boton de ACCESO - Iniciar sesión
        btnAcceso.setOnClickListener {
            validarCampos()
        }
        //boton de Registrarse lo lleva a otra pantalla
        tvRegistro?.setOnClickListener {
            cambioActivity(RegistroActivity::class.java)
        }
    }

    private fun validarCampos() {
        //Obtener texto y quitar espacios en blanco
        val correo = tietCorreo.text?.toString()?.trim().orEmpty()
        val clave  = tietClave.text?.toString()?.trim().orEmpty()
        var hayError = false

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo" //muestra el error debajo del input
            hayError = true
        } else {
            tilCorreo.error = null
        }

        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            hayError = true
        } else {
            tilClave.error = null
        }

        if (hayError) return  // si hay error, no continua con la validación

        //buscar usuario en la lista con dicho correo y clave
        var usuarioEncontrado: Usuario? = null
        for (u in listaUsuarios) {
            //comparar correo y clave
            if (u.correo.equals(correo, ignoreCase = true) && u.clave == clave) {
                usuarioEncontrado = u
                break
            }
        }

        //si se encontró usuario, muestra msje de bienvenida y pasa a la lista de hobbies
        if (usuarioEncontrado != null) {
            Toast.makeText(this,
                "Bienvenida/o ${usuarioEncontrado.nombres}",
                Toast.LENGTH_LONG).show()

            startActivity(Intent(this, ListaHobbiesActivity::class.java))
            // finish() // evita volver a login al dar clic Atrás
        } else {
            //si no existe usuario, mostrar msje de error
            Toast.makeText(this,
                "Correo o contraseña incorrectos",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun cambioActivity(activityDestino: Class<out Activity>) {
        startActivity(Intent(this, activityDestino))
    }
}
