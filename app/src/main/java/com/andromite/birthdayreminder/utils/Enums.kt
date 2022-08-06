package com.andromite.birthdayreminder.utils

enum class Enums {
    Users,
    Birthdays,


    // FSBirthday enums
    id,
    person_name,
    date,
    event,
    isImportant,
    notes,
    profilePic,

    //Firestore responses
    UPDATE_REQ_SUCCESS,
    UPDATE_REQ_FAILED,
    VIEW_REQ_SUCCESS,
    VIEW_REQ_FAILED,
    ADD_REQ_SUCCESS,
    ADD_REQ_FAILED,
    DELETE_REQ_SUCCESS,
    DELETE_REQ_FAILED,
    PROFILE_PIC_REQ_SUCCESS,
    PROFILE_PIC_REQ_FAILED,
    PROFILE_PIC_DELETE_SUCCESS,
    PROFILE_PIC_DELETE_FAILED,
    CLOUD_REQ_SUCCESS,
    CLOUD_REQ_FAILED,

    // SP
    BirthdaySP,

    // user data
    UserId,
    ProfilePic,
    DocId,

    ADD_BIRTHDAY,
    UPDATE_BIRTHDAY


}