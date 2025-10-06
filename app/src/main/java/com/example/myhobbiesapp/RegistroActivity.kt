package com.example.myhobbiesapp

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.core.HobbiesStore
import com.example.myhobbiesapp.core.UserStore
import com.example.myhobbiesapp.databinding.ActivityRegistroBinding
import com.example.myhobbiesapp.entity.Usuario

class RegistroActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegistroBinding
    private val allowedDomains = setOf("mh.pe","gmail.com","hotmail.com")

    private val seleccion = mutableSetOf<String>() // hobbies seleccionados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Género
        b.actvGenero.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.generos))
        )

        // Elegir hobbies
        b.btnElegirHobbies.setOnClickListener { showHobbiesDialog() }

        // Crear
        b.btnCrearCuenta.setOnClickListener { onCrear() }
    }

    private fun showHobbiesDialog() {
        val all = HobbiesStore.todos
        val checked = all.map { it in seleccion }.toBooleanArray()

        AlertDialog.Builder(this)
            .setTitle("Elige tus hobbies")
            .setMultiChoiceItems(all.toTypedArray(), checked) { _, which, isChecked ->
                val h = all[which]
                if (isChecked) seleccion.add(h) else seleccion.remove(h)
            }
            .setPositiveButton("Aceptar") { d, _ ->
                b.tvHobbiesSeleccionados.text =
                    if (seleccion.isEmpty()) "Sin hobbies"
                    else seleccion.joinToString(", ")
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ ->
                d.dismiss()
            }
            .setNeutralButton("Limpiar") { d, _ ->
                seleccion.clear()
                b.tvHobbiesSeleccionados.text = "Sin hobbies"
                d.dismiss()
            }
            .show()
    }

    private fun onCrear() {
        clearErrors()

        val nombres = b.tietNombres.text?.toString()?.trim().orEmpty()
        val apellidos = b.tietApellidos.text?.toString()?.trim().orEmpty()
        val correo = b.tietCorreo.text?.toString()?.trim().orEmpty()
        val clave = b.tietClave.text?.toString()?.trim().orEmpty()
        val conf = b.tietConfirmar.text?.toString()?.trim().orEmpty()
        val celular = b.tietCelular.text?.toString()?.trim().orEmpty()
        val genero = b.actvGenero.text?.toString()?.trim().orEmpty()

        var ok = true
        if (nombres.isEmpty()) { b.tilNombres.error = "Ingresa tus nombres"; ok = false }
        if (apellidos.isEmpty()) { b.tilApellidos.error = "Ingresa tus apellidos"; ok = false }

        if (correo.isEmpty()) { b.tilCorreo.error = "Ingresa tu correo"; ok = false }
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            b.tilCorreo.error = "Correo no válido"; ok = false
        } else {
            val dom = correo.substringAfterLast("@","")
            if (dom !in allowedDomains) { b.tilCorreo.error = "Usa @mh.pe, @gmail.com o @hotmail.com"; ok = false }
        }

        if (clave.length < 6) { b.tilClave.error = "Mínimo 6 caracteres"; ok = false }
        if (conf != clave) { b.tilConfirmar.error = "No coincide"; ok = false }

        if (!celular.matches(Regex("^\\d{9}\$"))) { b.tilCelular.error = "9 dígitos"; ok = false }
        if (genero.isEmpty()) { b.tilGenero.error = "Selecciona tu género"; ok = false }

        if (!b.swTerminos.isChecked) {
            Toast.makeText(this, "Acepta los Términos y Condiciones", Toast.LENGTH_SHORT).show()
            ok = false
        }

        if (!ok) return

        val user = Usuario(
            nombres = nombres,
            apellidos = apellidos,
            correo = correo,
            clave = clave,
            celular = celular,
            genero = genero,
            hobbies = seleccion.toList()
        )
        UserStore.saveUser(this, user)
        UserStore.setLogged(this, user.correo)

        AlertDialog.Builder(this)
            .setTitle("¡Cuenta creada!")
            .setMessage("Bienvenid@ ${user.nombres} 🎉\nHobbies: ${if (user.hobbies.isEmpty()) "ninguno" else user.hobbies.joinToString(", ")}")
            .setPositiveButton("Ir a iniciar sesión") { _, _ ->
                finish() // volvemos al AccesoActivity
            }
            .show()
    }

    private fun clearErrors() {
        b.tilNombres.error = null
        b.tilApellidos.error = null
        b.tilCorreo.error = null
        b.tilClave.error = null
        b.tilConfirmar.error = null
        b.tilCelular.error = null
        b.tilGenero.error = null
    }
}
