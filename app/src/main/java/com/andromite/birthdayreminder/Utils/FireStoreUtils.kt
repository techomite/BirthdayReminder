package com.andromite.birthdayreminder.Utils

import com.andromite.birthdayreminder.FSBirthday
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonElement


class FireStoreUtils {

    var db = Firebase.firestore

    fun readAllBirthdays(listener: FirestoreListener) {

        db.collection(Enums.Users.name).document("userOne").collection(Enums.Birthdays.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Utils.flog("firestore response ${document.id} => ${document.data}")

                    convertMapToObject(document.data)?.let { listener.response(it) }
                }
            }
            .addOnFailureListener { exception ->
                Utils.flog("Error getting documents. $exception")
//                listener.response(birthday)
            }
    }

    fun viewBirthday(docId: String, listener: FirestoreListener) {
        val docRef = db.collection(Enums.Users.name).document("userid")
                       .collection(Enums.Birthdays.name).document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Utils.floge("DocumentSnapshot data: ${document.data}")
                    var birthday = convertMapToObject(document.data)
                    birthday?.let { listener.response(it) }
                } else {
                    Utils.floge("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Utils.floge("get failed with $exception")
            }
    }

    fun addBirthday(fsBirthday: FSBirthday) {
        // Create a new user with a first and last name
        val user = hashMapOf(
            Enums.id.name to fsBirthday.id,
            Enums.person_name.name to fsBirthday.person_name,
            Enums.date.name to fsBirthday.date,
            Enums.events.name to fsBirthday.event,
            Enums.isImportant.name to fsBirthday.isImportant,
            Enums.notes.name to fsBirthday.notes,
            Enums.profilePic.name to fsBirthday.profilePic,
        )

// Add a new document with a generated ID
        db.collection(Enums.Users.name).document("userOne").collection(Enums.Birthdays.name)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Utils.floge("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Utils.floge("Error adding document $e")
            }
    }

    fun convertMapToObject(data: MutableMap<String, Any>?): FSBirthday? {
        var birthday = FSBirthday("","","","","","","")
        val gson = Gson()
        val jsonElement: JsonElement = gson.toJsonTree(data)
        return gson.fromJson(jsonElement, FSBirthday::class.java)

    }
}