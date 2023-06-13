package com.ywauran.sapajari.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.ywauran.sapajari.MainActivity
import com.ywauran.sapajari.databinding.ActivitySplashBinding
import com.ywauran.sapajari.ui.auth.AuthActivity
import com.ywauran.sapajari.ui.challenge.ChallengeActivity

class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_DURATION = 2000L // Duration in milliseconds
    }

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            val currentUser = auth.currentUser
            val intent = if (currentUser != null) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_DURATION)
    }
}