package com.andromite.birthdayreminder.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromite.birthdayreminder.BaseFragment
import com.andromite.birthdayreminder.adapter.HomeAdapter
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.activities.ViewBirthday
import com.andromite.birthdayreminder.db.Birthday
import com.andromite.birthdayreminder.db.BirthdayDatabase
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*


class HomeFragment : BaseFragment(), HomeAdapter.OnRecyclerItemClickListener {

    lateinit var home: HomeFragment
    var imp : Boolean = false
    var event : Int = 1
     var profileuri : Uri? = null
     lateinit var birthdayList : List<Birthday>

    private val GALLERY_REQUEST_CODE = 1234

    val TAG = "12345"







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

        // add date selector    done

        // add date validation function

        // add select birthday or anniversary   done
            // change colors if possible
        // add others option
        // add connect with contacts


        //event = birthday or anniversary
        view.icn_birthday.setOnClickListener {
            event = 1  // 1 for birthday
            view.icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
            view.icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))


            view.icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
            view.icn_ann_tv.setTextColor(Color.parseColor("#000000"))
        }

        view.icn_anniversary.setOnClickListener {
            event = 2 // 2 for anniversary
            view.icn_anniversary.setBackgroundResource(R.drawable.color_primary_bg)
            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary_white)
            view.icn_ann_tv.setTextColor(Color.parseColor("#FFFFFF"))


            view.icn_birthday.setBackgroundResource(R.drawable.grey_bg)
            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_black)
            view.icn_bir_tv.setTextColor(Color.parseColor("#000000"))
        }

        // is important
        view.imp_title.setOnClickListener {

            if (!imp){
                imp = true
                view.imp_title.setBackgroundResource(R.drawable.color_primary_bg)
                view.star.setImageResource(R.drawable.ic_gold_star)
                view.isImp_tv.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                imp = false
                view.imp_title.setBackgroundResource(R.drawable.grey_bg)
                view.star.setImageResource(R.drawable.ic_black_star)
                view.isImp_tv.setTextColor(Color.parseColor("#000000"))
            }


        }

        //Date
        view.tv3.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)



                val dpd = DatePickerDialog(requireContext(),R.style.MyDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    date.text = "$dayOfMonth/${monthOfYear + 1}/$year"
                }, year, month, day)
                dpd.show()

            select_birthday_error.visibility = View.GONE



        }





        view.recyclerview.hasFixedSize()
        view.recyclerview.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL, false)

        launch {
            context?.let {
                birthdayList = BirthdayDatabase(it).getBirthdayDao().getAllBirthday()
                view.recyclerview.adapter =
                    HomeAdapter(
                        birthdayList,
                        this@HomeFragment
                    )
            }
        }








        //buttons


        view.floating_action_button.setOnClickListener {

            val transform: MaterialContainerTransform = MaterialContainerTransform().apply {
                startView = view.floating_action_button
                endView = view.cl_one
                pathMotion = MaterialArcMotion()
                scrimColor = Color.TRANSPARENT
                duration = 500
            }

            TransitionManager.beginDelayedTransition(view.cl_main, transform)
            view.floating_action_button.visibility = View.GONE
            view.cl_one.visibility = View.VISIBLE

        }

        view.btn_close.setOnClickListener {

            val transform: MaterialContainerTransform = MaterialContainerTransform().apply {
                endView = view.floating_action_button
                startView = view.cl_one
                pathMotion = MaterialArcMotion()
                scrimColor = Color.TRANSPARENT
                duration = 700
            }
            TransitionManager.beginDelayedTransition(view.cl_main, transform)


            //clear error message
            tv1.error = null
            select_birthday_error.visibility = View.GONE

            //clear attributes
            view.person_name.text!!.clear()
            date.text = null

            event = 1  // 1 for birthday
            view.icn_birthday.setBackgroundResource(R.drawable.color_primary_bg)
            view.icn_bir_iv.setImageResource(R.drawable.ic_birthdaycake_white)
            view.icn_bir_tv.setTextColor(Color.parseColor("#FFFFFF"))


            view.icn_anniversary.setBackgroundResource(R.drawable.grey_bg)
            view.icn_ann_iv.setImageResource(R.drawable.ic_anniversary)
            view.icn_ann_tv.setTextColor(Color.parseColor("#000000"))

            imp = false
            view.imp_title.setBackgroundResource(R.drawable.grey_bg)
            view.star.setImageResource(R.drawable.ic_black_star)
            view.isImp_tv.setTextColor(Color.parseColor("#000000"))

            view.notes.text!!.clear()

            //hide additional information
            view.additional_info.visibility = View.VISIBLE

            view.tv2.visibility = View.GONE
            view.select_pic.visibility = View.GONE
            view.imp_title.visibility = View.GONE







            view.floating_action_button.visibility = View.VISIBLE
            view.cl_one.visibility = View.GONE

        }

        view.btn_submit.setOnClickListener {

            // Create a new user with a first and last name
            val user = hashMapOf(
                "first" to "Ada",
                "last" to "Lovelace",
                "born" to 1815
            )

// Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.e(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }



            home = HomeFragment()

            val person_name = person_name.text.toString().trim()
            val date = date.text.toString().trim()
            val isImportant = imp
            val notes = notes.text.toString().trim()
            val profilePic = getProfilePic()

            //checking if the attributes are empty
            if(person_name.isEmpty()){
                tv1.error = getString(R.string.name_error_string)
            } else if(date.isEmpty()) {
                select_birthday_error.visibility =View.VISIBLE
            }


            if (!person_name.isEmpty() && !date.isEmpty()){
                launch {
                    val birthday = Birthday(person_name,date,event,isImportant,notes,profilePic)
                    context?.let {
                        BirthdayDatabase(activity!!).getBirthdayDao().addBirthday(birthday)
                        Toast.makeText(activity,"Birthday Added",Toast.LENGTH_SHORT).show()
                    }
                }

                fragmentManager!!.beginTransaction()
                    .replace(R.id.frame_layout,home)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }


        }

        view.additional_info.setOnClickListener {

            view.additional_info.visibility = View.GONE

            view.tv2.visibility = View.VISIBLE
            view.select_pic.visibility = View.VISIBLE
            view.imp_title.visibility = View.VISIBLE

        }

        view.select_pic.setOnClickListener {

            pickFromGallery()

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


    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(profileImage)
    }

    fun getProfilePic() : ByteArray?
    {
        if (profileuri != null){
            val iStream: InputStream? = requireActivity().getContentResolver().openInputStream(profileuri!!)
            val inputData = iStream!!.readBytes()
            return  inputData
        }
        return null
    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.OFF)
            .setFixAspectRatio(true)
            .setCropShape(CropImageView.CropShape.OVAL) // default is rectangle
            .start(requireContext(),this)
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

//        val transform: MaterialContainerTransform = MaterialContainerTransform().apply {
//            startView = cardView
//            endView = view?.viewBirthday
//            pathMotion = MaterialArcMotion()
//            scrimColor = Color.TRANSPARENT
//            duration = 500
//        }
//
//        TransitionManager.beginDelayedTransition(view?.cl_main, transform)
//        view!!.floating_action_button.visibility = View.GONE
//        view!!.viewBirthday.visibility = View.VISIBLE

        var selected_birthday = birthdayList.get(imageData)


        val intent = Intent (requireContext(), ViewBirthday::class.java)
        intent.putExtra("birthday", selected_birthday)
        startActivity(intent)

    }




}
