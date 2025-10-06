package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.core.UserStore
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
import com.example.myhobbiesapp.ui.InicioActivity

class AccesoActivity : AppCompatActivity() {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var tietUsuario: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var btnInicio: Button
    private lateinit var tvRegistro: TextView

    private val allowedDomains = setOf("mh.pe","gmail.com","hotmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)

        //  demo si no hay usuarios
        UserStore.ensureSeed(this)

        tilUsuario = findViewById(R.id.tilUsuario)
        tilClave   = findViewById(R.id.tilClave)
        tietUsuario = findViewById(R.id.tietUsuario)
        tietClave   = findViewById(R.id.tietClave)
        btnInicio   = findViewById(R.id.btnInicio)
        tvRegistro  = findViewById(R.id.tvRegistro)

        tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        btnInicio.setOnClickListener { onLoginClicked() }
    }

    private fun onLoginClicked() {
        val email = (tietUsuario.text?.toString() ?: "").trim().lowercase(Locale.ROOT)
        val pass  = (tietClave.text?.toString() ?: "").trim()

        if (email.isEmpty()) { tilUsuario.error = "Ingresa tu correo"; return }
        tilUsuario.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilUsuario.error = "Correo no válido"; return
        }
        val domain = email.substringAfterLast("@", "")
        if (domain !in allowedDomains) {
            tilUsuario.error = "Usa @mh.pe, @gmail.com o @hotmail.com"; return
        }
        if (pass.isEmpty()) { tilClave.error = "Ingresa tu contraseña"; return }
        tilClave.error = null

        val user = UserStore.authenticate(this, email, pass)
        if (user == null) {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
            return
        }

        UserStore.setLogged(this, user.correo)
        startActivity(Intent(this, InicioActivity::class.java).apply {
            putExtra("nombreUsuario", user.nombres)
        })
        finish()
    }
}
