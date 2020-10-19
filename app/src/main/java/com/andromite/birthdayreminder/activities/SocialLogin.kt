package com.andromite.birthdayreminder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.Utils.Constants.Companion.RC_SIGN_IN
import com.andromite.birthdayreminder.Utils.SharedPrefrenceUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_social_login.*

class SocialLogin : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login)

        //region SignIn Process
        googleLogin.setOnClickListener {

            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN

            )
        }
        //endregion

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                val userUID = user!!.uid

                //add user to the collection in db
                //create SharedPreference for user_id

                val userIdentity = hashMapOf(
                    "displayName" to user.displayName,
                    "email" to user.email
                )

                db.collection("users").document(userUID).set(userIdentity)
                SharedPrefrenceUtils().saveSP(this, "googleuid", userUID)
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                Toast.makeText(this, "Login Failed, Please try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

}
