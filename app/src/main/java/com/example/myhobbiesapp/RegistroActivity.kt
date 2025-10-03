package com.example.myhobbiesapp

import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import android.text.Editable
import android.text.TextWatcher

import android.text.InputFilter
import android.text.method.DigitsKeyListener

import android.view.View

import com.google.android.material.switchmaterial.SwitchMaterial


class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombres = findViewById<TextInputEditText>(R.id.etNombres)
        val etApellidos = findViewById<TextInputEditText>(R.id.etApellidos)
        val cgGenero = findViewById<ChipGroup>(R.id.cgGenero)

        val etCorreo = findViewById<TextInputEditText>(R.id.etCorreo)
        val etCelular = findViewById<TextInputEditText?>(R.id.etCelular)

        etCelular?.apply {
            keyListener = DigitsKeyListener.getInstance("0123456789")
            filters = arrayOf(InputFilter.LengthFilter(9))
        }

        val tilClave = findViewById<TextInputLayout>(R.id.tilClave)
        val etClave = findViewById<TextInputEditText>(R.id.etClave)

        val tilClaveConfirmar = findViewById<TextInputLayout>(R.id.tilClaveConfirmar)
        val etClaveConfirmar = findViewById<TextInputEditText>(R.id.etClaveConfirmar)

        val swTerminos = findViewById<SwitchMaterial>(R.id.swTerminos)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val fabVolver = findViewById<View>(R.id.fabVolver)

        // --- Limpia errores mientras se escribe ---
        listOf(etNombres, etApellidos, etCorreo, etCelular, etClave, etClaveConfirmar).forEach { et ->
            et?.addTextChangedListener(SimpleTextWatcher {
                setFieldError(et, null) // quitar error
            })
        }

        fabVolver.setOnClickListener { finish() }

        btnRegistrar.setOnClickListener {
            // 1) Leer valores
            val nombres = etNombres.text?.toString()?.trim().orEmpty()
            val apellidos = etApellidos.text?.toString()?.trim().orEmpty()
            val correo = etCorreo.text?.toString()?.trim().orEmpty()
            val celular = etCelular?.text?.toString()?.trim().orEmpty()
            val clave = etClave.text?.toString().orEmpty()
            val clave2 = etClaveConfirmar.text?.toString().orEmpty()
            val generoSeleccionado = getGeneroSeleccionado(cgGenero)

            // 2) Validaciones en orden, enfocando el primer error
            when {
                nombres.isEmpty() -> {
                    setFieldError(etNombres, "Ingresa tus nombres")
                    etNombres.requestFocus()
                }
                apellidos.isEmpty() -> {
                    setFieldError(etApellidos, "Ingresa tus apellidos")
                    etApellidos.requestFocus()
                }
                generoSeleccionado == null -> {
                    Toast.makeText(this, "Selecciona tu género", Toast.LENGTH_SHORT).show()
                }
                correo.isEmpty() -> {
                    setFieldError(etCorreo, "Ingresa tu correo")
                    etCorreo.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                    setFieldError(etCorreo, "Correo no válido")
                    etCorreo.requestFocus()
                }
                // Celular es opcional: solo valida si no está vacío
                celular.isNotEmpty() && !isValidPhone9(celular) -> {
                    setFieldError(etCelular, "Celular no válido")
                    etCelular?.requestFocus()
                }
                clave.length < 8 -> {
                    tilClave.error = "Usa al menos 8 caracteres"
                    etClave.requestFocus()
                }
                clave2.isEmpty() -> {
                    tilClaveConfirmar.error = "Repite tu contraseña"
                    etClaveConfirmar.requestFocus()
                }
                clave != clave2 -> {
                    tilClaveConfirmar.error = "Las contraseñas no coinciden"
                    etClaveConfirmar.requestFocus()
                }
                swTerminos.isChecked.not() -> {
                    Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    tilClave.error = null
                    tilClaveConfirmar.error = null

                    Toast.makeText(
                        this,
                        "Usuario $nombres registrado con éxito",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }
        }
    }


    /** Coloca error en el TextInputLayout contenedor si existe; si no, en el EditText. */
    private fun setFieldError(editText: TextInputEditText?, errorMsg: String?) {
        if (editText == null) return
        val til = (editText.parent?.parent) as? TextInputLayout
        if (til != null) {
            til.error = errorMsg
        } else {
            editText.error = errorMsg
        }
    }

    /** Devuelve el texto del chip seleccionado o null si nada elegido. */
    private fun getGeneroSeleccionado(group: ChipGroup): String? {
        val id = group.checkedChipId
        if (id == View.NO_ID) return null
        val chip = group.findViewById<com.google.android.material.chip.Chip>(id)
        return chip?.text?.toString()
    }

    /** Valida un teléfono **/
    private fun isValidPhone9(phone: String): Boolean =
        Regex("^\\d{9}$").matches(phone)


    class SimpleTextWatcher(private val onChanged: (String) -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { onChanged(s?.toString().orEmpty()) }
    }

}
