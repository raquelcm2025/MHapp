package com.example.myhobbiesapp.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.example.myhobbiesapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val anim = findViewById<LottieAnimationView>(R.id.animationSplash)

        anim.setRenderMode(RenderMode.AUTOMATIC)
        anim.repeatCount = 0
        anim.speed = 1.0f

        anim.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashActivity, AccesoActivity::class.java))
                finish() // se cierra splash para no volver atrás
            }
        })

        anim.playAnimation()

        anim.postDelayed({
            startActivity(Intent(this, AccesoActivity::class.java))
            finish()
        }, 5000) // 5000 ms = 5 segundos
    }
}