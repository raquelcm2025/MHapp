package com.example.myhobbiesapp.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.model.DniResponse
import com.example.myhobbiesapp.data.remote.ApiClient
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.firebase.model.UserProfile

class RegistroActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var tilDni: TextInputLayout
    private lateinit var tietDni: TextInputEditText
    private lateinit var btnBuscarDni: Button
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tietNombre: TextInputEditText
    private lateinit var tilApPat: TextInputLayout
    private lateinit var tietApPat: TextInputEditText
    private lateinit var tilApMat: TextInputLayout
    private lateinit var tietApMat: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tilClave: TextInputLayout
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilConfirmar: TextInputLayout
    private lateinit var tietConfirmar: TextInputEditText
    private lateinit var tilCelular: TextInputLayout
    private lateinit var tietCelular: TextInputEditText
    private lateinit var spGenero: Spinner
    private lateinit var swTerminos: MaterialSwitch
    private lateinit var btnCrear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        bindViews()
        wireUi()
    }

    private fun bindViews() {
        tilDni = findViewById(R.id.tilDNI)
        tietDni = findViewById(R.id.tietDNI)
        btnBuscarDni = findViewById(R.id.btnBuscarDNI)
        tilNombre = findViewById(R.id.tilNombre)
        tietNombre = findViewById(R.id.tietNombre)
        tilApPat = findViewById(R.id.tilApellidoPaterno)
        tietApPat = findViewById(R.id.tietApellidoPaterno)
        tilApMat = findViewById(R.id.tilApellidoMaterno)
        tietApMat = findViewById(R.id.tietApellidoMaterno)
        tilCorreo = findViewById(R.id.tilCorreo)
        tietCorreo = findViewById(R.id.tietCorreo)
        tilClave = findViewById(R.id.tilClave)
        tietClave = findViewById(R.id.tietClave)
        tilConfirmar = findViewById(R.id.tilConfirmar)
        tietConfirmar = findViewById(R.id.tietConfirmar)
        tilCelular = findViewById(R.id.tilCelular)
        tietCelular = findViewById(R.id.tietCelular)
        spGenero = findViewById(R.id.spGenero)
        swTerminos = findViewById(R.id.swTerminos)
        btnCrear = findViewById(R.id.btnCrearCuenta)
    }

    private fun wireUi() {
        btnBuscarDni.setOnClickListener {
            val dni = tietDni.text?.toString()?.trim().orEmpty()
            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                tilDni.error = "DNI debe tener 8 dígitos"
                return@setOnClickListener
            }
            tilDni.error = null

            btnBuscarDni.isEnabled = false
            btnBuscarDni.text = "Buscando…"

            lifecycleScope.launch {
                if (!hayInternet()) {
                    habilitarEdicionManual()
                    toast("Sin conexión. Completa tus datos manualmente.")
                    btnBuscarDni.isEnabled = true
                    btnBuscarDni.text = "Buscar"
                    return@launch
                }

                val info = consultarDniRemoto(dni)
                if (info != null) {
                    completarNombre(info)
                    toast("DNI encontrado y autocompletado")
                } else {
                    habilitarEdicionManual()
                    toast("No encontrado. Completa tus datos manualmente.")
                }

                btnBuscarDni.isEnabled = true
                btnBuscarDni.text = "Buscar"
            }
        }

        btnCrear.setOnClickListener { crearCuenta() }
    }


    private fun crearCuenta() {
        clearErrors()

        val dni = tietDni.text?.toString()?.trim().orEmpty()
        val nombre = tietNombre.text?.toString()?.trim().orEmpty()
        val apPat = tietApPat.text?.toString()?.trim().orEmpty()
        val apMat = tietApMat.text?.toString()?.trim().orEmpty()
        val correo = tietCorreo.text?.toString()?.trim().orEmpty()
        val clave = tietClave.text?.toString()?.trim().orEmpty()
        val confirmar = tietConfirmar.text?.toString()?.trim().orEmpty()
        val celular = tietCelular.text?.toString()?.trim().orEmpty()
        val genero = spGenero.selectedItem?.toString()?.trim().orEmpty()
        val acepta = swTerminos.isChecked

        var ok = true
        if (dni.isNotEmpty() && (dni.length != 8 || !dni.all { it.isDigit() })) {
            tilDni.error = "DNI inválido (8 dígitos)"; ok = false
        }
        if (correo.isBlank()) { tilCorreo.error = "Ingresa tu correo"; ok = false }
        if (clave.isBlank()) { tilClave.error = "Ingresa una contraseña"; ok = false }
        else if (clave.length < 6) { tilClave.error = "Mínimo 6 caracteres"; ok = false }
        if (confirmar.isBlank() || confirmar != clave) { tilConfirmar.error = "Las contraseñas no coinciden"; ok = false }
        if (celular.isNotEmpty() && (celular.length != 9 || !celular.all { it.isDigit() })) { tilCelular.error = "Celular de 9 dígitos"; ok = false }
        if (nombre.isBlank() || apPat.isBlank() || apMat.isBlank()) {
            toast("Completa tu nombre y apellidos"); ok = false
        }
        if (!acepta) { toast("Debes aceptar términos y condiciones"); ok = false }
        if (!ok) return

        setLoading(true)

        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    toast(task.exception?.localizedMessage ?: "No se pudo crear la cuenta")
                    setLoading(false) // Desbloquear si falla
                    return@addOnCompleteListener
                }

                val uid = task.result?.user?.uid
                if (uid == null) {
                    toast("Error crítico: no se pudo obtener UID. Intenta de nuevo.")
                    setLoading(false)
                    return@addOnCompleteListener
                }

                val perfil = UserProfile(
                    nombre = nombre,
                    apellidoPaterno = apPat,
                    apellidoMaterno = apMat,
                    correo = correo,
                    celular = celular,
                    genero = mapGenero(genero) ?: "otro",
                    avatar = mapGenero(genero) ?: "otro",
                    aceptaTerminos = acepta,
                    hobbies = emptyMap() // Empezamos sin hobbies
                )

                FirebaseDb.saveUserProfileAndIndex(uid, perfil) { ok, ex ->
                    if (!ok) {

                        toast("Advertencia: no se pudo guardar tu perfil: ${ex?.message}")
                    }
                }

                FirebaseDb.upsertEmailIndex(correo, uid)
                SessionManager.setCurrentEmail(this, correo)
                toast("Cuenta creada 🎉 (Firebase)")
                startActivity(
                    Intent(this, InicioActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }
    }

    private suspend fun consultarDniRemoto(dni: String): DniInfo? =
        withContext(Dispatchers.IO) {
            try {
                val resp = ApiClient.service.buscarDni(dni)
                if (!resp.isSuccessful) return@withContext null
                val body: DniResponse = resp.body() ?: return@withContext null
                DniInfo(
                    nombres = body.nombres.trim(),
                    apellidoPaterno = body.apellidoPaterno.trim(),
                    apellidoMaterno = body.apellidoMaterno.trim()
                )
            } catch (_: Exception) {
                null
            }
        }

    private fun setLoading(isLoading: Boolean) {
        btnCrear.isEnabled = !isLoading
        btnBuscarDni.isEnabled = !isLoading
        tietDni.isEnabled = !isLoading
        tietNombre.isEnabled = !isLoading
    }

    private fun completarNombre(info: DniInfo) {
        tietNombre.setText(info.nombres)
        tietApPat.setText(info.apellidoPaterno)
        tietApMat.setText(info.apellidoMaterno)
        tietNombre.isEnabled = false
        tietApPat.isEnabled = false
        tietApMat.isEnabled = false
    }

    private fun habilitarEdicionManual() {
        tietNombre.isEnabled = true
        tietApPat.isEnabled = true
        tietApMat.isEnabled = true
    }

    private fun mapGenero(valor: String): String? {
        val v = valor.lowercase()
        return when {
            v.contains("fem") -> "femenino"
            v.contains("mas") -> "masculino"
            v.contains("otro") -> "otro"
            else -> null
        }
    }

    private fun clearErrors() {
        tilDni.error = null
        tilNombre.error = null
        tilApPat.error = null
        tilApMat.error = null
        tilCorreo.error = null
        tilClave.error = null
        tilConfirmar.error = null
        tilCelular.error = null
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun hayInternet(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    data class DniInfo(
        val nombres: String,
        val apellidoPaterno: String,
        val apellidoMaterno: String
    )
}