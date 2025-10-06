package com.example.myhobbiesapp.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.databinding.ActivityInicioBinding
import com.google.android.material.navigation.NavigationView

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) usa tu Toolbar como ActionBar
        setSupportActionBar(binding.toolbar)

        // 2) conéctala con el Drawer
        toggle = ActionBarDrawerToggle(
            this,
            binding.dlayMenu,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.dlayMenu.addDrawerListener(toggle)
        toggle.syncState()

        // 3) clicks del menú
        binding.nvMenu.setNavigationItemSelectedListener {
            selectMenu(it.itemId)
            true
        }

        if (savedInstanceState == null) selectMenu(R.id.itInicio)
    }

    private fun selectMenu(id: Int) {
        val frag: Fragment = when (id) {
            R.id.itExplora -> ExploraFragment()
            R.id.itChats   -> ChatsFragment()
            R.id.itPerfil  -> PerfilFragment()
            else           -> InicioFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, frag)
            .commit()

        binding.nvMenu.setCheckedItem(id)
        binding.dlayMenu.openDrawer(GravityCompat.START)  // fuerza abrir
               binding.toolbar.title = when (id) {
            R.id.itExplora -> getString(R.string.menu_explora)
            R.id.itChats   -> getString(R.string.menu_chats)
            R.id.itPerfil  -> getString(R.string.menu_perfil)
            else           -> getString(R.string.menu_home)
        }
    }
}