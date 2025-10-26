package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhobbiesapp.adapter.HobbyAdapter
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.data.entity.Hobby
import com.example.myhobbiesapp.databinding.ActivityListaHobbiesBinding
import com.example.myhobbiesapp.util.CurrentUser

class ListaHobbiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaHobbiesBinding
    private lateinit var adapter: HobbyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaHobbiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idUsuario = CurrentUser.idFromSession(this)
        if (idUsuario == null) {
            Toast.makeText(this, "Inicia sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val dao = HobbyDAO(this)

        adapter = HobbyAdapter() // usa tu constructor real si requiere params
        binding.rvHobbies.layoutManager = LinearLayoutManager(this)
        binding.rvHobbies.adapter = adapter

        cargarLista(dao, idUsuario)
    }

    private fun cargarLista(dao: HobbyDAO, idUsuario: Int) {
        val misHobbies: List<Hobby> = dao.listByUser(idUsuario)
        submitListCompat(adapter, misHobbies)
    }

    // Compatibilidad: intenta llamar submitList(List) o setData(List) si tu adapter usa otro nombre
    private fun submitListCompat(adapter: HobbyAdapter, items: List<Hobby>) {
        try {
            val m = adapter::class.java.getMethod("submitList", List::class.java)
            m.invoke(adapter, items)
            return
        } catch (_: Exception) { /* ignora y prueba setData */ }

        try {
            val m = adapter::class.java.getMethod("setData", List::class.java)
            m.invoke(adapter, items)
            return
        } catch (_: Exception) { /* ignora */ }

        // Si tu adapter expone una lista pública, como 'data', también podrías:
        // try {
        //     val f = adapter::class.java.getDeclaredField("data")
        //     f.isAccessible = true
        //     @Suppress("UNCHECKED_CAST")
        //     val list = f.get(adapter) as? MutableList<Hobby>
        //     list?.let {
        //         it.clear()
        //         it.addAll(items)
        //         adapter.notifyDataSetChanged()
        //     }
        // } catch (_: Exception) {}
    }
}
