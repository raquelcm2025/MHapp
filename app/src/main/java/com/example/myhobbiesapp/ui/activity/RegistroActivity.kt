package com.example.myhobbiesapp.ui.activity

import android.R
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.ActivityRegistroBinding
import com.example.myhobbiesapp.entity.Usuario

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private val dominios = setOf("mh.pe", "gmail.com", "hotmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapterGenero = ArrayAdapter(
            this,
            R.layout.simple_dropdown_item_1line,
            resources.getStringArray(com.example.myhobbiesapp.R.array.generos)
        )
        binding.actvGenero.setAdapter(adapterGenero)

        binding.btnCrearCuenta.setOnClickListener {
            val nombres    = binding.tietNombres.text?.toString()?.trim().orEmpty()
            val apellidos  = binding.tietApellidos.text?.toString()?.trim().orEmpty()
            val correo     = binding.tietCorreo.text?.toString()?.trim().orEmpty()
            val clave      = binding.tietClave.text?.toString()?.trim().orEmpty()
            val confirmar  = binding.tietConfirmar.text?.toString()?.trim().orEmpty()
            val celular    = binding.tietCelular.text?.toString()?.trim().orEmpty()
            val genero     = binding.actvGenero.text?.toString()?.trim().orEmpty()
            val terminosOk = binding.swTerminos.isChecked

            // Validación de campos vacíos
            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() ||
                clave.isEmpty() || confirmar.isEmpty() || celular.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de nombres y apellidos solo letras
            val soloLetras = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            if (!soloLetras.matches(nombres)) {
                Toast.makeText(this, "Nombres inválidos: solo letras", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!soloLetras.matches(apellidos)) {
                Toast.makeText(this, "Apellidos inválidos: solo letras", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de celular (9 dígitos, empezando en 9)
            val celularRegex = Regex("^9\\d{8}$")
            if (!celularRegex.matches(celular)) {
                Toast.makeText(this, "Celular inválido: debe tener 9 dígitos y empezar con 9", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de clave mínima de 6 caracteres
            if (clave.length < 6) {
                Toast.makeText(this, "La clave debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de confirmación de clave
            if (clave != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de aceptación de términos
            if (!terminosOk) {
                Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de correo y dominio permitido
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dom = correo.substringAfter("@").lowercase()
            if (dom !in dominios) {
                Toast.makeText(this, "Dominio no permitido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Verificar si el correo ya existe
            val dao = UsuarioDAO(this)
            if (dao.getByCorreo(correo) != null) {
                Toast.makeText(this, "Ya existe una cuenta con ese correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevo = Usuario(
                nombre = nombres,
                apellido = apellidos,
                correo = correo,
                celular = celular,
                clave = clave,
                genero = if (genero.isEmpty()) null else genero,
                aceptaTerminos = terminosOk,
                foto = fotoPorGenero(genero)
            )

            val id = dao.insert(nuevo)
            if (id > 0) {
                Toast.makeText(this, "Cuenta creada 🎉", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fotoPorGenero(genero: String): Int {
        val g = genero.lowercase()
        return when {
            g.contains("fem") -> com.example.myhobbiesapp.R.drawable.ic_mujer
            g.contains("mas") -> com.example.myhobbiesapp.R.drawable.ic_hombre
            else -> com.example.myhobbiesapp.R.drawable.ic_person
        }
    }
}