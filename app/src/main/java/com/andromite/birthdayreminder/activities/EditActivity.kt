package com.andromite.birthdayreminder.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.db.BirthdayDatabase
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit.date
import kotlinx.android.synthetic.main.activity_edit.icn_ann_iv
import kotlinx.android.synthetic.main.activity_edit.icn_ann_tv
import kotlinx.android.synthetic.main.activity_edit.icn_anniversary
import kotlinx.android.synthetic.main.activity_edit.icn_bir_iv
import kotlinx.android.synthetic.main.activity_edit.icn_bir_tv
import kotlinx.android.synthetic.main.activity_edit.icn_birthday
import kotlinx.android.synthetic.main.activity_edit.imp_title
import kotlinx.android.synthetic.main.activity_edit.isImp_tv
import kotlinx.android.synthetic.main.activity_edit.notes
import kotlinx.android.synthetic.main.activity_edit.person_name
import kotlinx.android.synthetic.main.activity_edit.profileImage
import kotlinx.android.synthetic.main.activity_edit.star
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*

class EditActivity : AppCompatActivity() {

    var event : Int = 1
    var imp : Boolean = false
    private val GALLERY_REQUEST_CODE = 1234
    var profileuri : Uri? = null
    lateinit var id : String
    lateinit var db : FirebaseFirestore
    lateinit var  selected_birthday : FSBirthday

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

         db = Firebase.firestore

        selected_birthday  = intent.extras?.get("birthday") as FSBirthday

//        id = selected_birthday.id

        setDefaultData(selected_birthday)


        clickEvents()

        btn_submitt.setOnClickListener {
//            savetoDB()
            updateFirestore()
        }






    }

    fun updateFirestore() {

        val person_name = person_name.text.toString().trim()
        val date = date.text.toString().trim()
        val isImportant = imp
        val notes = notes.text.toString().trim()
        val profilePic = getProfilePic()

        //checking if the attributes are empty
        if (person_name.isEmpty()) {
            tvv1.error = getString(R.string.name_error_string)
        } else if (date.isEmpty()) {
            select_birthday_error.visibility = View.VISIBLE
        }


        if (!person_name.isEmpty() && !date.isEmpty()) {

            val data = hashMapOf("peron_name" to person_name,"date" to date, "isImportant" to isImportant, "notes" to notes)

            db.collection("Birthdays").document(selected_birthday.id)
                .set(data, SetOptions.merge())

            startActivity(Intent(this, MainActivity::class.java))

        }
    }

    private fun savetoDB() {

        val person_name = person_name.text.toString().trim()
        val date = date.text.toString().trim()
        val isImportant = imp
        val notes = notes.text.toString().trim()
        val profilePic = getProfilePic()

        //checking if the attributes are empty
        if (person_name.isEmpty()) {
            tvv1.error = getString(R.string.name_error_string)
        } else if (date.isEmpty()) {
            select_birthday_error.visibility = View.VISIBLE
        }


        if (!person_name.isEmpty() && !date.isEmpty()) {

//            GlobalScope.launch {
//                val birthday = Birthday(person_name, date, event, isImportant, notes, profilePic)
//                applicationContext.let {
//                    BirthdayDatabase(this@EditActivity).getBirthdayDao().updateBirthday(person_name,date,event,isImportant,notes,profilePic,id)
//                    Toast.makeText(this@EditActivity, "Birthday Updated", Toast.LENGTH_SHORT).show()
//                }
//            }

//            launch {
//                val birthday = Birthday(person_name, date, event, isImportant, notes, profilePic)
//                let {
//                    BirthdayDatabase(this@EditActivity).getBirthdayDao().addBirthday(birthday)
//                    Toast.makeText(this@EditActivity, "Birthday Added", Toast.LENGTH_SHORT).show()
//                }
//            }


            startActivity(Intent(this, MainActivity::class.java))

        }
    }

    fun clickEvents(){

        // event
        icn_birthday.setOnClickListener {
            event = 1  // 1 for birthday
            icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
            icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
            icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))


            icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
            icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
            icn_ann_tv.setTextColor(Color.parseColor("#000000"))
        }

        icn_anniversary.setOnClickListener {
            event = 2 // 2 for anniversary
            icn_anniversary.setBackgroundResource(R.drawable.color_primary_bg)
            icn_ann_iv.setImageResource(R.drawable.ic_anniversary_white)
            icn_ann_tv.setTextColor(Color.parseColor("#FFFFFF"))


            icn_birthday.setBackgroundResource(R.drawable.grey_bg)
            icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_black)
            icn_bir_tv.setTextColor(Color.parseColor("#000000"))
        }

        //important
        imp_title.setOnClickListener {

            if (!imp){
                imp = true
                imp_title.setBackgroundResource(R.drawable.color_primary_bg)
                star.setImageResource(R.drawable.ic_gold_star)
                isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
            } else
            {
                imp = false
                imp_title.setBackgroundResource(R.drawable.grey_bg)
                star.setImageResource(R.drawable.ic_black_star)
                isImp_tv.setTextColor(Color.parseColor("#000000"))
            }
        }

        //date
        tvv3.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)



            val dpd = DatePickerDialog(this,R.style.MyDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
            }, year, month, day)
            dpd.show()

            select_birthday_errors.visibility = View.GONE



        }

        //select picture
        select_picc.setOnClickListener {
            pickFromGallery()
        }

        // close
        btn_closee.setOnClickListener {
            finish()
        }

    }

    fun setDefaultData(selectedBirthday: FSBirthday) {

        person_name.setText(selectedBirthday.person_name)
        date.setText(selectedBirthday.date)

        if (selectedBirthday.event.equals("1")){
            event = 1  // 1 for birthday
            icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
            icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
            icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))


            icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
            icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
            icn_ann_tv.setTextColor(Color.parseColor("#000000"))
        } else {
            event = 2 // 2 for anniversary
            icn_anniversary.setBackgroundResource(R.drawable.color_primary_bg)
            icn_ann_iv.setImageResource(R.drawable.ic_anniversary_white)
            icn_ann_tv.setTextColor(Color.parseColor("#FFFFFF"))


            icn_birthday.setBackgroundResource(R.drawable.grey_bg)
            icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_black)
            icn_bir_tv.setTextColor(Color.parseColor("#000000"))
        }

        if (selectedBirthday.isImportant.equals("true")){
            imp = true
            imp_title.setBackgroundResource(R.drawable.color_primary_bg)
            star.setImageResource(R.drawable.ic_gold_star)
            isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
        }else {
            imp = false
            imp_title.setBackgroundResource(R.drawable.grey_bg)
            star.setImageResource(R.drawable.ic_black_star)
            isImp_tv.setTextColor(Color.parseColor("#000000"))
        }

        if (!selectedBirthday.notes.equals("")){
            notes.setText(selectedBirthday.notes)
        }

        if (selectedBirthday.profilePic != null){
            Glide.with(this)
                .load(selectedBirthday.profilePic)
                .into(profileImage)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
                else{
                    Log.e("12345", "Image selection error: Couldn't select that image from memory." )
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    profileuri = result.uri
                    setImage(result.uri)
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("12345", "Crop error: ${result.getError()}" )
                }
            }
        }

    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.OFF)
            .setFixAspectRatio(true)
            .setCropShape(CropImageView.CropShape.OVAL) // default is rectangle
            .start(this)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(profileImage)
    }

    fun getProfilePic() : ByteArray?
    {
        if (profileuri != null){
            val iStream: InputStream? = getContentResolver().openInputStream(profileuri!!)
            val inputData = iStream!!.readBytes()
            return  inputData
        }
        return null
    }

}
