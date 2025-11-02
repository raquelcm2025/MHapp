package com.example.myhobbiesapp.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.example.myhobbiesapp.R

class SplashActivity : AppCompatActivity() {

    private var launched = false
    private val handler = Handler(Looper.getMainLooper())
    private val fallback = Runnable { goNext() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val anim = findViewById<LottieAnimationView>(R.id.animationSplash)
        anim.setRenderMode(RenderMode.AUTOMATIC)
        anim.repeatCount = 0
        anim.speed = 1.0f

        anim.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) { goNext() }
            override fun onAnimationCancel(animation: Animator) { goNext() }
        })

        handler.postDelayed(fallback, 5000L)

        anim.playAnimation()
    }

    private fun goNext() {
        if (launched) return
        launched = true

        handler.removeCallbacks(fallback)

        val next = Intent(this, AccesoActivity::class.java)
        next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(next)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacks(fallback)
        super.onDestroy()
    }
}
