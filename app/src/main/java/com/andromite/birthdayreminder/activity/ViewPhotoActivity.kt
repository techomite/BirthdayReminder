package com.andromite.birthdayreminder.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromite.birthdayreminder.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_photo.*

class ViewPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)

        val photourl = intent.getStringExtra("photourl")
        if (photourl!!.isNotEmpty()) {
            Glide.with(this).load(photourl).into(photo)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
