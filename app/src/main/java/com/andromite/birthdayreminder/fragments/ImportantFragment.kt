package com.andromite.birthdayreminder.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.activity.ViewBirthday
import com.andromite.birthdayreminder.adapter.FsimpAdapter
import com.andromite.birthdayreminder.utils.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_important.view.*


/**
 * A simple [Fragment] subclass.
 */
class ImportantFragment : Fragment(), FirestoreListener {

    var FSbirthdayList: ArrayList<FSBirthday> = ArrayList()
    lateinit var recyclerview : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_important,container,false)
        recyclerview = view.findViewById(R.id.recyclerview)

        FireStoreUtils().readImportantBirthdays(requireContext(), this)


        return view

    }

     fun onImageClick(imageData: Int) {
        val intent = Intent(requireContext(), ViewBirthday::class.java)
        intent.putExtra("selected_birthday", FSbirthdayList[imageData])
        startActivity(intent)

    }

    override fun fireStoreResponse(response: Any) {
        FSbirthdayList = response as ArrayList<FSBirthday>
       recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
       recyclerview.hasFixedSize()
       recyclerview.adapter = FsimpAdapter(FSbirthdayList,this)
    }

    override fun viewResponse(name: String, birthday: FSBirthday?) {
    }

}
