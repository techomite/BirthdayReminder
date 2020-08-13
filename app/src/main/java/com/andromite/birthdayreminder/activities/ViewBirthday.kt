package com.andromite.birthdayreminder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.adapter.FSHomeAdapter
import com.andromite.birthdayreminder.db.Birthday
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_view_birthday.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class ViewBirthday : AppCompatActivity() {

    lateinit var FSselected_birthday : FSBirthday

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_birthday)

        val db = Firebase.firestore


        FSselected_birthday = getIntent().extras?.get("selected_birthday") as FSBirthday
        setValues()


        edit_birthday.setOnClickListener {

            var intent = Intent(this,EditActivity::class.java)
            intent.putExtra("birthday",FSselected_birthday)
            startActivity(intent)

        }

    }

    fun setValues(){

        viewBirthday.visibility = View.VISIBLE

        ed_name.text = FSselected_birthday.person_name

        birthday.text = FSselected_birthday.date
        notes.text = FSselected_birthday.notes

        if (FSselected_birthday.isImportant.equals("1")){
            isImportant.visibility = View.VISIBLE
        }

        if (FSselected_birthday.event == "1") {
            imageView2.setImageResource(R.drawable.ic_birthdaycake_black)
        } else {
            imageView2.setImageResource(R.drawable.ic_anniversary)
        }
    }
}
