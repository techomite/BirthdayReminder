package com.andromite.birthdayreminder.Utils

import android.util.Log

class Utils {

    companion object{
        fun flog(value: Any) {
            if (Constants.ENABLE_LOGS)
            Log.i("BR", ":-: $value :-:")
        }

        fun floge(value: Any) {
            if (Constants.ENABLE_LOGS)
                Log.e("asdfasdf", ":-: $value :-:")
        }
    }
}