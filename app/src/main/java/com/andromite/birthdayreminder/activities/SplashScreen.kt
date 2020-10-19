package com.andromite.birthdayreminder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import com.andromite.birthdayreminder.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        var top_anim  = AnimationUtils.loadAnimation(this,R.anim.top_anim)
        var bottom_anim  = AnimationUtils.loadAnimation(this,R.anim.bottom_anim)

        logo.animation = top_anim
        txt.animation = bottom_anim

        Handler().postDelayed(Runnable {
            calculateUserLogin()
        },3000)

    }

    fun calculateUserLogin() {

        //get current logged in user. returns null if not logged in.
        var current_user = FirebaseAuth.getInstance().currentUser
        if (current_user!=null) {
            Log.e("12345 current UID", current_user!!.uid)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this,SocialLogin::class.java))
        }

    }

}
