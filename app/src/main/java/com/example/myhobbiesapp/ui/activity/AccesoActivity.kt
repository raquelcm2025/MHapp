package com.example.myhobbiesapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.R

import com.example.myhobbiesapp.databinding.ActivityAccesoBinding
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.firebase.model.UserProfile
// ------------------------------

class AccesoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccesoBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleClient: GoogleSignInClient


    /** ---------- Google launcher ---------- **/
    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val cred = GoogleAuthProvider.getCredential(account.idToken, null)
            setLoading(true)

            val email = account.email ?: ""
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { methodsTask ->
                val isNewUser = methodsTask.result?.signInMethods?.isEmpty() ?: false

                auth.signInWithCredential(cred).addOnCompleteListener { t ->
                    setLoading(false)
                    if (!t.isSuccessful) {
                        toast(t.exception?.localizedMessage ?: "Error al iniciar con Google")
                        return@addOnCompleteListener
                    }
                    onLoginSuccess(isNewUser)
                }
            }

        } catch (e: ApiException) {
            setLoading(false)
            toast(e.localizedMessage ?: "Inicio con Google cancelado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccesoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInicio.setOnClickListener { doLoginFirebase() }
        binding.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
        binding.tvOlvidarClave.setOnClickListener {
            startActivity(Intent(this, RestablecerClaveActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)
        binding.btnGoogle.setOnClickListener {
            googleLauncher.launch(googleClient.signInIntent)        }
    }

    private fun doLoginFirebase() {
        clearErrors()
        val correo = binding.tietUsuario.text?.toString()?.trim().orEmpty()
        val clave  = binding.tietClave.text?.toString()?.trim().orEmpty()

        var ok = true
        if (correo.isEmpty()) {
            binding.tilUsuario.error = "Correo inválido"
            ok = false
        }
        if (clave.isEmpty()) {
            binding.tilClave.error = "Ingresa tu clave"
            ok = false
        }
        if (!ok) return

        setLoading(true)
        auth.signInWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        FirebaseDb.upsertEmailIndex(correo, uid)
                    }
                    onLoginSuccess(isNewUser = false)
                } else {
                    val e = task.exception
                    when (e) {
                        is FirebaseAuthInvalidUserException -> {
                            binding.tilUsuario.error = "No existe una cuenta con este correo"
                            toast("Usuario no registrado")
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            if (e.errorCode == "ERROR_INVALID_EMAIL") {
                                binding.tilUsuario.error = "Formato de correo inválido"
                            } else {
                                binding.tilClave.error = "Contraseña incorrecta"
                            }
                            toast("Credenciales incorrectas")
                        }
                        else -> toast("Error: ${e?.localizedMessage ?: "desconocido"}")
                    }
                }
            }
    }


    private fun onLoginSuccess(isNewUser: Boolean) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        val uid = user.uid

        // Guardar sesión local
        SessionManager.setCurrentEmail(this, email)

        // crea perfil en Firebase.
        if (isNewUser) {
            Log.d("AccesoActivity", "Es un usuario nuevo de Google. Creando perfil en Firebase...")

            val displayName = user.displayName ?: "Usuario"
            val partes = displayName.split(" ")
            val nombre = partes.firstOrNull() ?: "Usuario"
            val apellidos = if (partes.size > 1) partes.drop(1).joinToString(" ") else "Google"

            val perfil = UserProfile(
                nombre = nombre,
                apellidoPaterno = apellidos,
                apellidoMaterno = "",
                correo = email,
                celular = user.phoneNumber ?: "",
                genero = "otro",
                avatar = "otro",
                aceptaTerminos = true,
                hobbies = emptyMap()
            )

            FirebaseDb.saveUserProfileAndIndex(uid, perfil) { ok, ex ->
                if (!ok) {
                    toast("Advertencia: no se pudo guardar tu perfil: ${ex?.message}")
                }
            }

            FirebaseDb.upsertEmailIndex(email, uid)
        }

        Toast.makeText(this, "¡Bienvenido/a! 🎉", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, InicioActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun isCorreoPermitido(correo: String): Boolean {
        if (!correo.contains("@")) return false
        val dominios = listOf("@mh.pe", "@hotmail.com", "@gmail.com")
        val okDom = dominios.any { correo.endsWith(it, ignoreCase = true) }
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return okDom && regex.matches(correo)
    }

    private fun setLoading(loading: Boolean) {
        binding.btnInicio.isEnabled = !loading
        binding.btnGoogle.isEnabled = !loading
        binding.tvRegistro.isEnabled = !loading
        binding.tvOlvidarClave.isEnabled = !loading
        binding.tietUsuario.isEnabled = !loading
        binding.tietClave.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun clearErrors() {
        binding.tilUsuario.error = null
        binding.tilClave.error = null
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
