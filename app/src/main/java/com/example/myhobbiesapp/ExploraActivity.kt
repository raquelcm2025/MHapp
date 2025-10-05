package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ExploraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explora)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_explora

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_explora -> {
                    true
                }

                R.id.nav_chats -> {
                    startActivity(Intent(this, ChatsActivity::class.java))
                    true
                }


                R.id.nav_cuenta -> {
                    startActivity(Intent(this, CuentaActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}