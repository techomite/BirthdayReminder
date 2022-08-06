package com.andromite.birthdayreminder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.utils.*
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_view_birthday.*

class ViewBirthday : AppCompatActivity(), FirestoreListener {

    var FSselected_birthday : FSBirthday? = null
    lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_birthday)

        uid = SP.get(this, Enums.UserId.name)
        Utils.flog(uid)

        intent.getStringExtra(Enums.DocId.name)
            ?.let { FireStoreUtils().viewBirthday(this, it, this) }


        edit_birthday.setOnClickListener {
            var intent = Intent(this, EditActivity::class.java)
            intent.putExtra(Enums.DocId.name, getIntent().getStringExtra(Enums.DocId.name))
            intent.putExtra(Enums.UPDATE_BIRTHDAY.name, true)
            startActivity(intent)

        }

        //delete birthday
        delete_birthday.setOnClickListener {

            FSselected_birthday?.let { it1 -> FireStoreUtils().deleteBirthday(this, it1.id, this ) }
            FSselected_birthday?.let { it1 -> FirebaseCloudStorageUtils().deleteImage(it1.profilePic) }
    }

        profilePic.setOnClickListener {

            if (!FSselected_birthday?.profilePic.equals("")) {
                var intent = Intent(this, ViewPhotoActivity::class.java)
                intent.putExtra("photourl",FSselected_birthday?.profilePic)
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

        ed_name.text = FSselected_birthday?.person_name

        birthday.text = FSselected_birthday?.date
        notes.text = FSselected_birthday?.notes

        if (FSselected_birthday?.isImportant.equals("1")){
            isImportant.visibility = View.VISIBLE
        }

        if (FSselected_birthday?.event == "1") {
            imageView2.setImageResource(R.drawable.ic_birthdaycake_black)
        } else {
            imageView2.setImageResource(R.drawable.ic_anniversary)
        }

        if (!FSselected_birthday?.profilePic.equals("")) {
            FSselected_birthday?.profilePic?.let {
                FirebaseCloudStorageUtils().downloadImage(it, object : FirebaseCloudListener {
                    override fun cloudResponse(response: Any) {
                        Glide.with(this@ViewBirthday).load(response).into(profilePic)
                    }
                })
            }
        }
    }

    override fun fireStoreResponse(response: Any) {
        if (response == Enums.DELETE_REQ_SUCCESS.name) {
            startActivity(Intent(this, MainActivity::class.java))
        } else if (response == Enums.DELETE_REQ_FAILED.name){
            Toast.makeText(this, "Error deleting birthday", Toast.LENGTH_SHORT).show()

        }
    }

    override fun viewResponse(name: String, birthday: FSBirthday?) {
        if (name == Enums.VIEW_REQ_SUCCESS.name) {
            FSselected_birthday = birthday
            setValues()
        } else if (name == Enums.VIEW_REQ_FAILED.name){
            Toast.makeText(this, "Error getting data", Toast.LENGTH_SHORT).show()

        }
    }
}
