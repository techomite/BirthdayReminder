package com.andromite.birthdayreminder.utils

import android.content.Context
import android.net.Uri
import com.andromite.birthdayreminder.FSBirthday
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class FirebaseCloudStorageUtils {

    // Create a storage reference from our app
    private val storage = FirebaseStorage.getInstance()

    fun uploadProfilePic(context: Context, profileuri: Uri, listener: FirebaseCloudListener) {
        val uid = SP.get(context, Enums.UserId.name)
        // Create a storage reference from our app
        val storageRef: StorageReference = storage.getReference()

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("${profileuri.lastPathSegment}.jpg")

        // Create a reference to 'images/mountains.jpg'
        val mountainImagesRef =
            storageRef.child("${Enums.profilePic.name}/${uid}/${profileuri.lastPathSegment}.jpg")

        // While the file names are the same, the references point to different files
        mountainsRef.name == mountainImagesRef.name // true
        mountainsRef.path == mountainImagesRef.path // false


        val riversRef = storageRef.child("images/${profileuri.lastPathSegment}")
        val uploadTask = riversRef.putFile(profileuri)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads

            Utils.floge("profilePic upload failed")
            listener.cloudResponse(Enums.PROFILE_PIC_REQ_FAILED)
        }.addOnSuccessListener { taskSnapshot ->
            Utils.floge("profilePic uploaded successfully")
            listener.cloudResponse(Enums.PROFILE_PIC_REQ_SUCCESS)
        }
    }

    fun downloadImage(profilePic : String, listener: FirebaseCloudListener){
        val storageRef: StorageReference = storage.getReference()
        storageRef.child("images/$profilePic").downloadUrl
            .addOnSuccessListener {
                listener.cloudResponse(it)
            }
            .addOnFailureListener {
                listener.cloudResponse("")
            }
        }

    fun deleteImage(profilePic: String) {
        if (profilePic != ""){
            val delref = storage.getReferenceFromUrl(profilePic)
            delref.delete()
                .addOnSuccessListener { Utils.flog("Photo successfully deleted!") }
                .addOnFailureListener { e -> Utils.flog("Error deleting photo $e") }
        }
    }


}