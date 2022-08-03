package com.andromite.birthdayreminder.Utils

import android.app.Activity
import android.content.Context

class SP {

    companion object {

        fun save(context: Context, key: String, value: String) {
            val sharedPref =
                context.getSharedPreferences(Enums.BirthdaySP.name, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(key, value)
                commit()
            }
        }

        fun get(context: Context, key: String): String {
            val sharedPref =
                context.getSharedPreferences(Enums.BirthdaySP.name, Context.MODE_PRIVATE)
            val value = sharedPref.getString(key, "0")
            return value!!
        }

    }

}