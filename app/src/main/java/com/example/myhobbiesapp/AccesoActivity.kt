package com.example.myhobbiesapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.ui.InicioActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class AccesoActivity : AppCompatActivity() {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var tilDominio: TextInputLayout
    private lateinit var tilClave: TextInputLayout

    private lateinit var tietUsuario: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var btnInicio: Button


    private var tvRegistro: TextView? = null

    // Dominios permitidos (sin @)
    private val allowedDomains = setOf("mh.pe", "gmail.com", "hotmail.com")

    // Usuarios de prueba
    private val listaUsuarios = listOf(
        Usuario(1, "Raquel", "Callata Mamani", "raquelcm@mh.pe", "246810"),
        Usuario(2, "Martha", "Lopez Valencia", "marlova@hotmail.com", "000000"),
        Usuario(3, "Luis", "Gomez Moreno", "lgomez@gmail.com", "123456")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)

        setupViews()
        setupListeners()
        updateLoginEnabled()
    }

    private fun setupViews() {
        tilUsuario = findViewById(R.id.tilUsuario)
        tilDominio = findViewById(R.id.tilDominio) // está GONE en XML
        tilClave   = findViewById(R.id.tilClave)

        tietUsuario = findViewById(R.id.tietUsuario)
        tietClave   = findViewById(R.id.tietClave)

        btnInicio  = findViewById(R.id.btnInicio)
        tvRegistro = findViewById(R.id.tvRegistro)
    }

    private fun setupListeners() {
        btnInicio.setOnClickListener { onLoginClicked() }
        tvRegistro?.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        addClearErrorOnTyping(tilUsuario, tietUsuario)
        addClearErrorOnTyping(tilClave, tietClave)

        // Habilita/deshabilita botón al escribir
        tietUsuario.addTextChangedListener(simpleWatcher { updateLoginEnabled() })
        tietClave.addTextChangedListener(simpleWatcher { updateLoginEnabled() })
    }

    private fun simpleWatcher(onChange: () -> Unit) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onChange() }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateLoginEnabled() {
        val email = (tietUsuario.text?.toString() ?: "").trim()
        val pass  = (tietClave.text?.toString() ?: "").trim()
        btnInicio.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
    }

    private fun onLoginClicked() {
        val email = (tietUsuario.text?.toString() ?: "").trim().lowercase(Locale.ROOT)
        val clave = (tietClave.text?.toString() ?: "").trim()

        // 1) Campo vacío
        if (email.isEmpty()) {
            tilUsuario.error = "Ingresa tu correo"
            return
        } else tilUsuario.error = null

        if (clave.isEmpty()) {
            tilClave.error = "Ingresa tu contraseña"
            return
        } else tilClave.error = null

        // 2) Formato de email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilUsuario.error = "Correo no válido"
            return
        }

        // 3) Dominio permitido
        val domain = email.substringAfterLast("@", missingDelimiterValue = "")
        if (domain !in allowedDomains) {
            tilUsuario.error = "Solo se permiten @mh.pe, @gmail.com o @hotmail.com"
            Toast.makeText(this, "Dominio no permitido", Toast.LENGTH_SHORT).show()
            return
        }

        // 4) Autenticación de ejemplo
        val user = listaUsuarios.firstOrNull { it.correo.equals(email, true) && it.clave == clave }
        if (user != null) {
            Toast.makeText(this, "Bienvenid@ ${user.nombres}", Toast.LENGTH_SHORT).show()
            goTo(InicioActivity::class.java)
            //finish () // si quieres volvera a la pantalla anterior login
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }
    }

    private fun addClearErrorOnTyping(til: TextInputLayout, view: TextView) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { til.error = null }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun goTo(destino: Class<out Activity>) {
        startActivity(Intent(this, destino))
    }
}
