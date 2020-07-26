package com.andromite.birthdayreminder.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Birthday::class],
    version = 2
)

abstract class BirthdayDatabase : RoomDatabase() {

    abstract fun getBirthdayDao() : BirthdayDao

    companion object {

        @Volatile private var instance : BirthdayDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context : Context) = instance?: synchronized(LOCK) {
            instance?: buildDatabase(context).also {
                instance = it
            }

        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            BirthdayDatabase::class.java,
            "birthdatabase"
        ).build()


    }

}