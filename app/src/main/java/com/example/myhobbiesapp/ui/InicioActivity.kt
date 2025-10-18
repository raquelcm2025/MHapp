package com.example.myhobbiesapp.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // === TOOLBAR (sin logo, solo título dinámico) ===
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            setDisplayUseLogoEnabled(false)
            title = getString(R.string.menu_home) // Título inicial
        }

        // === menú lateral ===
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // === NAVEGACIÓN - MENU ===
        binding.navView.setNavigationItemSelectedListener { item ->
            val handled = when (item.itemId) {
                R.id.menu_home    -> { showFragment(InicioFragment(),  "inicio");  true }
                R.id.menu_explora -> { showFragment(ExploraFragment(), "explora"); true }
                R.id.menu_chats   -> { showFragment(ChatsFragment(),   "chats");   true }
                R.id.menu_perfil  -> { showFragment(PerfilFragment(),  "perfil");  true }
                else -> false
            }

            if (handled) {
                item.isChecked = true
                binding.drawerLayout.closeDrawers()
            }
            handled
        }

        //  CARGAR FRAGMENT INICIAL
        if (savedInstanceState == null) {
            binding.navView.setCheckedItem(R.id.menu_home)
            showFragment(InicioFragment(), "inicio")
        }

        // === BOTÓN ATRÁS
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finishAfterTransition()
                }
            }
        })
    }

    // === CAMBIA EL FRAGMENT ACTIVO ===
    private fun showFragment(f: androidx.fragment.app.Fragment, tag: String) {
        val current = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (current != null && current::class == f::class) {
            updateTitle(tag) // Ya estás en fragment; solo actualiza título
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, f, tag)
            .commitAllowingStateLoss()

        updateTitle(tag)
    }

    // === CAMBIA AUTOMÁTICAMENTE EL TÍTULO EN LA TOOLBAR ===
    private fun updateTitle(tag: String) {
        supportActionBar?.title = when (tag) {
            "inicio"  -> getString(R.string.menu_home)
            "explora" -> getString(R.string.menu_explora)
            "chats"   -> getString(R.string.menu_chats)
            "perfil"  -> getString(R.string.menu_perfil)
            else      -> getString(R.string.app_name)
        }
    }
}
