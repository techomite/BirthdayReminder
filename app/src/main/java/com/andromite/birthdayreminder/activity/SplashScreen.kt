package com.andromite.birthdayreminder.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.andromite.birthdayreminder.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logo.animation = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        txt.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        Handler().postDelayed(Runnable {
            calculateUserLogin()
        }, 3000)

    }

    private fun calculateUserLogin() {
        //get current logged in user. returns null if not logged in.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.e("12345 current UID", currentUser.uid)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, SocialLogin::class.java))
            finish()
        }
    }

}
