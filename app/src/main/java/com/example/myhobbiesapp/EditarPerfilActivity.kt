package com.example.myhobbiesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.data.UsuarioDAO

class EditarPerfilActivity : AppCompatActivity(R.layout.activity_editar_perfil) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val etCel = findViewById<EditText>(R.id.etCelular)
        val etPass = findViewById<EditText>(R.id.etClave)
        val btn = findViewById<Button>(R.id.btnGuardar)

        val u = SesionActiva.usuarioActual
        if (u == null) { Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show(); finish(); return }

        etCel.setText(u.celular)
        etPass.setText(u.clave)

        val dao = UsuarioDAO(this)
        btn.setOnClickListener {
            val newCel = etCel.text.toString().trim()
            val newPwd = etPass.text.toString().trim()
            var cambios = 0
            if (newCel.isNotEmpty() && newCel != u.celular) { if (dao.updateCelular(u.id, newCel) > 0) { u.celular = newCel; cambios++ } }
            if (newPwd.isNotEmpty() && newPwd != u.clave)   { if (dao.updateClave(u.id, newPwd) > 0)   { u.clave = newPwd; cambios++ } }
            if (cambios > 0) { Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show(); finish() }
            else Toast.makeText(this, "Sin cambios", Toast.LENGTH_SHORT).show()
        }
    }
}
