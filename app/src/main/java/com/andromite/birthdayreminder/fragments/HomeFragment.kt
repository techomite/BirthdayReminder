package com.andromite.birthdayreminder.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromite.birthdayreminder.BaseFragment
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.Utils.Enums
import com.andromite.birthdayreminder.Utils.SP
import com.andromite.birthdayreminder.Utils.Utils
import com.andromite.birthdayreminder.activity.EditActivity
import com.andromite.birthdayreminder.activity.ViewBirthday
import com.andromite.birthdayreminder.adapter.FSHomeAdapter
import com.andromite.birthdayreminder.adapter.HomeAdapter
import com.andromite.birthdayreminder.db.Birthday
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.InputStream
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment(), HomeAdapter.OnRecyclerItemClickListener {

    lateinit var home: HomeFragment
    var imp: Boolean = false
    var event: Int = 1
    var profileuri: Uri? = null
    lateinit var birthdayList: List<Birthday>
    var FSbirthdayList: ArrayList<FSBirthday> = ArrayList()
    var DocList: ArrayList<String> = ArrayList()
    var docid : String = ""

    private val GALLERY_REQUEST_CODE = 1234

    val TAG = "12345"
    var profilePicAdded: Boolean = false


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val db = Firebase.firestore
        val storage = Firebase.storage


        // get UID from sharedprefrences
        var uid = context?.let { SP.get(it, Enums.UserId.name) }
        Utils.flog(Enums.UserId.name + uid)


        view.recyclerview.hasFixedSize()
        view.recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        //get list of birthdays from firestore
        db.collection("users/" + uid + "/Birthdays").orderBy("peron_name").get()
            .addOnSuccessListener {



                for (doc in it.documents) {

                    Utils.flog("List of Birthdays" + doc.data.toString())
                    val dataList  : MutableMap<String, Any>? = doc.data

//                    if (dataList != null) {
                        val birthday = FSBirthday(
                            doc.id,
                            dataList?.getValue("peron_name").toString(),
                            dataList?.getValue("date").toString(),
                            dataList?.getValue("event").toString(),
                            dataList?.getValue("isImportant").toString(),
                            dataList?.getValue("notes").toString(),
                            dataList?.getValue("profilePic").toString()
                        )
                        FSbirthdayList.add(birthday)
//                    }
                }
                view.recyclerview.adapter = FSHomeAdapter(FSbirthdayList, this@HomeFragment)

            }


//
//        launch {
//            context?.let {
//                birthdayList = BirthdayDatabase(it).getBirthdayDao().getAllBirthday()
//                view.recyclerview.adapter = HomeAdapter(birthdayList,this@HomeFragment)
//            }
//        }


        // FAB add Birthday Button
        view.floating_action_button.setOnClickListener {

            var intent = Intent(context,EditActivity::class.java)
            intent.putExtra("add_birthday",true);
            startActivity(intent)



//            val transform: MaterialContainerTransform = MaterialContainerTransform().apply {
//                startView = view.floating_action_button
//                endView = view.cl_one
//                pathMotion = MaterialArcMotion()
//                scrimColor = Color.TRANSPARENT
//                duration = 500
//            }
//
//            TransitionManager.beginDelayedTransition(view.cl_main, transform)
//            view.floating_action_button.visibility = View.GONE
//            view.cl_one.visibility = View.VISIBLE

        }

        return view
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
                    profileuri = result.uri
                    profilePicAdded = true
                    Log.e(TAG, profileuri.toString())
                    setImage(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("12345", "Crop error: ${result.error}")
                }
            }
        }

    }


    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(profileImage)
    }

    private fun getProfilePic(): ByteArray? {
        if (profileuri != null) {
            val iStream: InputStream? =
                requireActivity().contentResolver.openInputStream(profileuri!!)
            val inputData = iStream!!.readBytes()
            return inputData
        }
        return null
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.OFF)
            .setFixAspectRatio(true)
            .setCropShape(CropImageView.CropShape.OVAL) // default is rectangle
            .start(requireContext(), this)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onImageClick(imageData: Int) {
        Log.e("12345 in fragment", imageData.toString())



//        var selected_birthday = FSbirthdayList.get(imageData)
//        var list : ArrayList<FSBirthday> = ArrayList()
//        list.add(selected_birthday)

        val intent = Intent(requireContext(), ViewBirthday::class.java)
        intent.putExtra("selected_birthday", FSbirthdayList[imageData])
        startActivity(intent)

    }

    fun oldAnimationCode() {
//        //event = birthday or anniversary
//        view.icn_birthday.setOnClickListener {
//            event = 1  // 1 for birthday
//            view.icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
//            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
//            view.icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))
//
//
//            view.icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
//            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
//            view.icn_ann_tv.setTextColor(Color.parseColor("#000000"))
//        }
//
//        view.icn_anniversary.setOnClickListener {
//            event = 2 // 2 for anniversary
//            view.icn_anniversary.setBackgroundResource(R.drawable.color_primary_bg)
//            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary_white)
//            view.icn_ann_tv.setTextColor(Color.parseColor("#FFFFFF"))
//
//
//            view.icn_birthday.setBackgroundResource(R.drawable.grey_bg)
//            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_black)
//            view.icn_bir_tv.setTextColor(Color.parseColor("#000000"))
//        }
//
//        // is important
//        view.imp_title.setOnClickListener {
//
//            if (!imp) {
//                imp = true
//                view.imp_title.setBackgroundResource(R.drawable.color_primary_bg)
//                view.star.setImageResource(R.drawable.ic_gold_star)
//                view.isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
//            } else {
//                imp = false
//                view.imp_title.setBackgroundResource(R.drawable.grey_bg)
//                view.star.setImageResource(R.drawable.ic_black_star)
//                view.isImp_tv.setTextColor(Color.parseColor("#000000"))
//            }
//
//
//        }
//
//        //Date
//        view.tv3.setOnClickListener {
//
//            val c = Calendar.getInstance()
//            val calYear = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//
//
//            val dpd = DatePickerDialog(
//                requireContext(),
//                R.style.MyDatePickerDialogTheme,
//                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//                    // Display Selected date in TextView
//                    date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
//                },
//                calYear,
//                month,
//                day
//            )
//            dpd.show()
//
//            select_birthday_error.visibility = View.GONE
//
//
//        }

//        //close and open transition FAB
//        view.btn_close.setOnClickListener {
//
//            val transform: MaterialContainerTransform = MaterialContainerTransform().apply {
//                endView = view.floating_action_button
//                startView = view.cl_one
//                pathMotion = MaterialArcMotion()
//                scrimColor = Color.TRANSPARENT
//                duration = 700
//            }
//            TransitionManager.beginDelayedTransition(view.cl_main, transform)
//
//
//            //clear error message
//            tv1.error = null
//            select_birthday_error.visibility = View.GONE
//
//            //clear attributes
//            view.person_name.text!!.clear()
//            date.text = null
//
//            event = 1  // 1 for birthday
//            view.icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
//            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
//            view.icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))
//
//
//            view.icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
//            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
//            view.icn_ann_tv.setTextColor(Color.parseColor("#000000"))
//
//            imp = false
//            view.imp_title.setBackgroundResource(R.drawable.grey_bg)
//            view.star.setImageResource(R.drawable.ic_black_star)
//            view.isImp_tv.setTextColor(Color.parseColor("#000000"))
//
//            view.notes.text!!.clear()
//
//            //hide additional information
//            view.additional_info.visibility = View.VISIBLE
//
//            view.tv2.visibility = View.GONE
//            view.select_pic.visibility = View.GONE
//            view.imp_title.visibility = View.GONE
//
//
//
//
//
//
//
//            view.floating_action_button.visibility = View.VISIBLE
//            view.cl_one.visibility = View.GONE
//
//        }
//
//        //firestore add birthday
//        view.btn_submit.setOnClickListener {
//
//
//            home = HomeFragment()
//
//            val personName = person_name.text.toString().trim()
//            val date = date.text.toString().trim()
//            val isImportant = imp
//            val notes = notes.text.toString().trim()
//            val profilePic = getProfilePic()
//
//            //checking if the attributes are empty
//            if (personName.isEmpty()) {
//                tv1.error = getString(R.string.name_error_string)
//            } else if (date.isEmpty()) {
//                select_birthday_error.visibility = View.VISIBLE
//            }
//
//            if (personName.isNotEmpty() && date.isNotEmpty()) {
//
//                launch {
//                    val birthday = Birthday(personName, date, event, isImportant, notes, profilePic)
////                    context?.let {
////                        BirthdayDatabase(activity!!).getBirthdayDao().addBirthday(birthday)
//
//                    if (profilePicAdded) {
//                        Utils.flog("inside if" )
//
//                        var ref = storage.reference.child("profilePic/" + uid + "/" +  profileuri!!.lastPathSegment + ".jpg")
//                        ref.putFile(profileuri!!).addOnSuccessListener {
//
//                            Utils.flog("photo uploaded successfully" )
//
//                            ref.downloadUrl.addOnCompleteListener {
//
//                                Utils.flog("Storage downloadedUrl : ${it.getResult()}" )
//
//                                var p = it.result.toString()
//
//                                // Create a new birthday in db
//                                val birthdayMap = hashMapOf(
//                                    "peron_name" to personName,
//                                    "date" to date,
//                                    "event" to event,
//                                    "isImportant" to isImportant,
//                                    "notes" to notes,
//                                    "profilePic" to it.result.toString()
//                                )
//
//                                // Add a new document with a generated ID
//                                db.collection("users/" + uid + "/Birthdays")
//                                    .add(birthdayMap)
//                                    .addOnSuccessListener { documentReference ->
//
//                                        Utils.flog("DocumentSnapshot added with ID: ${documentReference.id}" )
//
//                                        fragmentManager!!.beginTransaction()
//                                            .replace(R.id.frame_layout, home)
//                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                            .commit()
//                                        Toast.makeText(activity, "Birthday Added", Toast.LENGTH_SHORT).show()
//
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Utils.flog( "Error adding document" + e)
//                                    }
//                            }
//                        }
//                    } else {
//
//                        // Create a new birthday in db
//                        val birthdayMap = hashMapOf(
//                            "peron_name" to personName,
//                            "date" to date,
//                            "event" to event,
//                            "isImportant" to isImportant,
//                            "notes" to notes,
//                            "profilePic" to ""
//                        )
//
//                        // Add a new document with a generated ID
//                        db.collection("users/" + uid + "/Birthdays")
//                            .add(birthdayMap)
//                            .addOnSuccessListener { documentReference ->
//
//                                Utils.flog("DocumentSnapshot added with ID: ${documentReference.id}" )
//
//                                fragmentManager!!.beginTransaction()
//                                    .replace(R.id.frame_layout, home)
//                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                    .commit()
//                                Toast.makeText(activity, "Birthday Added", Toast.LENGTH_SHORT).show()
//
//                            }
//                            .addOnFailureListener { e ->
//                                Utils.flog( "Error adding document" + e)
//                            }
//
//                    }
//                }
//
////                fragmentManager!!.beginTransaction()
////                    .replace(R.id.frame_layout, home)
////                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
////                    .commit()
//            }
//
//        }
//
//        view.additional_info.setOnClickListener {
//            view.additional_info.visibility = View.GONE
//            view.tv2.visibility = View.VISIBLE
//            view.select_pic.visibility = View.VISIBLE
//            view.imp_title.visibility = View.VISIBLE
//        }
//
//        view.select_pic.setOnClickListener {
//
//            pickFromGallery()
//
//        }

    }


}
