package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.data.UsuarioDAO
import com.example.myhobbiesapp.entity.Usuario
import com.example.myhobbiesapp.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private val dominios = setOf("mh.pe", "gmail.com", "hotmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapterGenero = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.generos) // asegúrate de tener este array
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

            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() ||
                clave.isEmpty() || confirmar.isEmpty() || celular.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (clave != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!terminosOk) {
                Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dom = correo.substringAfter("@").lowercase()
            if (dom !in dominios) {
                Toast.makeText(this, "Dominio no permitido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

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
                finish() // vuelve al login
            } else {
                Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fotoPorGenero(genero: String): Int {
        val g = genero.lowercase()
        return when {
            g.contains("fem") -> R.drawable.ic_mujer
            g.contains("mas") -> R.drawable.ic_hombre
            else -> R.drawable.ic_person
        }
    }
}
