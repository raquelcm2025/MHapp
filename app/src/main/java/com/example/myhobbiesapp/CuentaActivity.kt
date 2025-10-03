package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Resalta el miCuenta
        bottomNav.selectedItemId = R.id.nav_cuenta

        val btnListaHobbies = findViewById<Button>(R.id.btnListaHobbies)
        btnListaHobbies.setOnClickListener {
            // Ir a ActivityListaHobbies
            startActivity(Intent(this, ListaHobbiesActivity::class.java))
            // Si no quieres que quede en back stack:
            // finish()


            // Listener para navegar entre pantallas
            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {

                    R.id.nav_inicio -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }

                    R.id.nav_explora -> {
                        startActivity(Intent(this, ExploraActivity::class.java))
                        true
                    }

                    R.id.nav_chats -> {
                        startActivity(Intent(this, ChatsActivity::class.java))
                        true
                    }

                    R.id.nav_cuenta -> {
                        true
                    }

                    else -> false
                }
            }
        }
    }
}