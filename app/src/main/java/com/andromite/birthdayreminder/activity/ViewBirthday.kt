package com.andromite.birthdayreminder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.Utils.SP
import com.andromite.birthdayreminder.Utils.Utils
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_view_birthday.*

class ViewBirthday : AppCompatActivity() {

    lateinit var FSselected_birthday : FSBirthday
    lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_birthday)

        val db = Firebase.firestore
        val storage = Firebase.storage
        uid = SP().get(this,"googleuid")
        Utils.flog(uid)


        FSselected_birthday = getIntent().extras?.get("selected_birthday") as FSBirthday
        setValues()


        edit_birthday.setOnClickListener {

            var intent = Intent(this,EditActivity::class.java)
            intent.putExtra("birthday",FSselected_birthday)
            startActivity(intent)

        }

        //delete birthday
        delete_birthday.setOnClickListener {

            db.collection("users/" + uid + "/Birthdays").document(FSselected_birthday.id)
                .delete()
                .addOnSuccessListener {
                    Utils.flog( "DocumentSnapshot successfully deleted!")
                    startActivity(Intent(this,MainActivity::class.java))

                }
                .addOnFailureListener { e -> Utils.flog("Error deleting document $e") }
        }

        if (!FSselected_birthday.profilePic.equals("")){

            var delref = storage.getReferenceFromUrl(FSselected_birthday.profilePic)
            delref.delete()
                .addOnSuccessListener { Utils.flog( "Photo successfully deleted!") }
                .addOnFailureListener { e -> Utils.flog("Error deleting photo $e") }

        }

        profilePic.setOnClickListener {

            if (!FSselected_birthday.profilePic.equals("")) {
                var intent = Intent(this, ViewPhotoActivity::class.java)
                intent.putExtra("photourl",FSselected_birthday.profilePic)
                startActivity(intent)
            } else {
                Toast.makeText(this,"No Photo",Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun deleteAlarmReminder() {



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

        if (!FSselected_birthday.profilePic.equals("")) {
            Glide.with(this).load(FSselected_birthday.profilePic).into(profilePic)
        }
    }
}
