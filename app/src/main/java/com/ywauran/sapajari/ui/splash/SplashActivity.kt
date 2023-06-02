package com.ywauran.sapajari.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import com.ywauran.sapajari.R
import com.ywauran.sapajari.databinding.ActivitySplashBinding
import com.ywauran.sapajari.ui.auth.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_DURATION = 2000L // Duration in milliseconds
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()


        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DURATION)
    }
}