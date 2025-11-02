package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class DialogEditarPerfil : DialogFragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db get() = com.google.firebase.database.FirebaseDatabase.getInstance().reference

    private lateinit var tilCel: TextInputLayout
    private lateinit var etCel: TextInputEditText
    private lateinit var tilPassActual: TextInputLayout
    private lateinit var etPassActual: TextInputEditText
    private lateinit var tilPassNueva: TextInputLayout
    private lateinit var etPassNueva: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_editar_perfil, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        tilCel = v.findViewById(R.id.tilCelular)
        etCel = v.findViewById(R.id.etCelular)
        tilPassActual = v.findViewById(R.id.tilClaveActual)
        etPassActual = v.findViewById(R.id.etClaveActual)
        tilPassNueva = v.findViewById(R.id.tilClaveNueva)
        etPassNueva = v.findViewById(R.id.etClaveNueva)
        btnGuardar = v.findViewById(R.id.btnGuardar)
        btnCancelar = v.findViewById(R.id.btnCancelar)

        etPassActual.transformationMethod = PasswordTransformationMethod.getInstance()
        etPassNueva.transformationMethod = PasswordTransformationMethod.getInstance()

        val user = auth.currentUser
        val email = SessionManager.getCurrentEmail(requireContext())

        if (user == null || email == null) {
            toast("Sesión no disponible")
            dismiss()
            return
        }
        val uid = user.uid

        btnGuardar.isEnabled = false
        FirebaseDb.getUserProfile(uid) { profile ->
            if (isAdded && profile != null) {
                etCel.setText(profile.celular)
                btnGuardar.isEnabled = true
            } else if (isAdded) {
                toast("No se pudo cargar tu perfil actual")
                dismiss()
            }
        }

        btnCancelar.setOnClickListener { dismiss() }
        btnGuardar.setOnClickListener {
            guardarCambios(user, email)
        }
    }

    private fun guardarCambios(user: com.google.firebase.auth.FirebaseUser, email: String) {
        val uid = user.uid
        val nuevoCel = etCel.text?.toString()?.trim().orEmpty()
        val passActual = etPassActual.text?.toString().orEmpty()
        val passNueva = etPassNueva.text?.toString().orEmpty()

        var ok = true
        if (nuevoCel.isNotEmpty() && (nuevoCel.length != 9 || !nuevoCel.all { it.isDigit() })) {
            tilCel.error = "Debe tener exactamente 9 dígitos"; ok = false
        } else tilCel.error = null

        if (passNueva.isNotEmpty()) {
            if (passActual.isEmpty()) {
                tilPassActual.error = "Requerida para cambiar contraseña"; ok = false
            } else tilPassActual.error = null

            if (passNueva.length < 6) {
                tilPassNueva.error = "Mínimo 6 caracteres"; ok = false
            } else tilPassNueva.error = null
        }
        if (!ok) return

        btnGuardar.isEnabled = false

        // --- Lógica de Actualización ---
        var tareaCelularCompletada = false
        var tareaClaveCompletada = false
        var huboError = false

        val onComplete: (String, Boolean) -> Unit = { tarea, exito ->
            if (!exito) huboError = true

            if (tarea == "cel") tareaCelularCompletada = true
            if (tarea == "pass") tareaClaveCompletada = true

            // Si ambas tareas terminaron (o no eran necesarias)
            if (tareaCelularCompletada && tareaClaveCompletada) {
                if (huboError) {
                    toast("Algunos cambios fallaron. Intenta de nuevo.")
                } else {
                    toast("Perfil actualizado correctamente")
                    // Avisamos al PerfilFragment que recargue
                    parentFragmentManager.setFragmentResult(RESULT_PERFIL_EDITADO, Bundle())
                    dismiss()
                }
            }
        }

        db.child("users").child(uid).child("profile").child("celular").setValue(nuevoCel)
            .addOnCompleteListener { onComplete("cel", it.isSuccessful) }

        if (passNueva.isNotEmpty() && passActual.isNotEmpty()) {
            // Para cambiar clave, Firebase te obliga a re-autenticar por seguridad
            val credencial = EmailAuthProvider.getCredential(email, passActual)

            user.reauthenticate(credencial).addOnCompleteListener { reAuthTask ->
                if (!reAuthTask.isSuccessful) {
                    toast("Contraseña actual incorrecta")
                    tilPassActual.error = "Contraseña incorrecta"
                    btnGuardar.isEnabled = true // Desbloquear
                    return@addOnCompleteListener
                }

                // Si la re-autenticación fue exitosa, cambiamos la clave
                user.updatePassword(passNueva).addOnCompleteListener { updateTask ->
                    if (!updateTask.isSuccessful) {
                        toast("Error al cambiar contraseña: ${updateTask.exception?.message}")
                    }
                    onComplete("pass", updateTask.isSuccessful)
                }
            }
        } else {
            tareaClaveCompletada = true
        }
    }

    private fun toast(msg: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val RESULT_PERFIL_EDITADO = "perfil_editado"
        fun newInstance() = DialogEditarPerfil()
    }
}
