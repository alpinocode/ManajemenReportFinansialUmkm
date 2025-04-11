package com.example.manajemenreportfinansialumkm.helper

data class UserAuth(
    val id: String,
    val name:String,
    val email:String,
    val imageUrl:String? = null
)