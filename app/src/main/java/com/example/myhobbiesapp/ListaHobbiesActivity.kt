package com.example.myhobbiesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.adapter.HobbyAdapter
import com.example.myhobbiesapp.data.HobbyDAO
import com.example.myhobbiesapp.entity.Hobby
import com.example.myhobbiesapp.ui.DialogOpciones
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListaHobbiesActivity :
    AppCompatActivity(R.layout.activity_lista_hobbies),
    DialogOpciones.Listener {

    private lateinit var dao: HobbyDAO
    private var idUsuario: Int = -1

    private lateinit var etHobby: EditText
    private lateinit var etNota: EditText
    private lateinit var btnAgregar: Button
    private lateinit var tvVacio: TextView
    private lateinit var rv: RecyclerView
    private lateinit var adapter: HobbyAdapter
    private lateinit var btnHistorial: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idUsuario = intent.getIntExtra("idUsuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        dao = HobbyDAO(this)

        etHobby = findViewById(R.id.etHobby)
        etNota  = findViewById(R.id.etNota)
        btnAgregar = findViewById(R.id.btnAgregar)
        tvVacio  = findViewById(R.id.tvVacio)
        rv = findViewById(R.id.rvHobbies)
        btnHistorial = findViewById(R.id.btnHistorial)

        rv.layoutManager = LinearLayoutManager(this)
        adapter = HobbyAdapter { hobby -> mostrarDialogOpciones(hobby) }
        rv.adapter = adapter

        refrescarLista()

        btnHistorial.setOnClickListener {
            val it = Intent(this, HistorialActivity::class.java)
            it.putExtra("idUsuario", idUsuario)
            startActivity(it)
        }

        btnAgregar.setOnClickListener {
            val nombre = etHobby.text.toString().trim()
            val nota   = etNota.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Escribe un hobby", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fecha = System.currentTimeMillis().toString()

            val h = Hobby(0, nombre, nota, fecha, idUsuario)
            val id = dao.insert(h)
            if (id > 0) {
                etHobby.setText("")
                etNota.setText("")
                refrescarLista()
            }
        }
    }

    private fun mostrarDialogOpciones(hobby: Hobby) {
        DialogOpciones.newInstance(hobby.id)
            .show(supportFragmentManager, "DialogOpciones")
    }

    // ====== Callbacks del diálogo ======
    override fun onEditar(hobbyId: Int) {
        // Si tienes dao.getById, precarga
        val h = dao.getById(hobbyId)
        if (h == null) {
            Toast.makeText(this, "No se pudo cargar el hobby", Toast.LENGTH_SHORT).show()
            return
        }
        etHobby.setText(h.nombre)
        etNota.setText(h.nota)
        Toast.makeText(this, "Edita y presiona Agregar para guardar", Toast.LENGTH_SHORT).show()
        // Si implementas dao.update(h), puedes cambiar a modo edición real.
    }

    override fun onEliminar(hobbyId: Int) {
        val r = dao.deleteById(hobbyId)
        if (r > 0) {
            Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
            refrescarLista()
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refrescarLista() {
        val list = dao.listByUser(idUsuario)
        adapter.submit(list)
        tvVacio.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}
