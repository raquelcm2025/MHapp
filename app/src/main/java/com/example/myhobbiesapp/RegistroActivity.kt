package com.example.myhobbiesapp

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.entity.Usuarios
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton

class RegistroActivity : AppCompatActivity() {

    // dominios permitidos
    private val allowedDomains = setOf("mh.pe", "gmail.com", "hotmail.com")

    // TILs
    private lateinit var tilNombres: TextInputLayout
    private lateinit var tilApellidos: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var tilConfirmar: TextInputLayout
    private lateinit var tilCelular: TextInputLayout
    private lateinit var tilGenero: TextInputLayout

    // Inputs
    private lateinit var tietNombres: TextInputEditText
    private lateinit var tietApellidos: TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tietConfirmar: TextInputEditText
    private lateinit var tietCelular: TextInputEditText
    private lateinit var actvGenero: MaterialAutoCompleteTextView

    // Switch + botón
    private lateinit var swTerminos: MaterialSwitch
    private lateinit var btnCrear: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // 1) Bind
        tilNombres   = findViewById(R.id.tilNombres)
        tilApellidos = findViewById(R.id.tilApellidos)
        tilCorreo    = findViewById(R.id.tilCorreo)
        tilClave     = findViewById(R.id.tilClave)
        tilConfirmar = findViewById(R.id.tilConfirmar)
        tilCelular   = findViewById(R.id.tilCelular)
        tilGenero    = findViewById(R.id.tilGenero)

        tietNombres   = findViewById(R.id.tietNombres)
        tietApellidos = findViewById(R.id.tietApellidos)
        tietCorreo    = findViewById(R.id.tietCorreo)
        tietClave     = findViewById(R.id.tietClave)
        tietConfirmar = findViewById(R.id.tietConfirmar)
        tietCelular   = findViewById(R.id.tietCelular)
        actvGenero    = findViewById(R.id.actvGenero)

        swTerminos = findViewById(R.id.swTerminos)
        btnCrear   = findViewById(R.id.btnCrearCuenta)

        // 2) Género dropdown
        actvGenero.setAdapter(
            ArrayAdapter.createFromResource(
                this,
                R.array.generos,
                android.R.layout.simple_list_item_1
            )
        )

        // 3) Click crear
        btnCrear.setOnClickListener { onCrearCuenta() }
    }

    private fun onCrearCuenta() {
        clearErrors()

        val nombres   = tietNombres.text?.toString()?.trim().orEmpty()
        val apellidos = tietApellidos.text?.toString()?.trim().orEmpty()
        val correo    = tietCorreo.text?.toString()?.trim().orEmpty()
        val clave     = tietClave.text?.toString()?.trim().orEmpty()
        val conf      = tietConfirmar.text?.toString()?.trim().orEmpty()
        val celular   = tietCelular.text?.toString()?.trim().orEmpty()
        val genero    = actvGenero.text?.toString()?.trim().orEmpty()

        var ok = true

        if (nombres.isEmpty())  { tilNombres.error = "Ingresa tus nombres"; ok = false }
        if (apellidos.isEmpty()){ tilApellidos.error = "Ingresa tus apellidos"; ok = false }

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingresa tu correo"; ok = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.error = "Correo no válido"; ok = false
        } else {
            val dom = correo.substringAfterLast("@", "")
            if (dom !in allowedDomains) {
                tilCorreo.error = "Usa @mh.pe, @gmail.com o @hotmail.com"; ok = false
            }
        }

        if (clave.length < 6)   { tilClave.error = "Mínimo 6 caracteres"; ok = false }
        if (conf != clave)      { tilConfirmar.error = "No coincide"; ok = false }

        if (!celular.matches(Regex("^\\d{9}\$"))) {
            tilCelular.error = "Celular de 9 dígitos"; ok = false
        }

        if (genero.isEmpty())   { tilGenero.error = "Selecciona tu género"; ok = false }

        if (!swTerminos.isChecked) {
            Toast.makeText(this, "Debes aceptar Términos y Condiciones", Toast.LENGTH_SHORT).show()
            ok = false
        }

        if (!ok) return

        // Crear usuario (demo local)
        val usuario = Usuarios(
            nombres = nombres,
            apellidos = apellidos,
            correo = correo,
            clave = clave,
            celular = celular,
            genero = genero,
            hobbiesFavoritos = emptyList()
        )

        Toast.makeText(this, "¡Cuenta creada para ${usuario.nombres}!", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun clearErrors() {
        tilNombres.error = null
        tilApellidos.error = null
        tilCorreo.error = null
        tilClave.error = null
        tilConfirmar.error = null
        tilCelular.error = null
        tilGenero.error = null
    }
}
