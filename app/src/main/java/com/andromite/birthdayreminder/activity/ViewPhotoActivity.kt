package com.andromite.birthdayreminder.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.utils.FirebaseCloudListener
import com.andromite.birthdayreminder.utils.FirebaseCloudStorageUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_birthday.*
import kotlinx.android.synthetic.main.activity_view_photo.*

class ViewPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)

        val photourl = intent.getStringExtra("photourl")
        if (photourl!!.isNotEmpty()) {
                FirebaseCloudStorageUtils().downloadImage(photourl, object : FirebaseCloudListener {
                    override fun cloudResponse(response: Any) {
                        Glide.with(this@ViewPhotoActivity).load(response).into(photo)
                    }
                })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
