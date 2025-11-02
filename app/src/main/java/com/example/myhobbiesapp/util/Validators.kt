package com.example.myhobbiesapp.util

object Validators {


    fun isCorreoPermitido(correo: String): Boolean {
        val email = correo.trim()
        if (!email.contains("@")) return false

        val lower = email.lowercase()
        val dominioOk = lower.endsWith("@gmail.com") || lower.endsWith("@hotmail.com") || lower.endsWith("@gmail.com")
        if (!dominioOk) return false

        // Regex básica de email (ya usabas algo similar)
        val basic = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return basic.matches(email)
    }
}
