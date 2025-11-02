package com.example.myhobbiesapp.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import com.example.myhobbiesapp.workers.AtencionUsuarioWorker
import java.util.concurrent.TimeUnit
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
import com.google.android.material.snackbar.Snackbar
import com.example.myhobbiesapp.util.SessionManager

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    private val MH_LILA    = 0xFFA88CFF.toInt()
    private val MH_CELESTE = 0xFFCDE8FF.toInt()
    private val MH_TEXT    = 0xFF222222.toInt()

    private var tourSnack: Snackbar? = null
    private var tourThread: Thread? = null
    @Volatile private var tourRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureSessionOrGoLogin()) return

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            setDisplayUseLogoEnabled(false)
            title = getString(R.string.menu_home)
        }

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        binding.navView.setNavigationItemSelectedListener { item ->
            binding.drawerLayout.closeDrawers()

            Handler(Looper.getMainLooper()).postDelayed({

                item.isChecked = true

                // Navegamos al fragmento
                when (item.itemId) {
                    R.id.menu_home    -> showFragment(InicioFragment(),  "inicio")
                    R.id.menu_explora -> showFragment(ExploraFragment(), "explora")
                    R.id.menu_chats   -> showFragment(ChatFragment(),   "chats")
                    R.id.menu_solicitudes -> showFragment(SolicitudesFragment(), "solicitudes")
                    R.id.menu_perfil  -> showFragment(PerfilFragment(),  "perfil")
                }
            }, 250) // 250ms de espera

            return@setNavigationItemSelectedListener true
        }
        // --- FIN DE LA ACTUALIZACIÓN ---

        if (savedInstanceState == null) {
            binding.navView.setCheckedItem(R.id.menu_home)
            showFragment(InicioFragment(), "inicio")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (tourRunning) {
                    cancelTourThread()
                } else {
                    finishAfterTransition()
                }
            }
        })

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
            "solicitudes" -> getString(R.string.menu_solicitudes)
            "perfil"  -> getString(R.string.menu_perfil)
            else      -> getString(R.string.app_name)
        }
    }

    private fun prefs() =
        getSharedPreferences("mh_prefs", Context.MODE_PRIVATE)

    // lógica de TOUR
    fun launchOnboardingTour() {
        val drawer  = binding.drawerLayout
        val nav     = binding.navView
        val toolbar = binding.toolbar

        fun Int.dp() = (this * resources.displayMetrics.density).toInt()
        fun View.asRectInWindow(): Rect {
            val p = IntArray(2)
            getLocationInWindow(p)
            return Rect(p[0], p.component2(), p[0] + width, p.component2() + height)
        }
        fun navIconRect(tb: View): Rect {
            val r = tb.asRectInWindow()
            val size = 48.dp()
            val left = r.left + 8.dp()
            val top  = r.top + (tb.height - size)/2
            return Rect(left, top, left + size, top + size)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Tour rápido")
            .setMessage("Te mostramos las secciones clave. Toca el círculo para avanzar o usa “Cancelar”.")
            .setPositiveButton("Empezar") { _, _ ->
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                showTourCancelSnack {
                    cancelTourThread()
                    drawer.closeDrawers()
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                startTourThread()
                toolbar.post {
                    val paso1 = try {
                        TapTarget.forToolbarNavigationIcon(
                            toolbar,
                            "Abrir menú",
                            "Toca dentro del círculo para continuar."
                        )
                    } catch (_: Throwable) {
                        TapTarget.forBounds(
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
                        .cancelable(true)
                        .id(1001)

                    TapTargetView.showFor(
                        this,
                        paso1,
                        object : TapTargetView.Listener() {
                            override fun onTargetClick(view: TapTargetView) {
                                super.onTargetClick(view)
                                drawer.openDrawer(GravityCompat.START)
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)

                                nav.postDelayed({
                                    fun viewRect(v: View, offsetXDp: Int): Rect {
                                        val base = v.asRectInWindow()
                                        val dx = offsetXDp.dp()
                                        return Rect(base.left + dx, base.top, base.right + dx, base.bottom)
                                    }
                                    fun itemTarget(itemId: Int, title: String, desc: String, id: Int): TapTarget {
                                        val itemView = nav.findViewById<View>(itemId)
                                        return if (itemView != null) {
                                            TapTarget.forBounds(
                                                viewRect(itemView, -12),
                                                title, desc
                                            )
                                                .outerCircleColorInt(if (id % 2 == 0) MH_CELESTE else MH_LILA)
                                                .targetCircleColorInt(if (id % 2 == 0) MH_LILA else MH_CELESTE)
                                                .titleTextColorInt(MH_TEXT)
                                                .descriptionTextColorInt(MH_TEXT)
                                                .transparentTarget(true)
                                                .drawShadow(true)
                                                .cancelable(false)
                                                .targetRadius(34)
                                                .id(id)
                                        } else {
                                            val root = findViewById<View>(R.id.nav_host_fragment)
                                            val r = Rect(root.width/4, root.height/3, root.width*3/4, root.height*2/3)
                                            TapTarget.forBounds(r, title, desc)
                                                .outerCircleColorInt(MH_CELESTE)
                                                .titleTextColorInt(MH_TEXT)
                                                .descriptionTextColorInt(MH_TEXT)
                                                .transparentTarget(true)
                                                .drawShadow(true)
                                                .cancelable(false)
                                                .id(id)
                                        }
                                    }

                                    val t2 = itemTarget(R.id.menu_home,    "Inicio",   "Pantalla de bienvenida y tutorial.", 2)
                                    val t3 = itemTarget(R.id.menu_explora, "Explora",  "Descubre perfiles con hobbies.", 3)
                                    val t4 = itemTarget(R.id.menu_solicitudes, "Solicitudes", "Acepta amigos aquí.", 4)
                                    val t5 = itemTarget(R.id.menu_chats,   "Chats",    "Conversa con tus amigos.", 5)
                                    val t6 = itemTarget(R.id.menu_perfil,  "Mi Perfil","Edita tu perfil y hobbies.", 6)


                                    TapTargetSequence(this@InicioActivity)
                                        .targets(t2, t3, t4, t5, t6) // <-- Asegúrate de incluir t4
                                        .listener(object : TapTargetSequence.Listener {
                                            override fun onSequenceFinish() {
                                                tourSnack?.dismiss()
                                                cancelTourThread()
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
                                            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {}
                                            override fun onSequenceCanceled(lastTarget: TapTarget?) {
                                                tourSnack?.dismiss()
                                                cancelTourThread()
                                                drawer.closeDrawers()
                                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                            }
                                        })
                                        .start()
                                }, 180)
                            }
                            override fun onTargetCancel(view: TapTargetView?) {
                                tourSnack?.dismiss()
                                cancelTourThread()
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

    private fun View.asRectInWindow(): Rect {
        val loc = IntArray(2)
        getLocationInWindow(loc)
        return Rect(loc[0], loc[1], loc[0] + width, loc[1] + height)
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

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

    private fun startTourThread() {
        if (tourRunning) return
        tourRunning = true
        tourThread = Thread {
            try {
                val total = 20
                for (step in 1..total) {
                    if (!tourRunning) break
                    Thread.sleep(350)
                    runOnUiThread {
                        if (step % 5 == 0) {
                            tourSnack?.setText("Tour en progreso • ${(step * 100) / total}%")
                        }
                    }
                }
            } catch (_: InterruptedException) {
                // cancelado
            } finally {
                runOnUiThread {
                    tourRunning = false
                    tourSnack?.dismiss()
                }
            }
        }.also { it.start() }
    }

    private fun cancelTourThread() {
        tourRunning = false
        tourThread?.interrupt()
        tourThread = null
        tourSnack?.dismiss()
    }

    private fun ensureSessionOrGoLogin(): Boolean {
        val email = SessionManager.getCurrentEmail(this)
        return if (email.isNullOrBlank()) {
            startActivity(
                Intent(this, AccesoActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            false
        } else {
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tourRunning) return

        val workRequest = OneTimeWorkRequest.Builder(AtencionUsuarioWorker::class.java)
            .setInitialDelay(2, TimeUnit.MINUTES)
            .addTag("come-back-work")
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "come-back-work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}