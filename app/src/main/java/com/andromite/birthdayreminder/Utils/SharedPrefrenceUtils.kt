package com.andromite.birthdayreminder.Utils

import android.app.Activity
import android.content.Context

class SharedPrefrenceUtils {

    fun saveSP(activity: Activity,key : String, value : String) {
        val sharedPref = activity.getSharedPreferences("BirthdaySP",Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(key,value)
            commit()
        }
    }

    fun getSP(activity: Context?, key: String) : String
    {
        val sharedPref = activity!!.getSharedPreferences("BirthdaySP",Context.MODE_PRIVATE)
        val value = sharedPref.getString(key, "0")
        return value!!
    }

}