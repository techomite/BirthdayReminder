package com.andromite.birthdayreminder.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.utils.*
import com.andromite.birthdayreminder.utils.Constants.Companion.GALLERY_REQUEST_CODE
import com.andromite.birthdayreminder.broadcast.ReminderBroadcast
import com.andromite.birthdayreminder.fragments.HomeFragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.util.*

class EditActivity : AppCompatActivity(), FirestoreListener, FirebaseCloudListener {

    var event: Int = 1
    var imp: Boolean = false
    var profileuri: Uri? = null
    lateinit var id: String
    lateinit var selected_birthday: FSBirthday
    lateinit var uid: String
    val storage = Firebase.storage
    var profilePicAdded: Boolean = false
    lateinit var home: HomeFragment
    lateinit var current_date: Calendar
    lateinit var selected_date: Calendar
    lateinit var docId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        uid = SP.get(this, Enums.UserId.name)


        if (intent.hasExtra(Enums.ADD_BIRTHDAY.name)) {
            docId = FireStoreUtils().getUniqueDocId(this)

        } else {
            var docId = intent.getStringExtra(Enums.DocId.name)
            FireStoreUtils().viewBirthday(this, docId.toString(), this) // update selected birthday
//            setDefaultData(selected_birthday)
        }

        btn_submitt.setOnClickListener {
//            saveToDB()        // old Room DB code
            if (intent.hasExtra(Enums.ADD_BIRTHDAY.name)) {
                if (profilePicAdded)
                uploadProfilePic()
                else
                    addBirthdayFireStore()
//                setReminder()
            } else if (intent.hasExtra(Enums.UPDATE_BIRTHDAY.name)) {
                updateFireStore()
            }
        }

        clickEvents()
    }

    private fun setReminder() {

        val intent = Intent(this, ReminderBroadcast::class.java)
        intent.putExtra("name", person_name.text.toString().trim())
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val manager = getSystemService(ALARM_SERVICE) as AlarmManager

//        var time = System.currentTimeMillis()
//        var birthday_time = 1000 * 10;

        manager.set(
            AlarmManager.RTC_WAKEUP,
//         time + longtime,
            selected_date.timeInMillis,
            pendingIntent
        )


    }

    private fun verifyData(): Boolean {
        val personName = person_name.text.toString().trim()
        val date = date.text.toString().trim()

        //checking if the attributes are empty
        if (personName.isEmpty()) {
            tv1.error = getString(R.string.name_error_string)
            return false
        }

        if (date.isEmpty()) {
            et_select_birthday_error.visibility = View.VISIBLE
            return false
        } else {
            et_select_birthday_error.visibility = View.GONE
        }

        return true
    }

    private fun addBirthdayFireStore() {
        home = HomeFragment()

        val personName = person_name.text.toString().trim()
        val date = date.text.toString().trim()
        val isImportant = imp
        val notes = notes.text.toString().trim()
//        val profilePic = getProfilePic()

        val verifyData = verifyData()

        if (verifyData) {

            CoroutineScope(Dispatchers.Main).launch {
                val birthday: FSBirthday = if (profilePicAdded)
                    FSBirthday(
                        docId,
                        personName,
                        date,
                        event.toString(),
                        isImportant.toString(),
                        notes,
                        profileuri?.lastPathSegment.toString()
                    )
                else
                    FSBirthday(
                        docId,
                        personName,
                        date,
                        event.toString(),
                        isImportant.toString(),
                        notes,
                        ""
                    )

                FireStoreUtils().addBirthday(this@EditActivity, birthday, this@EditActivity)
            }
        }
    }

    private fun uploadProfilePic() {
        CoroutineScope(Dispatchers.Main).launch {
            profileuri?.let {
                FirebaseCloudStorageUtils().uploadProfilePic(
                    this@EditActivity,
                    it,
                    this@EditActivity
                )
            }
        }
    }

    private fun updateFireStore() {

        val personName = person_name.text.toString().trim()
        val date = date.text.toString().trim()
        val isImportant = imp
        val notes = notes.text.toString().trim()
        val profilePic = getProfilePic()


        if (verifyData()) {
            val birthday: FSBirthday = if (profilePicAdded)
                FSBirthday(
                    selected_birthday.id,
                    personName,
                    date,
                    event.toString(),
                    isImportant.toString(),
                    notes,
                    profileuri?.lastPathSegment.toString()
                )
            else
                FSBirthday(
                    selected_birthday.id,
                    personName,
                    date,
                    event.toString(),
                    isImportant.toString(),
                    notes,
                    selected_birthday.profilePic
                )

            FireStoreUtils().updateBirthday(this, selected_birthday.id, birthday, this)
        }
    }

    private fun saveToDB() {

        val personName = person_name.text.toString().trim()
        val date = date.text.toString().trim()
        val isImportant = imp
        val notes = notes.text.toString().trim()
        val profilePic = getProfilePic()

        //checking if the attributes are empty
        if (personName.isEmpty()) {
            tvv1.error = getString(R.string.name_error_string)
        } else if (date.isEmpty()) {
            select_birthday_error.visibility = View.VISIBLE
        }


        if (personName.isNotEmpty() && date.isNotEmpty()) {

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


//            startActivity(Intent(this, MainActivity::class.java))

        }
    }

    private fun clickEvents() {

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

            if (!imp) {
                imp = true
                imp_title.setBackgroundResource(R.drawable.color_primary_bg)
                star.setImageResource(R.drawable.ic_gold_star)
                isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
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

            current_date = c

            val dpd = DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
                    selected_date = Calendar.getInstance()
                    selected_date.set(Calendar.YEAR, year)
                    selected_date.set(Calendar.MONTH, monthOfYear)
                    selected_date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    selected_date.set(Calendar.HOUR_OF_DAY, 0)
                    selected_date.set(Calendar.MINUTE, 0)
                    selected_date.set(Calendar.SECOND, 0)

                    compareDate()
                },
                year,
                month,
                day
            )

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

    private fun compareDate() {
        val compare = current_date.compareTo(selected_date)

        // if compare > 0 Date 1 occurs after Date 2    increase year and set alarm
        // if compare < 0 Date 1 occurs before Date 2   set alarm directly
        // if compare = 0 Both dates are equal          set alarm directly

        if (compare > 0) {
            val year = selected_date.get(Calendar.YEAR)
            selected_date.set(Calendar.YEAR, year + 1)
        }
    }

    private fun setDefaultData(selectedBirthday: FSBirthday) {

        person_name.setText(selectedBirthday.person_name)
        date.text = selectedBirthday.date

        if (selectedBirthday.event.equals("1")) {
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

        if (selectedBirthday.isImportant == "true") {
            imp = true
            imp_title.setBackgroundResource(R.drawable.color_primary_bg)
            star.setImageResource(R.drawable.ic_gold_star)
            isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            imp = false
            imp_title.setBackgroundResource(R.drawable.grey_bg)
            star.setImageResource(R.drawable.ic_black_star)
            isImp_tv.setTextColor(Color.parseColor("#000000"))
        }

        if (selectedBirthday.notes != "") {
            notes.setText(selectedBirthday.notes)
        }

        if (selectedBirthday.profilePic != "") {
            Glide.with(this)
                .load(selectedBirthday.profilePic)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.ic_circle_avatar)
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
                } else {
                    Log.e("12345", "Image selection error: Couldn't select that image from memory.")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {

                    val file = File(result.uri.path)
                    Utils.flog("original file size: " + file.length())

                    if (file.length() > 600000) { // greater than 600 kb
                        GlobalScope.launch {
                            val compressedImage = Compressor.compress(applicationContext, file) {
                                quality(50)
                            }

                            profileuri = Uri.fromFile(compressedImage)
                            profilePicAdded = true
                            runOnUiThread {
                                Utils.flog("compressed file size: " + compressedImage.length())
                                setImage(Uri.fromFile(compressedImage))
                            }
                        }
                    } else {

                        profileuri = result.uri
                        profilePicAdded = true
                        setImage(result.uri)

                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("12345", "Crop error: ${result.error}")
                }
            }
        }

    }

    private fun launchImageCrop(uri: Uri) {
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
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(profileImage)
    }

    private fun getProfilePic(): ByteArray? {
        if (profileuri != null) {
            val iStream: InputStream? = contentResolver.openInputStream(profileuri!!)
            return iStream!!.readBytes()
        }
        return null
    }

    override fun fireStoreResponse(response: Any) {
        //add birthday callback
        if (response == Enums.ADD_REQ_SUCCESS) {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        else if (response == Enums.ADD_REQ_FAILED)
            Toast.makeText(this, "Adding birthday failed", Toast.LENGTH_SHORT).show()

        if (response == Enums.UPDATE_REQ_SUCCESS.name) {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        else if (response == Enums.UPDATE_REQ_FAILED.name)
            Toast.makeText(this, "Updating birthday failed", Toast.LENGTH_SHORT).show()

    }

    override fun viewResponse(name: String, birthday: FSBirthday?) {
        if (name == Enums.VIEW_REQ_SUCCESS.name) {
            if (birthday != null) {
                selected_birthday = birthday
            }
            setDefaultData(selected_birthday)
        } else if (name == Enums.VIEW_REQ_FAILED.name){
            Toast.makeText(this, "Error getting data", Toast.LENGTH_SHORT).show()

        }
    }

    override fun cloudResponse(response: Any) {
        //upload profile pic callback
        if (response == Enums.PROFILE_PIC_REQ_SUCCESS)
            addBirthdayFireStore()
        else if (response == Enums.PROFILE_PIC_REQ_FAILED)
            Toast.makeText(this, "ProfilePic upload failed", Toast.LENGTH_SHORT).show()
    }

}
