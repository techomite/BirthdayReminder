package com.andromite.birthdayreminder.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.Utils.SharedPrefrenceUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_social_login.*

class SocialLogin : AppCompatActivity() {

    var RC_SIGN_IN = 1
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login)

        //get current logged in user. returns null if not logged in.
        var current_user = FirebaseAuth.getInstance().currentUser

        // send to MainActivity if user is logged in
//        if (current_user!=null) {
//
//            // save userUID to SP
//            // send to HomeFragment
//            Log.e("12345 current UID", current_user!!.uid)
//            startActivity(Intent(this, MainActivity::class.java))
//
//        }


        googleLogin.setOnClickListener {

            // start signin process
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build())

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser

                var userUID = user!!.uid

                Log.e("12345 UID after login", user!!.uid)

                //add user to the collection in db
                //create SharedPreference for user_id

                val userIdentity = hashMapOf(
                    "displayName" to user.displayName,
                    "email" to user.email
                )


                db.collection("users").document(userUID).set(userIdentity)
                SharedPrefrenceUtils().saveSP(this,"googleuid",userUID)
                startActivity(Intent(this, MainActivity::class.java))


                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

    }

}
