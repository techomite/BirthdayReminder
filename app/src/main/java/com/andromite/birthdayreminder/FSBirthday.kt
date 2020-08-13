package com.andromite.birthdayreminder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FSBirthday(val id: String,
                      val person_name : String,
                      val date : String,
                      val event : String,
                      val isImportant : String,
                      val notes : String,
                      val profilePic : String) : Parcelable
