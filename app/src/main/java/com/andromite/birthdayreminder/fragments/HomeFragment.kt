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
import com.andromite.birthdayreminder.activity.EditActivity
import com.andromite.birthdayreminder.activity.ViewBirthday
import com.andromite.birthdayreminder.adapter.FSHomeAdapter
import com.andromite.birthdayreminder.adapter.HomeAdapter
import com.andromite.birthdayreminder.db.Birthday
import com.andromite.birthdayreminder.utils.*
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

    var event: Int = 1
    lateinit var birthdayList: List<FSBirthday>

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var uid = context?.let { SP.get(it, Enums.UserId.name) }
        Utils.flog(Enums.UserId.name + uid)


        view.recyclerview.hasFixedSize()
        view.recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        FireStoreUtils().readAllBirthdays(requireContext(), object : FirestoreListener {
            override fun fireStoreResponse(response: Any) {
                birthdayList = response as List<FSBirthday>
                view.recyclerview.adapter = FSHomeAdapter(birthdayList, this@HomeFragment)
            }

            override fun viewResponse(name: String, birthday: FSBirthday?) {

            }

        })

        // FAB add Birthday Button
        view.floating_action_button.setOnClickListener {
            var intent = Intent(context, EditActivity::class.java)
            intent.putExtra(Enums.ADD_BIRTHDAY.name, true);
            startActivity(intent)
        }

        return view
    }

    override fun onImageClick(imageData: Int) {
        val intent = Intent(requireContext(), ViewBirthday::class.java)
        intent.putExtra(Enums.DocId.name, birthdayList[imageData].id)
        intent.putExtra(Enums.UPDATE_BIRTHDAY.name, true);
        startActivity(intent)

    }
}
