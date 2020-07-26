package com.andromite.birthdayreminder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.andromite.birthdayreminder.R
import com.andromite.birthdayreminder.fragments.CalenderFragment
import com.andromite.birthdayreminder.fragments.HomeFragment
import com.andromite.birthdayreminder.fragments.ImportantFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var home : HomeFragment
    lateinit var important : ImportantFragment
    lateinit var calender : CalenderFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        home = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout,home)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()



        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_1 -> {
                    home =
                        HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,home)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.page_2 -> {
                    important =
                        ImportantFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,important)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.page_3 -> {
                    calender =
                        CalenderFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout,calender)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }
}
