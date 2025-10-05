package com.example.myhobbiesapp

import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.entity.Usuarios
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class RegistroActivity : AppCompatActivity() {

    private val allowedDomains = setOf("gmail.com", "outlook.com", "myhobbies.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Insets (tu mismo código)
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

        // Views
        val etNombres = findViewById<TextInputEditText>(R.id.etNombres)
        val etApellidos = findViewById<TextInputEditText>(R.id.etApellidos)
        val cgGenero = findViewById<ChipGroup>(R.id.cgGenero)
        val etCorreo = findViewById<TextInputEditText>(R.id.etCorreo)
        val tilCorreo = findViewById<TextInputLayout>(R.id.tilCorreo) // <-- lo agregamos
        val etCelular = findViewById<TextInputEditText>(R.id.etCelular)
        val tilClave = findViewById<TextInputLayout>(R.id.tilClave)
        val etClave = findViewById<TextInputEditText>(R.id.etClave)
        val etClaveConfirmar = findViewById<TextInputEditText>(R.id.etClaveConfirmar)
        val swTerminos = findViewById<SwitchMaterial>(R.id.swTerminos)
        val btnRegistrar = findViewById<MaterialButton>(R.id.btnRegistrar)

        // Click registrar
        btnRegistrar.setOnClickListener {
            // Limpia errores previos
            tilCorreo.error = null
            tilClave.error = null

            val nombres = etNombres.text?.toString()?.trim().orEmpty()
            val apellidos = etApellidos.text?.toString()?.trim().orEmpty()
            val correo = etCorreo.text?.toString()?.trim()?.lowercase().orEmpty()
            val celular = etCelular.text?.toString()?.trim().orEmpty()
            val clave = etClave.text?.toString().orEmpty()
            val clave2 = etClaveConfirmar.text?.toString().orEmpty()
            val genero = cgGenero.checkedChipTextOrNull()

            // Validaciones mínimas
            if (nombres.isBlank()) { toast("Ingresa tus nombres"); return@setOnClickListener }
            if (apellidos.isBlank()) { toast("Ingresa tus apellidos"); return@setOnClickListener }

            // Correo: formato + dominio permitido
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Correo inválido"
                return@setOnClickListener
            }
            val domain = correo.substringAfter("@", missingDelimiterValue = "")
            if (domain !in allowedDomains) {
                tilCorreo.error = "Solo se aceptan: ${allowedDomains.joinToString(", ")}"
                return@setOnClickListener
            }

            // Celular opcional (si lo quieres obligatorio, valida aquí)
            if (celular.isNotBlank() && celular.length < 9) {
                toast("Celular debe tener 9 dígitos"); return@setOnClickListener
            }

            // Clave
            if (clave.length < 6) {
                tilClave.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }
            if (clave != clave2) {
                tilClave.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            if (!swTerminos.isChecked) {
                toast("Debes aceptar los términos y condiciones")
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

            val usuario = Usuarios(
                codigo = 0, // o genera uno
                nombres = nombres,
                apellidos = apellidos,
                correo = correo,
                clave = clave,         // (en real: guarda hash)
                celular = celular,
                genero = genero ?: "",
                fechaRegistro = fecha,
                hobbiesFavoritos = emptyList()
            )

            // TODO: guardar usuario
            toast("Cuenta creada para ${usuario.nombres}")
            // finish() // si quieres volver
        }
    }

    // Helper: obtener texto del Chip seleccionado
    private fun ChipGroup.checkedChipTextOrNull(): String? {
        val id = this.checkedChipId
        if (id == -1) return null
        val chip = findViewById<Chip>(id)
        return chip?.text?.toString()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


}
