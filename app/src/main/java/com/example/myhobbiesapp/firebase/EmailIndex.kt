package com.example.myhobbiesapp.firebase

import com.google.firebase.database.FirebaseDatabase

object EmailIndex {
    private fun key(email: String) = email.trim().lowercase()
        .replace(".", ",").replace("#","_").replace("$","_")
        .replace("[","_").replace("]","_")

    /** Guarda/actualiza: email -> uid */
    fun upsert(email: String, uid: String) {
        if (email.isBlank() || uid.isBlank()) return
        FirebaseDatabase.getInstance()
            .getReference("index/emailToUid/${key(email)}")
            .setValue(uid)
    }

    /** Resuelve uid por email. Si no existe, cb(null) */
    fun resolve(email: String, cb: (String?) -> Unit) {
        if (email.isBlank()) { cb(null); return }
        FirebaseDatabase.getInstance()
            .getReference("index/emailToUid/${key(email)}")
            .get()
            .addOnSuccessListener { cb(it.getValue(String::class.java)) }
            .addOnFailureListener { cb(null) }
    }
}
