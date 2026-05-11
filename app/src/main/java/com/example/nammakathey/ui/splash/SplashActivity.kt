package com.nammakathey.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.nammakathey.R
import com.nammakathey.databinding.ActivitySplashBinding
import com.nammakathey.ui.home.HomeActivity
import com.nammakathey.utils.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_up)
        val pulse  = AnimationUtils.loadAnimation(this, R.anim.pulse)

        binding.logoImage.startAnimation(fadeIn)
        binding.appTitle.startAnimation(fadeIn)
        binding.appSubtitle.startAnimation(fadeIn)
        binding.lampIcon.startAnimation(pulse)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500)
    }
}