package com.andromite.birthdayreminder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.db.Birthday
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_birthday.*

class ViewBirthday : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_birthday)




        var selected_birthday = getIntent().extras?.get("birthday") as Birthday

        if (selected_birthday.profilePic !=null) {
            Glide.with(this)
                .load(selected_birthday.profilePic)
                .into(profilePic)
        }

        ed_name.text = selected_birthday.person_name

        birthday.text = selected_birthday.date
        notes.text = selected_birthday.notes

        if (selected_birthday.isImportant){
            isImportant.visibility = View.VISIBLE
        }

        if (selected_birthday.event == 1) {
            imageView2.setImageResource(R.drawable.ic_birthdaycake_black)
        } else {
            imageView2.setImageResource(R.drawable.ic_anniversary)
        }

        edit_birthday.setOnClickListener {

            var intent = Intent(this,EditActivity::class.java)
            intent.putExtra("birthday",selected_birthday)
            startActivity(intent)

        }

    }
}
