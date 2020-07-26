package com.andromite.birthdayreminder.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andromite.birthdayreminder.R

/**
 * A simple [Fragment] subclass.
 */
class ImportantFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



//        recyclerview.hasFixedSize()
//        recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//
//
//        launch {
//            context?.let {
//                val notes = BirthdayDatabase(it).getBirthdayDao().getAllBirthday()
//                recyclerview.adapter = HomeAdapter(notes)
//            }
//        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_important, container, false)




    }

}
