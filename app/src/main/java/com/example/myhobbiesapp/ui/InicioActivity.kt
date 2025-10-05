package com.example.myhobbiesapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.databinding.ActivityInicioBinding
import com.google.android.material.navigation.NavigationView

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            binding.dlayMenu.openDrawer(GravityCompat.START)
        }

        binding.nvMenu.setNavigationItemSelectedListener(navListener)

        if (savedInstanceState == null) {
            selectMenu(R.id.itInicio)
        }
    }

    private val navListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
        selectMenu(menuItem.itemId); true
    }

    private fun selectMenu(itemId: Int) {
        val frag: Fragment = when (itemId) {
            R.id.itExplora -> ExploraFragment()
            R.id.itChats   -> ChatsFragment()
            R.id.itPerfil  -> PerfilFragment()
            else           -> InicioFragment()
        }
        replaceFragment(frag)
        binding.nvMenu.setCheckedItem(itemId)
        binding.dlayMenu.closeDrawer(GravityCompat.START)
        binding.toolbar.title = when (itemId) {
            R.id.itExplora -> "Explora"
            R.id.itChats   -> "Chats"
            R.id.itPerfil  -> "Perfil"
            else           -> "Inicio"
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, fragment)
            .commit()
    }
}