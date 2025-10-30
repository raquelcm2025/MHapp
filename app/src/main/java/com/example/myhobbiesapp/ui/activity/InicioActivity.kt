package com.example.myhobbiesapp.ui.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.databinding.ActivityInicioBinding
import com.example.myhobbiesapp.ui.fragment.*
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    private val MH_LILA    = 0xFFA88CFF.toInt()
    private val MH_CELESTE = 0xFFCDE8FF.toInt()
    private val MH_TEXT    = 0xFF222222.toInt()

    // Snackbar del tour (para "Cancelar")
    private var tourSnack: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            setDisplayUseLogoEnabled(false)
            title = getString(R.string.menu_home)
        }

        // menú lateral
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

        if (savedInstanceState == null) {
            binding.navView.setCheckedItem(R.id.menu_home)
            showFragment(InicioFragment(), "inicio")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finishAfterTransition()
                }
            }
        })

        // Lanza automáticamente solo la 1ª vez
        if (!prefs().getBoolean("tutorial_inicio_visto", false)) {
            launchOnboardingTour()
        }
    }

    private fun showFragment(f: Fragment, tag: String) {
        val current = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (current != null && current::class == f::class) {
            updateTitle(tag); return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, f, tag)
            .commitAllowingStateLoss()
        updateTitle(tag)
    }

    private fun updateTitle(tag: String) {
        supportActionBar?.title = when (tag) {
            "inicio"  -> getString(R.string.menu_home)
            "explora" -> getString(R.string.menu_explora)
            "chats"   -> getString(R.string.menu_chats)
            "perfil"  -> getString(R.string.menu_perfil)
            else      -> getString(R.string.app_name)
        }
    }

    private fun prefs() =
        getSharedPreferences("mh_prefs", Context.MODE_PRIVATE)

    /** ===== TOUR controlado desde la Activity (bloquea/abre Drawer) ===== */
    fun launchOnboardingTour() {
        val drawer  = binding.drawerLayout
        val nav     = binding.navView
        val toolbar = binding.toolbar

        // colores
        val MH_LILA    = 0xFFA88CFF.toInt()
        val MH_CELESTE = 0xFFCDE8FF.toInt()
        val MH_TEXT    = 0xFF222222.toInt()

        // helpers
        fun Int.dp() = (this * resources.displayMetrics.density).toInt()
        fun View.asRectInWindow(): Rect {
            val p = IntArray(2)
            getLocationInWindow(p)
            return Rect(p[0], p[1], p[0] + width, p[1] + height)
        }
        // Rect aprox del icono “sandwich” por si forToolbarNavigationIcon falla
        fun navIconRect(tb: View): Rect {
            val r = tb.asRectInWindow()
            val size = 48.dp() // área del botón de nav
            val left = r.left + 8.dp()
            val top  = r.top + (tb.height - size)/2
            return Rect(left, top, left + size, top + size)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Tour rápido")
            .setMessage("Te mostramos las secciones clave. Toca el círculo para avanzar o usa “Cancelar”.")
            .setPositiveButton("Empezar") { _, _ ->
                // bloquea gestos mientras corre el tour
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                // botón Cancelar seguro (no depende de “tocar fuera”)
                val snack = com.google.android.material.snackbar.Snackbar
                    .make(findViewById(android.R.id.content), "Tour en progreso", com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE)
                    .setAction("Cancelar") {
                        drawer.closeDrawers()
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    }
                snack.show()

                // Espera a que el toolbar esté medido
                toolbar.post {
                    // 1) Paso “sandwich”: usa forToolbarNavigationIcon y si no, fallback a forBounds(navIconRect)
                    val paso1 = try {
                        com.getkeepsafe.taptargetview.TapTarget.forToolbarNavigationIcon(
                            toolbar,
                            "Abrir menú",
                            "Toca dentro del círculo para continuar."
                        )
                    } catch (_: Throwable) {
                        com.getkeepsafe.taptargetview.TapTarget.forBounds(
                            navIconRect(toolbar),
                            "Abrir menú",
                            "Toca dentro del círculo para continuar."
                        )
                    }
                    paso1
                        .outerCircleColorInt(MH_LILA)
                        .targetCircleColorInt(MH_CELESTE)
                        .titleTextColorInt(MH_TEXT)
                        .descriptionTextColorInt(MH_TEXT)
                        .transparentTarget(true)
                        .drawShadow(true)
                        .cancelable(true) // permite tocar fuera para cancelar este primer paso
                        .id(1001)

                    com.getkeepsafe.taptargetview.TapTargetView.showFor(
                        this,
                        paso1,
                        object : com.getkeepsafe.taptargetview.TapTargetView.Listener() {
                            override fun onTargetClick(view: com.getkeepsafe.taptargetview.TapTargetView) {
                                super.onTargetClick(view)
                                // abrir y BLOQUEAR drawer abierto
                                drawer.openDrawer(GravityCompat.START)
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)

                                // 2) Lanza la secuencia cuando el menú ya está visible/medido
                                nav.postDelayed({
                                    // targets con leve desplazamiento a la izquierda
                                    fun viewRect(v: View, offsetXDp: Int): Rect {
                                        val base = v.asRectInWindow()
                                        val dx = offsetXDp.dp()
                                        return Rect(base.left + dx, base.top, base.right + dx, base.bottom)
                                    }
                                    fun itemTarget(itemId: Int, title: String, desc: String, id: Int): com.getkeepsafe.taptargetview.TapTarget {
                                        val itemView = nav.findViewById<View>(itemId)
                                        return if (itemView != null) {
                                            com.getkeepsafe.taptargetview.TapTarget.forBounds(
                                                viewRect(itemView, -12), // mueve 12dp a la izquierda
                                                title, desc
                                            )
                                                .outerCircleColorInt(if (id % 2 == 0) MH_CELESTE else MH_LILA)
                                                .targetCircleColorInt(if (id % 2 == 0) MH_LILA else MH_CELESTE)
                                                .titleTextColorInt(MH_TEXT)
                                                .descriptionTextColorInt(MH_TEXT)
                                                .transparentTarget(true)
                                                .drawShadow(true)
                                                .cancelable(false)     // ya no se cancela tocando fuera
                                                .targetRadius(34)
                                                .id(id)
                                        } else {
                                            // fallback por si el item aún no está
                                            val root = findViewById<View>(R.id.nav_host_fragment)
                                            val r = Rect(root.width/4, root.height/3, root.width*3/4, root.height*2/3)
                                            com.getkeepsafe.taptargetview.TapTarget.forBounds(r, title, desc)
                                                .outerCircleColorInt(MH_CELESTE)
                                                .titleTextColorInt(MH_TEXT)
                                                .descriptionTextColorInt(MH_TEXT)
                                                .transparentTarget(true)
                                                .drawShadow(true)
                                                .cancelable(false)
                                                .id(id)
                                        }
                                    }

                                    val t2 = itemTarget(R.id.menu_home,    "Inicio",  "Pantalla de bienvenida y tutorial.",            2)
                                    val t3 = itemTarget(R.id.menu_explora, "Explora", "Descubre perfiles con hobbies en común.",       3)
                                    val t4 = itemTarget(R.id.menu_chats,   "Chats",   "Conversa y comparte intereses en tiempo real.", 4)
                                    val t5 = itemTarget(R.id.menu_perfil,  "Mi Perfil","Edita tu celular y contraseña cuando quieras.", 5)

                                    com.getkeepsafe.taptargetview.TapTargetSequence(this@InicioActivity)
                                        .targets(t2, t3, t4, t5)
                                        .listener(object : com.getkeepsafe.taptargetview.TapTargetSequence.Listener {
                                            override fun onSequenceFinish() {
                                                snack.dismiss()
                                                drawer.closeDrawers()
                                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                                prefs().edit().putBoolean("tutorial_inicio_visto", true).apply()

                                                MaterialAlertDialogBuilder(this@InicioActivity)
                                                    .setTitle("¡Listo!")
                                                    .setMessage("Eso fue todo el tour. ¿Deseas ir a Explora ahora?")
                                                    .setPositiveButton("Ir a Explora") { _, _ ->
                                                        binding.navView.setCheckedItem(R.id.menu_explora)
                                                        showFragment(ExploraFragment(), "explora")
                                                    }
                                                    .setNegativeButton("Quedarme aquí", null)
                                                    .show()
                                            }
                                            override fun onSequenceStep(lastTarget: com.getkeepsafe.taptargetview.TapTarget?, targetClicked: Boolean) {}
                                            override fun onSequenceCanceled(lastTarget: com.getkeepsafe.taptargetview.TapTarget?) {
                                                snack.dismiss()
                                                drawer.closeDrawers()
                                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                            }
                                        })
                                        .start()
                                }, 180) // pequeño delay para asegurar medición del menú
                            }
                            override fun onTargetCancel(view: com.getkeepsafe.taptargetview.TapTargetView?) {
                                snack.dismiss()
                                drawer.closeDrawers()
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                            }
                        }
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Secuencia de los ítems del menú (Inicio → Explora → Chats → Mi Perfil) */
    private fun launchMenuSequence(
        drawer: DrawerLayout,
        nav: NavigationView
    ) {
        fun targetMenuItem(itemId: Int, title: String, desc: String, stepId: Int): TapTarget {
            val itemView = nav.findViewById<View>(itemId)
            return if (itemView != null) {
                val bounds = rectWithOffsetX(itemView, offsetXDp = -12) // mueve un poco a la izquierda
                TapTarget.forBounds(bounds, title, desc)
                    .outerCircleColorInt(if (stepId % 2 == 0) MH_CELESTE else MH_LILA)
                    .targetCircleColorInt(if (stepId % 2 == 0) MH_LILA else MH_CELESTE)
                    .titleTextColorInt(MH_TEXT)
                    .descriptionTextColorInt(MH_TEXT)
                    .drawShadow(true)
                    .transparentTarget(true)
                    .cancelable(false)   // no cancelar tocando fuera
                    .id(stepId)
                    .targetRadius(34)
            } else {
                val root = findViewById<View>(R.id.nav_host_fragment)
                val r = Rect(root.width/4, root.height/3, root.width*3/4, root.height*2/3)
                TapTarget.forBounds(r, title, desc)
                    .outerCircleColorInt(MH_CELESTE)
                    .titleTextColorInt(MH_TEXT)
                    .descriptionTextColorInt(MH_TEXT)
                    .drawShadow(true)
                    .transparentTarget(true)
                    .cancelable(false)
                    .id(stepId)
            }
        }

        val paso2 = targetMenuItem(R.id.menu_home,    "Inicio",   "Pantalla de bienvenida y tutorial.",            2)
        val paso3 = targetMenuItem(R.id.menu_explora, "Explora",  "Descubre perfiles con hobbies en común.",       3)
        val paso4 = targetMenuItem(R.id.menu_chats,   "Chats",    "Conversa y comparte intereses en tiempo real.", 4)
        val paso5 = targetMenuItem(R.id.menu_perfil,  "Mi Perfil","Edita tu celular y contraseña cuando quieras.", 5)

        TapTargetSequence(this)
            .targets(paso2, paso3, paso4, paso5)
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    tourSnack?.dismiss()
                    drawer.closeDrawers()
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    prefs().edit().putBoolean("tutorial_inicio_visto", true).apply()

                    MaterialAlertDialogBuilder(this@InicioActivity)
                        .setTitle("¡Listo!")
                        .setMessage("Eso fue todo el tour. ¿Deseas ir a Explora ahora?")
                        .setPositiveButton("Ir a Explora") { _, _ ->
                            binding.navView.setCheckedItem(R.id.menu_explora)
                            showFragment(ExploraFragment(), "explora")
                        }
                        .setNegativeButton("Quedarme aquí", null)
                        .show()
                }
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) { /* no-op */ }
                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    tourSnack?.dismiss()
                    drawer.closeDrawers()
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            })
            .start()
    }

    /** ===== Helpers ===== */
    private fun View.asRectInWindow(): Rect {
        val loc = IntArray(2)
        getLocationInWindow(loc)
        return Rect(loc[0], loc[1], loc[0] + width, loc[1] + height)
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    /** Rect del item con desplazamiento horizontal (negativo = hacia la izquierda) */
    private fun rectWithOffsetX(v: View, offsetXDp: Int): Rect {
        val r = v.asRectInWindow()
        val dx = offsetXDp.dp()
        return Rect(r.left + dx, r.top, r.right + dx, r.bottom)
    }

    private fun showTourCancelSnack(onCancel: () -> Unit) {
        tourSnack?.dismiss()
        val root = findViewById<View>(android.R.id.content)
        tourSnack = Snackbar
            .make(root, "Tour en progreso", Snackbar.LENGTH_INDEFINITE)
            .setAction("Cancelar") { onCancel() }
        tourSnack?.show()
    }
}
