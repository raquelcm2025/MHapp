package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.data.entity.Usuario
import com.example.myhobbiesapp.data.model.DniResponse
import com.example.myhobbiesapp.data.remote.ApiClient
import com.example.myhobbiesapp.databinding.ActivityRegistroBinding
import com.example.myhobbiesapp.util.SecurityUtils
import com.example.myhobbiesapp.util.SessionManager
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBuscarDNI.setOnClickListener {
            val dni = binding.tietDNI.text?.toString()?.trim().orEmpty()
            if (!dni.matches(Regex("\\d{8}"))) {
                binding.tietDNI.error = "DNI de 8 dígitos"
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val resp = ApiClient.service.buscarDni(dni)
                    if (resp.isSuccessful) {
                        val d: DniResponse? = resp.body()
                        if (d != null) {
                            binding.tietNombre.setText(d.nombres)
                            binding.tietApellidoPaterno.setText(d.apellidoPaterno)
                            binding.tietApellidoMaterno.setText(d.apellidoMaterno)
                        } else {
                            Toast.makeText(this@RegistroActivity, "Sin datos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegistroActivity, "No encontrado", Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception) {
                    Toast.makeText(this@RegistroActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCrearCuenta.setOnClickListener {
            clearErrors()

            val dni = binding.tietDNI.text?.toString()?.trim().orEmpty()
            val nombres = binding.tietNombre.text?.toString()?.trim().orEmpty()
            val apPat = binding.tietApellidoPaterno.text?.toString()?.trim().orEmpty()
            val apMat = binding.tietApellidoMaterno.text?.toString()?.trim().orEmpty()
            val correo = binding.tietCorreo.text?.toString()?.trim().orEmpty()
            val celular = binding.tietCelular.text?.toString()?.trim().orEmpty()
            val clave = binding.tietClave.text?.toString()?.trim().orEmpty()
            val claveConf = binding.tietConfirmar.text?.toString()?.trim().orEmpty()
            val generoSel = binding.spGenero.selectedItem?.toString()?.trim().orEmpty()
            val acepta = binding.swTerminos.isChecked

            var ok = true
            if (!dni.matches(Regex("\\d{8}"))) { binding.tietDNI.error = "DNI inválido"; ok = false }
            if (nombres.isEmpty()) { binding.tietNombre.error = "Requerido"; ok = false }
            if (apPat.isEmpty()) { binding.tietApellidoPaterno.error = "Requerido"; ok = false }
            if (apMat.isEmpty()) { binding.tietApellidoMaterno.error = "Requerido"; ok = false }
            if (!isCorreoPermitido(correo)) { binding.tietCorreo.error = "Solo @mh.pe, @hotmail.com, @gmail.com"; ok = false }
            if (celular.isEmpty()) { binding.tietCelular.error = "Requerido"; ok = false }
            if (clave.length < 6) { binding.tietClave.error = "Mínimo 6 caracteres"; ok = false }
            if (claveConf != clave) { binding.tietConfirmar.error = "No coincide"; ok = false }
            if (generoSel.isEmpty()) { Toast.makeText(this, "Selecciona género", Toast.LENGTH_SHORT).show(); ok = false }
            if (!acepta) { Toast.makeText(this, "Debes aceptar Términos y Condiciones", Toast.LENGTH_SHORT).show(); ok = false }

            if (!ok) return@setOnClickListener

            val claveHash = SecurityUtils.sha256(clave)
            val fotoRes = when (generoSel.lowercase()) {
                "femenino", "mujer" -> R.drawable.ic_mujer
                "masculino", "hombre" -> R.drawable.ic_hombre
                else -> R.drawable.ic_person
            }

            val usuario = Usuario(
                id = 0,
                nombre = nombres,
                apellidoPaterno = apPat,
                apellidoMaterno = apMat,
                correo = correo,
                celular = celular,
                claveHash = claveHash,
                genero = generoSel,
                aceptaTerminos = acepta,
                foto = fotoRes
            )

            val filas = UsuarioDAO(this).insert(usuario)
            if (filas > 0) {
                Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show()
                // guardar la sesión antes de inicio
                SessionManager.saveCurrentEmail(this, correo)
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isCorreoPermitido(correo: String): Boolean {
        if (!correo.contains("@")) return false
        val dominios = listOf("@mh.pe", "@hotmail.com", "@gmail.com")
        val okDom = dominios.any { correo.endsWith(it, ignoreCase = true) }
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return okDom && regex.matches(correo)
    }

    private fun clearErrors() {
        binding.tietDNI.error = null
        binding.tietNombre.error = null
        binding.tietApellidoPaterno.error = null
        binding.tietApellidoMaterno.error = null
        binding.tietCorreo.error = null
        binding.tietCelular.error = null
        binding.tietClave.error = null
        binding.tietConfirmar.error = null
    }
}
