package com.example.myhobbiesapp.firebase

import com.example.myhobbiesapp.firebase.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Un objeto simple para manejar la lógica de perfiles de Firebase
 */
object FirebaseDb {

    private val db get() = FirebaseDatabase.getInstance().reference
    private val auth get() = FirebaseAuth.getInstance()

    // --- Funciones de Perfil e Índice

    fun saveUserProfileAndIndex(uid: String, profile: UserProfile, onDone: (Boolean, Exception?) -> Unit) {
        // 1. Preparamos el perfil completo para /users/
        val profilePath = "/users/$uid/profile"
        val profileData = profile

        // 2. Preparamos el índice público para /userIndex/
        val indexPath = "/userIndex/$uid"
        val indexData = mapOf(
            "nombre" to profile.nombre,
            "apellidoPaterno" to profile.apellidoPaterno,
            "correo" to profile.correo,
            "genero" to profile.genero
        )

        // 3. Creamos un mapa para la actualización "multi-path"
        val updates = mapOf(
            profilePath to profileData,
            indexPath to indexData
        )

        // 4. Ejecutamos la actualización atómica
        db.updateChildren(updates)
            .addOnCompleteListener { task -> onDone(task.isSuccessful, task.exception) }
    }

    /**
     * Obtiene el perfil completo de un solo usuario.
     */
    fun getUserProfile(uid: String, onResult: (UserProfile?) -> Unit) {
        db.child("users").child(uid).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    onResult(s.getValue(UserProfile::class.java))
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }


    fun observeUserIndex(onChange: (Map<String, UserProfile>) -> Unit): ValueEventListener {
        val ref = db.child("userIndex")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val out = mutableMapOf<String, UserProfile>()
                snapshot.children.forEach { u ->
                    val p = u.getValue(UserProfile::class.java)
                    if (p != null && u.key != null) {
                        out[u.key!!] = p
                    }
                }
                onChange(out)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }

    /**
     * Actualiza la lista de hobbies de un usuario.
     */
    fun saveUserHobbies(uid: String, hobbies: Map<String, Boolean>, onDone: (Boolean) -> Unit) {
        db.child("users").child(uid).child("profile").child("hobbies")
            .setValue(hobbies)
            .addOnCompleteListener { task -> onDone(task.isSuccessful) }
    }


    // --- Funciones de Índice de Email

    private fun emailKey(email: String) = email.trim().lowercase()
        .replace(".", ",").replace("#", "_").replace("$", "_")
        .replace("[", "_").replace("]", "_")

    fun upsertEmailIndex(email: String, uid: String) {
        if (email.isBlank() || uid.isBlank()) return
        db.child("index").child("emailToUid").child(emailKey(email)).setValue(uid)
    }

    fun getUidByEmail(email: String, onResult: (String?) -> Unit) {
        db.child("index").child("emailToUid").child(emailKey(email))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    onResult(s.getValue(String::class.java))
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }
}