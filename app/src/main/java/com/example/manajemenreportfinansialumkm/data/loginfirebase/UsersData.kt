package com.example.manajemenreportfinansialumkm.data.loginfirebase

import android.os.Parcelable
import android.provider.ContactsContract.CommonDataKinds.Email
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsersData(
    val username:String,
    val email:String,
    val numberPhone:String?= null,
    val photoUrl:String? = null
): Parcelable