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
import com.andromite.birthdayreminder.utils.Enums
import com.andromite.birthdayreminder.utils.SP
import com.andromite.birthdayreminder.utils.Utils
import com.andromite.birthdayreminder.activity.ViewBirthday
import com.andromite.birthdayreminder.adapter.FsimpAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_important.view.*


/**
 * A simple [Fragment] subclass.
 */
class ImportantFragment : Fragment() {

    var FSbirthdayList: ArrayList<FSBirthday> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_important,container,false)

        val db = Firebase.firestore




        view.recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        view.recyclerview.hasFixedSize()
//
//
//        launch {
//            context?.let {
//                val notes = BirthdayDatabase(it).getBirthdayDao().getAllBirthday()
//                recyclerview.adapter = HomeAdapter(notes)
//            }
//        }




        // get UID from sharedprefrences
        var uid = context?.let { SP.get(it, Enums.UserId.name) }
        Utils.flog(Enums.UserId.name + uid)


        db.collection("users/" + uid + "/Birthdays").whereEqualTo("isImportant", true).get()
            .addOnSuccessListener {

                for (doc in it.documents) {

                    Utils.flog("List of important Birthdays" + doc.data.toString())
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
                view.recyclerview.adapter = FsimpAdapter(FSbirthdayList,this)

            }



        // Inflate the layout for this fragment
        return view

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     fun onImageClick(imageData: Int) {
        Log.e("12345 in fragment", imageData.toString())



//        var selected_birthday = FSbirthdayList.get(imageData)
//        var list : ArrayList<FSBirthday> = ArrayList()
//        list.add(selected_birthday)

        val intent = Intent(requireContext(), ViewBirthday::class.java)
        intent.putExtra("selected_birthday", FSbirthdayList[imageData])
        startActivity(intent)

    }

}
