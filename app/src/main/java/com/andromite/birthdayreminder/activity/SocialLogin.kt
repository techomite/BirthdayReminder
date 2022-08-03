package com.andromite.birthdayreminder.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.Utils.Enums
import com.andromite.birthdayreminder.Utils.SP
import com.andromite.birthdayreminder.Utils.Utils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_social_login.*

class SocialLogin : AppCompatActivity() {

    private val db = Firebase.firestore

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login)

        startActivity(Intent(this, MainActivity::class.java))

        //region SignIn Process
        googleLogin.setOnClickListener {

            // Choose authentication providers
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
        //endregion

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            val userUID = user!!.uid

            val userIdentity = hashMapOf(
                "displayName" to user.displayName,
                "email" to user.email
            )

//            db.collection(Enums.Users.name).document(userUID).set(userIdentity)

            SP.save(this, Enums.UserId.name, userUID)
            startActivity(Intent(this, MainActivity::class.java))
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...

            Toast.makeText(this, "Login Failed, Please try again! ${response?.error}", Toast.LENGTH_LONG).show()
            Utils.flog("response error: ${response?.error}")

        }
    }
}
