package com.example.myhobbiesapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.core.UserStore
import com.example.myhobbiesapp.databinding.ActivityInicioBinding
import com.google.android.material.navigation.NavigationView

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // SESIÓN SEGURA
        val logged = UserStore.getLogged(this)
        val saludo = intent.getStringExtra("nombreUsuario") ?: logged?.nombres
        if (saludo == null) {
            startActivity(Intent(this, com.example.myhobbiesapp.AccesoActivity::class.java))
            finish()
            return
        }

        // Esto conecta el ícono ←→ Drawer y lo abre/cierra al tocarlo
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            binding.dlayMenu,            // tu DrawerLayout
            binding.toolbar,             // tu MaterialToolbar
            R.string.nav_open,           // agrega estos strings
            R.string.nav_close
        )
        binding.dlayMenu.addDrawerListener(toggle)
        toggle.syncState()

        binding.nvMenu.setNavigationItemSelectedListener(navListener)

// Fragment por defecto
        if (savedInstanceState == null) selectMenu(R.id.itInicio)


    }

    private val navListener = NavigationView.OnNavigationItemSelectedListener { item ->
        selectMenu(item.itemId); true
    }

    private fun selectMenu(itemId: Int) {
        val frag: Fragment = when (itemId) {
            R.id.itExplora      -> ExploraFragment()
            R.id.itChats        -> ChatsFragment()
            R.id.itPerfil       -> PerfilFragment()
            else                -> InicioFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, frag)
            .commit()
        binding.nvMenu.setCheckedItem(itemId)
        binding.dlayMenu.closeDrawer(GravityCompat.START)
        binding.toolbar.title = when (itemId) {
            R.id.itExplora  -> "Explora"
            R.id.itChats    -> "Chats"
            R.id.itPerfil   -> "Mi cuenta"
            else            -> "Inicio"
        }
    }
}
