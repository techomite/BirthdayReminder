package com.andromite.birthdayreminder.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BirthdayDao {

//    @Insert
//    suspend fun addBirthday(birthday : Birthday)

//    @Query("SELECT * FROM birthday ORDER BY id DESC" )
//    suspend fun getAllBirthday() : List<Birthday>
//
//    @Query("UPDATE birthday SET person_name=:personName, date=:date, event=:event, isImportant=:important, notes=:notes, profilePic=:profilePic WHERE id=:id")
//    suspend fun updateBirthday(
//        personName: kotlin.String,
//        date: kotlin.String,
//        event: kotlin.Int,
//        important: kotlin.Boolean,
//        notes: kotlin.String,
//        profilePic: kotlin.ByteArray?,
//        id: kotlin.Int
//    )

}