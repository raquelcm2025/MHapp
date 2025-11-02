package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.databinding.ActivityPasswordResetBinding
import com.google.firebase.auth.FirebaseAuth

class RestablecerClaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordResetBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviar.setOnClickListener {
            val correo = binding.etEmail.text?.toString()?.trim().orEmpty()
            if (correo.isEmpty()) {
                binding.etEmail.error = "Ingresa tu correo"
                return@setOnClickListener
            }
            binding.etEmail.error = null

            auth.sendPasswordResetEmail(correo).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    Toast.makeText(this, "Revisa tu correo para restablecer la contraseña", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, t.exception?.localizedMessage ?: "No se pudo enviar el correo", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnEnviar.setOnClickListener { finish() }
    }
}
