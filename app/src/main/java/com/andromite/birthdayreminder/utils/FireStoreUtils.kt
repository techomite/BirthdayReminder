package com.andromite.birthdayreminder.utils

import android.content.Context
import android.content.Intent
import com.andromite.birthdayreminder.FSBirthday
import com.andromite.birthdayreminder.activity.MainActivity
import com.andromite.birthdayreminder.adapter.FsimpAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_important.view.*


class FireStoreUtils {

    var db = Firebase.firestore

    fun readAllBirthdays(context: Context, listener: FirestoreListener) {
        val userId = SP.get(context, Enums.UserId.name)
        var birthdayList = mutableListOf<FSBirthday>()

        db.collection(Enums.Users.name).document(userId).collection(Enums.Birthdays.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Utils.flog("firestore response ${document.id} => ${document.data}")

                    convertMapToObject(document.data)?.let { birthdayList.add(it) }
                }
                listener.fireStoreResponse(birthdayList)
            }
            .addOnFailureListener { exception ->
                Utils.flog("Error getting all birthday. $exception")
//                listener.response(birthday)
            }
    }

    fun readImportantBirthdays(context: Context, listener: FirestoreListener) {
        val userId = SP.get(context, Enums.UserId.name)
        var birthdayList = mutableListOf<FSBirthday>()

        db.collection(Enums.Users.name).document(userId).collection(Enums.Birthdays.name).get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    Utils.flog("List of important Birthdays" + doc.data.toString())
                    if (doc.data?.get("isImportant")?.equals("true") == true)
                    convertMapToObject(doc.data)?.let { birthdayList.add(it) }
                }
                listener.fireStoreResponse(birthdayList)
            }
            .addOnFailureListener {
                Utils.flog("Error getting important birthdays. $it")
            }


    }

    fun viewBirthday(context: Context, docId: String, listener: FirestoreListener) {
        val userId = SP.get(context, Enums.UserId.name)

        val docRef = db.collection(Enums.Users.name).document(userId)
                       .collection(Enums.Birthdays.name).document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Utils.floge("DocumentSnapshot data: ${document.data}")
                    val birthday = convertMapToObject(document.data)
                    Utils.floge("view birthday: $birthday")
                    listener.viewResponse(Enums.VIEW_REQ_SUCCESS.name, birthday)
                } else {
                    Utils.floge("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Utils.floge("get failed with $exception")
                listener.viewResponse(Enums.VIEW_REQ_FAILED.name, null)
            }
    }

    fun addBirthday(context: Context, fsBirthday: FSBirthday, listener: FirestoreListener) {
        val userId = SP.get(context, Enums.UserId.name)

        // Create a new user with a first and last name
        val user = hashMapOf(
            Enums.id.name to fsBirthday.id,
            Enums.person_name.name to fsBirthday.person_name,
            Enums.date.name to fsBirthday.date,
            Enums.event.name to fsBirthday.event,
            Enums.isImportant.name to fsBirthday.isImportant,
            Enums.notes.name to fsBirthday.notes,
            Enums.profilePic.name to fsBirthday.profilePic,
        )

// Add a new document with a generated ID
        db.collection(Enums.Users.name).document(userId).collection(Enums.Birthdays.name).document(fsBirthday.id).set(user)
            .addOnSuccessListener {
                listener.fireStoreResponse(Enums.ADD_REQ_SUCCESS)
            }
            .addOnFailureListener{
                listener.fireStoreResponse(Enums.ADD_REQ_FAILED)
            }
    }

    fun updateBirthday(context: Context, docId: String, fsBirthday: FSBirthday, listener: FirestoreListener) {
        val userId = SP.get(context, Enums.UserId.name)
        // Create a new user with a first and last name
        val user = hashMapOf(
            Enums.id.name to fsBirthday.id,
            Enums.person_name.name to fsBirthday.person_name,
            Enums.date.name to fsBirthday.date,
            Enums.event.name to fsBirthday.event,
            Enums.isImportant.name to fsBirthday.isImportant,
            Enums.notes.name to fsBirthday.notes,
            Enums.profilePic.name to fsBirthday.profilePic,
        )

        db.collection(Enums.Users.name).document(userId)
          .collection(Enums.Birthdays.name).document(docId)
            .update(user as Map<String, Any>)
            .addOnSuccessListener {
                Utils.floge("DocumentSnapshot updated")
                listener.fireStoreResponse(Enums.UPDATE_REQ_SUCCESS.name)
            }
            .addOnFailureListener { e ->
                Utils.floge("Error updating document $e")
                listener.fireStoreResponse(Enums.UPDATE_REQ_FAILED.name)
            }
    }

    fun deleteBirthday(context: Context, docId: String, listener: FirestoreListener) {
        val uid = SP.get(context, Enums.UserId.name)
        db.collection(Enums.Users.name).document(uid).collection(Enums.Birthdays.name).document(docId)
            .delete()
            .addOnSuccessListener {
                Utils.floge( "DocumentSnapshot successfully deleted!")
                listener.fireStoreResponse(Enums.DELETE_REQ_SUCCESS.name)
            }
            .addOnFailureListener { e ->
                Utils.floge("Error deleting document $e")
                listener.fireStoreResponse(Enums.DELETE_REQ_FAILED.name)
            }
    }

    fun getUniqueDocId(context: Context): String {
        val uid = SP.get(context, Enums.UserId.name)
        val ref = db.collection(Enums.Users.name).document(uid).collection(Enums.Birthdays.name).document()
        return ref.id
    }

    private fun convertMapToObject(data: MutableMap<String, Any>?): FSBirthday? {
        val gson = Gson()
        val jsonElement: JsonElement = gson.toJsonTree(data)
        return gson.fromJson(jsonElement, FSBirthday::class.java)

    }
}