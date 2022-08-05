package com.andromite.birthdayreminder.utils

interface FirebaseCloudListener {
    abstract fun cloudResponse(response: Any)
}