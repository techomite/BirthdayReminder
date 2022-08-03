package com.andromite.birthdayreminder.Utils

import com.andromite.birthdayreminder.FSBirthday

interface FirestoreListener {
    abstract fun response(response : Any)
}