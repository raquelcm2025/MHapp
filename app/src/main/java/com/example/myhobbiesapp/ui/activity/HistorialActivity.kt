package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.HistorialAdapter
import com.example.myhobbiesapp.data.entity.HistorialItem
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.firebase.model.UserProfile
import com.google.firebase.auth.FirebaseAuth

class HistorialActivity : AppCompatActivity(R.layout.activity_historial) {

    private lateinit var adapter: HistorialAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }
    private var currentUid: String? = null

    private var currentHobbiesMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            Toast.makeText(this, "Sesión no encontrada", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        val rv = findViewById<RecyclerView>(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)


        adapter = HistorialAdapter(mutableListOf()) { item, position ->
            eliminarHobby(item.nombre, position)
        }
        rv.adapter = adapter

        cargarDatosDesdeFirebase()


    }

    private fun cargarDatosDesdeFirebase() {
        val uid = currentUid ?: return

        FirebaseDb.getUserProfile(uid) { profile ->
            if (profile == null) {
                Toast.makeText(this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
                return@getUserProfile
            }

            // Guardamos el mapa para usarlo al borrar
            currentHobbiesMap = profile.hobbies.toMutableMap()


            val listaDeItems = profile.hobbies.keys
                .sorted()
                .map { nombreHobby -> HistorialItem(nombreHobby) }

            adapter.submit(listaDeItems)
        }
    }

    private fun eliminarHobby(nombre: String, position: Int) {
        val uid = currentUid ?: return

        //  Quitar el hobby del mapa local
        currentHobbiesMap.remove(nombre)

        //  Sincronizar el nuevo mapa (sin el hobby) con Firebase
        FirebaseDb.saveUserHobbies(uid, currentHobbiesMap) { ok ->
            if (ok) {
                Toast.makeText(this, "Eliminado: $nombre", Toast.LENGTH_SHORT).show()
                // Si se guardó bien, quitamos el item del adapter visualmente
                adapter.removeAt(position)
            } else {
                Toast.makeText(this, "No se pudo eliminar, reintenta", Toast.LENGTH_SHORT).show()
                // Si falló, lo volvemos a agregar al mapa local (revertir)
                currentHobbiesMap[nombre] = true
            }
        }
    }
}