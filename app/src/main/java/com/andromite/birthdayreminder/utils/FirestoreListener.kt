package com.andromite.birthdayreminder.utils

import com.andromite.birthdayreminder.FSBirthday

interface FirestoreListener {
    abstract fun fireStoreResponse(response : Any)
    abstract fun viewResponse(name: String, birthday: FSBirthday?)
}