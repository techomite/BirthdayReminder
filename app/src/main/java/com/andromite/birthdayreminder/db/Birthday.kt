package com.andromite.birthdayreminder.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Birthday(
    val person_name : String,
    val date : String,
    val event : Int,
    val isImportant : Boolean,
    val notes : String,
    val profilePic : ByteArray?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}