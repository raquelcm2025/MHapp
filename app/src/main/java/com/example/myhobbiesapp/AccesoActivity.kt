package com.example.myhobbiesapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class AccesoActivity : AppCompatActivity() {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var tilDominio: TextInputLayout
    private lateinit var tilClave: TextInputLayout

    private lateinit var tietUsuario: TextInputEditText          // parte antes del @ (o correo completo)
    private lateinit var actvDominio: AutoCompleteTextView       // dropdown dominios
    private lateinit var tietClave: TextInputEditText

    private lateinit var btnInicio: Button
    private var tvRegistro: TextView? = null

    // Usuarios de prueba
    private val listaUsuarios = listOf(
        Usuario(1, "Raquel", "Callata Mamani", "raquelcm@mh.pe", "246810"),
        Usuario(2, "Martha", "Lopez Vargas",  "marlova@hotmail.com", "000000"),
        Usuario(3, "Luis", "Gomez Moreno",    "lgomez@gmail.com",    "123456")
    )

    // Dominios disponibles
    private val dominiosDisponibles = listOf("@gmail.com", "@hotmail.com", "@mh.pe")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)

        setupViews()
        setupDomainDropdown()
        setupListeners()
        updateLoginEnabled()
    }

    // ------------------- CONFIGURACIONES -------------------

    private fun setupViews() {
        tilUsuario  = findViewById(R.id.tilUsuario)
        tilDominio  = findViewById(R.id.tilDominio)
        tilClave    = findViewById(R.id.tilClave)

        tietUsuario = findViewById(R.id.tietUsuario)
        actvDominio = findViewById(R.id.actvDominio)
        tietClave   = findViewById(R.id.tietClave)

        btnInicio   = findViewById(R.id.btnInicio)
        tvRegistro  = findViewById(R.id.tvRegistro)
    }

    /** lista despegable de dominios */
    private fun setupDomainDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // si quieres estilo Material: com.google.android.material.R.layout.material_simple_list_item_1
            dominiosDisponibles
        )
        actvDominio.setAdapter(adapter)
        actvDominio.setText(dominiosDisponibles.first(), false) // dominio por defecto
    }

    /** Listeners para botones, limpieza de errores y UX */
    private fun setupListeners() {
        btnInicio.setOnClickListener { onLoginClicked() }
        tvRegistro?.setOnClickListener { goTo(RegistroActivity::class.java) }

        // Limpiar errores al escribir
        addClearErrorOnTyping(tilUsuario, tietUsuario)
        addClearErrorOnTyping(tilClave, tietClave)
        actvDominio.setOnItemClickListener { _, _, _, _ ->
            tilDominio.error = null
            updateLoginEnabled()
        }

        // Si el usuario tipea un correo completo, sincroniza el dropdown
        tietUsuario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tilUsuario.error = null
                // Si contiene @, intenta detectar dominio
                val text = s?.toString().orEmpty()
                if (text.contains("@")) {
                    val dom = "@" + text.substringAfterLast("@")
                        .trim()
                        .lowercase(Locale.ROOT)
                    val match = dominiosDisponibles.firstOrNull { it.equals(dom, ignoreCase = true) }
                    if (match != null) {
                        // Ajusta el dropdown al dominio detectado
                        actvDominio.setText(match, false)
                        tilDominio.error = null
                    }
                }
                updateLoginEnabled()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Acción IME en contraseña (Enter = iniciar sesión)
        tietClave.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                onLoginClicked()
                true
            } else false
        }

        // Habilita/deshabilita botón al escribir contraseña
        tietClave.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tilClave.error = null
                updateLoginEnabled()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /** Limpia el error de un TIL cuando cambia el texto */
    private fun addClearErrorOnTyping(til: TextInputLayout, view: TextView) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.error = null
                updateLoginEnabled()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateLoginEnabled() {
        val usuarioParte = tietUsuario.text?.toString()?.trim().orEmpty()
        val dominioSel   = actvDominio.text?.toString()?.trim().orEmpty()
        val pass         = tietClave.text?.toString()?.trim().orEmpty()

        val wroteFullEmail = usuarioParte.contains("@")
        val basicOk = usuarioParte.isNotEmpty() && pass.isNotEmpty()
        val domainOk = wroteFullEmail || dominioSel.isNotEmpty()

        btnInicio.isEnabled = basicOk && domainOk
    }

    // -------------------  LOGIN -------------------

    private fun onLoginClicked() {
        val usuarioParte = tietUsuario.text?.toString()?.trim().orEmpty()
        val dominio      = actvDominio.text?.toString()?.trim().orEmpty()
        val clave        = tietClave.text?.toString()?.trim().orEmpty()

        if (!validateInputs(usuarioParte, dominio, clave)) return

        val correoCompleto = buildEmail(usuarioParte, dominio)

        if (!Patterns.EMAIL_ADDRESS.matcher(correoCompleto).matches()) {
            tilUsuario.error = "Correo no válido"
            return
        }

        val usuarioEncontrado = findUser(correoCompleto, clave)

        if (usuarioEncontrado != null) {
            showWelcomeDialog(usuarioEncontrado.nombres)
        } else {
            showLoginError()
        }
    }

    /** Validación de campos obligatorios */
    private fun validateInputs(usuarioParte: String, dominio: String, clave: String): Boolean {
        var ok = true

        if (usuarioParte.isEmpty()) {
            tilUsuario.error = "Ingresa tu correo"
            ok = false
        } else tilUsuario.error = null

        val escribioEmailCompleto = usuarioParte.contains("@")
        if (!escribioEmailCompleto) {
            if (dominio.isEmpty()) {
                tilDominio.error = "Elige un dominio"
                ok = false
            } else tilDominio.error = null
        } else tilDominio.error = null

        if (clave.isEmpty()) {
            tilClave.error = "Ingresa tu contraseña"
            ok = false
        } else tilClave.error = null

        return ok
    }

    /** Crea el email según lo ingresado y lista desplegable */
    private fun buildEmail(usuarioParte: String, dominio: String): String {
        val user = usuarioParte.trim()
        if (user.contains("@")) return user.lowercase(Locale.ROOT)

        val dom = sanitizeDomain(dominio)
        return (user + dom).lowercase(Locale.ROOT)
    }

    /** Normaliza el dominio para asegurar el @ inicial */
    private fun sanitizeDomain(raw: String): String {
        val d = raw.trim()
        if (d.isEmpty()) return ""
        return if (d.startsWith("@")) d else "@$d"
    }

    /** Busca en la lista local */
    private fun findUser(correo: String, clave: String): Usuario? {
        return listaUsuarios.firstOrNull {
            it.correo.equals(correo, ignoreCase = true) && it.clave == clave
        }
    }


    private fun showWelcomeDialog(nombre: String) {
        AlertDialog.Builder(this)
            .setTitle("¡Bienvenid@!")
            .setMessage("Hola $nombre, nos alegra verte en MyHobbiesApp 🎉")
            .setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
                goTo(MainActivity::class.java)
            }
            .show()
    }

    private fun showLoginError() {
        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
    }

    private fun goTo(destino: Class<out Activity>) {
        startActivity(Intent(this, destino))
    }
}
